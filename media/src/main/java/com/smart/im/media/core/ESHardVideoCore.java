package com.smart.im.media.core;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.opengl.EGL14;
import android.opengl.EGLExt;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.smart.im.media.bean.MediaCodecGLWapper;
import com.smart.im.media.bean.PushConfig;
import com.smart.im.media.bean.OffScreenGLWapper;
import com.smart.im.media.bean.RESFrameRateMeter;
import com.smart.im.media.bean.ScreenGLWapper;
import com.smart.im.media.bean.Size;
import com.smart.im.media.encoder.MediaVideoEncoder;
import com.smart.im.media.utils.GLHelper;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.smart.im.media.enums.DirectionEnum.ROTATION_0;
import static com.smart.im.media.enums.DirectionEnum.ROTATION_90;

/**
 * @date : 2019/4/9 下午3:40
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class ESHardVideoCore implements ESVideoCore {

    private final Object syncObj = new Object();
    private final Object syncIsLooping = new Object();


    private PushConfig pushConfig;
    private OffScreenGLWapper offScreenGLWapper;

    private MediaCodec dstVideoEncoder;
    private MediaFormat dstVideoFormat;
    private HandlerThread videoGLHandlerThread;
    private VideoGLHandler videoGLHander;


    private Lock lockVideoFilter = null;
    //encoder mp4 start
    private MediaVideoEncoder mVideoEncoder;
    private boolean mNeedResetEglContext = true;
    private int mCameraId = -1;


    private boolean isPreviewing = false;
    private boolean isStreaming = false;
    private boolean hasNewFrame = false;

    /**
     * 发送间隔，根据pushconfig的fps设置
     */
    private int loopingInterval;


    public ESHardVideoCore(PushConfig pushConfig) {
        this.pushConfig = pushConfig;
        lockVideoFilter = new ReentrantLock(false);
    }

    @Override
    public boolean prepare() {
        if (pushConfig == null) {
            LogUtils.e("ESHardVideoCore's pushConfig is null");
            return false;
        }
        loopingInterval = 1000 / pushConfig.getFps().getValue();

        dstVideoFormat = new MediaFormat();
        videoGLHandlerThread = new HandlerThread("GLThread");
        videoGLHandlerThread.start();
        videoGLHander = new VideoGLHandler(videoGLHandlerThread.getLooper());
        videoGLHander.sendEmptyMessage(VideoGLHandler.WHAT_INIT);
        return true;
    }

    @Override
    public void startPreview(SurfaceTexture surfaceTexture, int visualWidth, int visualHeight) {
        synchronized (syncObj) {
            videoGLHander.sendMessage(videoGLHander.obtainMessage(VideoGLHandler.WHAT_START_PREVIEW,
                    visualWidth, visualHeight, surfaceTexture));
            synchronized (syncIsLooping) {
                if (!isPreviewing && !isStreaming) {
                    videoGLHander.removeMessages(VideoGLHandler.WHAT_DRAW);
                    videoGLHander.sendMessageDelayed(videoGLHander.obtainMessage(VideoGLHandler.WHAT_DRAW, SystemClock.uptimeMillis() + loopingInterval), loopingInterval);
                }
                isPreviewing = true;
            }
        }
    }


    public void onFrameAvailable() {
        if (videoGLHandlerThread != null) {
            videoGLHander.addFrameNum();
        }
    }

    @Override
    public void updateCamTexture(SurfaceTexture camTex) {
        synchronized (syncObj) {
            if (videoGLHander != null) {
                videoGLHander.updateCamTexture(camTex);
            }
        }
    }


    private class VideoGLHandler extends Handler {

        private final Object syncFrameNum = new Object();
        private final Object syncCameraTex = new Object();
        private final Object syncCameraTextureVerticesBuffer = new Object();



        static final int WHAT_INIT = 0x001;
        static final int WHAT_UNINIT = 0x002;
        static final int WHAT_FRAME = 0x003;
        static final int WHAT_DRAW = 0x004;
        static final int WHAT_RESET_VIDEO = 0x005;
        static final int WHAT_START_PREVIEW = 0x010;
        static final int WHAT_STOP_PREVIEW = 0x020;
        static final int WHAT_START_STREAMING = 0x100;
        static final int WHAT_STOP_STREAMING = 0x200;
        static final int WHAT_RESET_BITRATE = 0x300;

        private int frameNum = 0;
        public boolean dropNextFrame = false;
        private SurfaceTexture cameraTexture;
        private SurfaceTexture screenTexture;
        private Size screenSize;

        private MediaCodecGLWapper mediaCodecGLWapper;
        private ScreenGLWapper screenGLWapper;
        private OffScreenGLWapper offScreenGLWapper;

        private int sample2DFrameBuffer;
        private int sample2DFrameBufferTexture;
        private int frameBuffer;
        private int frameBufferTexture;

        private FloatBuffer mediaCodecTextureVerticesBuffer;
        private FloatBuffer screenTextureVerticesBuffer;
        private FloatBuffer camera2dTextureVerticesBuffer;
        private FloatBuffer cameraTextureVerticesBuffer;
        private FloatBuffer shapeVerticesBuffer;
        private ShortBuffer drawIndecesBuffer;

        float[] textureMatrix; //纹理单元


        public static final int FILTER_LOCK_TOLERATION = 3;//3ms

        private RESFrameRateMeter drawFrameRateMeter;



        public VideoGLHandler(Looper looper) {
            super(looper);
            screenGLWapper = null;
            mediaCodecGLWapper = null;
            drawFrameRateMeter = new RESFrameRateMeter();
            screenSize = new Size(1, 1);
            initBuffer();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_INIT:
                    initOffScreenGL();
                    break;
                case WHAT_START_PREVIEW:
                    initScreenGL((SurfaceTexture) msg.obj);
                    updatePreview(msg.arg1, msg.arg2);
                    break;
                case WHAT_FRAME:
                    GLHelper.makeCurrent(offScreenGLWapper);
                    synchronized (syncFrameNum) {
                        synchronized (syncCameraTex) {
                            if (cameraTexture != null) {
                                while (frameNum != 0) {
                                    //从image stream(也就是作为producer的Surface）取出最近的帧来更新材质图像（就是构造中传给SurfaceTexture的texName）
                                    //首先获取最近帧的buffer，接着释放上次获取的buffer，最后一步最关键将buffer绑定到材质。
                                    cameraTexture.updateTexImage();
                                    --frameNum;
                                    if (!dropNextFrame) {
                                        hasNewFrame = true;
                                    } else {
                                        dropNextFrame = false;
                                        hasNewFrame = false;
                                    }
                                }
                            } else {
                                break;
                            }
                        }
                    }
                    drawSample2DFrameBuffer(cameraTexture);
                    break;
                case WHAT_DRAW:
                    long time = (Long) msg.obj;
                    long interval = time + loopingInterval - SystemClock.uptimeMillis();
                    synchronized (syncIsLooping) {
                        if (isPreviewing || isStreaming) {
                            if (interval > 0) {
                                videoGLHander.sendMessageDelayed(videoGLHander.obtainMessage(
                                        VideoGLHandler.WHAT_DRAW,
                                        SystemClock.uptimeMillis() + interval),
                                        interval);
                            } else {
                                videoGLHander.sendMessage(videoGLHander.obtainMessage(
                                        VideoGLHandler.WHAT_DRAW,
                                        SystemClock.uptimeMillis() + loopingInterval));

                            }
                        }
                    }

                    LogUtils.e("hasNewFrame:",hasNewFrame);
                    if (hasNewFrame) {
                        drawFrameBuffer();
//                        drawMediaCodec(time * 1000000);
                        drawScreen();
//                        encoderMp4(frameBufferTexture);//编码MP4
                        drawFrameRateMeter.count();
                        hasNewFrame = false;
                    }
                    break;
                default:
                    break;
            }
        }


        /**
         * 初始化离屏渲染
         */
        private void initOffScreenGL() {
            if (offScreenGLWapper == null) {
                offScreenGLWapper = new OffScreenGLWapper();
                //离屏渲染初始化-EGLSurface创建
                GLHelper.initOffScreenGL(offScreenGLWapper);
                // 设置默认的上下文环境和输出缓冲区(小米4上如果不设置有效的eglSurface后面创建着色器会失败，可以先创建一个默认的eglSurface)
                GLHelper.makeCurrent(offScreenGLWapper);

                //创建camera program
                offScreenGLWapper.camProgram = GLHelper.createCameraProgram();
                //使用camera program
                GLES20.glUseProgram(offScreenGLWapper.camProgram);
                //根据属性名获取这个程序的属性值
                offScreenGLWapper.camTextureLoc = GLES20.glGetUniformLocation(offScreenGLWapper.camProgram, "uTexture");
                offScreenGLWapper.camPostionLoc = GLES20.glGetAttribLocation(offScreenGLWapper.camProgram, "aPosition");
                offScreenGLWapper.camTextureCoordLoc = GLES20.glGetAttribLocation(offScreenGLWapper.camProgram, "aTextureCoord");

                //camera2d
                offScreenGLWapper.cam2dProgram = GLHelper.createCamera2DProgram();
                GLES20.glUseProgram(offScreenGLWapper.cam2dProgram);
                offScreenGLWapper.cam2dTextureLoc = GLES20.glGetUniformLocation(offScreenGLWapper.cam2dProgram, "uTexture");
                offScreenGLWapper.cam2dPostionLoc = GLES20.glGetAttribLocation(offScreenGLWapper.cam2dProgram, "aPosition");
                offScreenGLWapper.cam2dTextureCoordLoc = GLES20.glGetAttribLocation(offScreenGLWapper.cam2dProgram, "aTextureCoord");
                offScreenGLWapper.cam2dTextureMatrix = GLES20.glGetUniformLocation(offScreenGLWapper.cam2dProgram, "uTextureMatrix");

                //opengl 帧缓冲的创建
                int[] fb = new int[1], fbt = new int[1];
                GLHelper.createCamFrameBuff(fb, fbt, pushConfig.getPreviewHeight(), pushConfig.getPreviewWidth());//pushConfig.videoWidth, pushConfig.videoHeight
//                GLHelper.createCamFrameBuff(fb, fbt, pushConfig.getPreviewWidth(), pushConfig.getPreviewHeight());//pushConfig.videoWidth, pushConfig.videoHeight

                sample2DFrameBuffer = fb[0];
                sample2DFrameBufferTexture = fbt[0];
                GLHelper.createCamFrameBuff(fb, fbt, pushConfig.getPreviewHeight(), pushConfig.getPreviewWidth());//pushConfig.videoWidth, pushConfig.videoHeight
                frameBuffer = fb[0];
                frameBufferTexture = fbt[0];
            }

        }


        /**
         * 初始化屏幕的渲染
         *
         * @param screenSurfaceTexture
         */
        private void initScreenGL(SurfaceTexture screenSurfaceTexture) {
            if (screenGLWapper == null) {
                screenTexture = screenSurfaceTexture;
                screenGLWapper = new ScreenGLWapper();
                GLHelper.initScreenGL(screenGLWapper, offScreenGLWapper.eglContext, screenSurfaceTexture);
                GLHelper.makeCurrent(screenGLWapper);
                screenGLWapper.drawProgram = GLHelper.createScreenProgram();
                GLES20.glUseProgram(screenGLWapper.drawProgram);
                screenGLWapper.drawTextureLoc = GLES20.glGetUniformLocation(screenGLWapper.drawProgram, "uTexture");
                screenGLWapper.drawPostionLoc = GLES20.glGetAttribLocation(screenGLWapper.drawProgram, "aPosition");
                screenGLWapper.drawTextureCoordLoc = GLES20.glGetAttribLocation(screenGLWapper.drawProgram, "aTextureCoord");
            } else {
                throw new IllegalStateException("initScreenGL without unInitScreenGL");
            }
        }


        /**
         * 更新预览界面的尺寸
         * @param w
         * @param h
         */
        public void updatePreview(int w, int h) {
            screenSize = new Size(w, h);
        }


        /**
         * 更新ESVideoClient传过来的SurfaceTexture
         * @param surfaceTexture
         */
        public void updateCamTexture(SurfaceTexture surfaceTexture) {
            synchronized (syncCameraTex) {
                if (surfaceTexture != cameraTexture) {
                    cameraTexture = surfaceTexture;
                    frameNum = 0;
                    dropNextFrame = true;
                }
            }
        }


        public void addFrameNum() {
            synchronized (syncFrameNum) {
                ++frameNum;
                this.removeMessages(WHAT_FRAME);
                this.sendMessageAtFrontOfQueue(this.obtainMessage(VideoGLHandler.WHAT_FRAME));
            }
        }

        /**
         * 绘制2d 帧缓冲数据
         * @param cameraTexture
         */
        private void drawSample2DFrameBuffer(SurfaceTexture cameraTexture) {
            if(pushConfig.isPreviewMirror()||pushConfig.isPushMirror()){
                screenTextureVerticesBuffer = GLHelper.adjustTextureFlip(pushConfig.isPreviewMirror());
                mediaCodecTextureVerticesBuffer = GLHelper.adjustTextureFlip(pushConfig.isPushMirror());
            }

            //绑定frambuffer
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, sample2DFrameBuffer);
            //使用离屏渲染程序
            GLES20.glUseProgram(offScreenGLWapper.cam2dProgram);
            //选择活跃纹理单元0
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            //绑定纹理，设置类型 纹理帮定的目标(target)并不是通常的GL_TEXTURE_2D，而是GL_TEXTURE_EXTERNAL_OES,
            // 这是因为Camera使用的输出texture是一种特殊的格式。同样的，在shader中我们也必须使用SamperExternalOES 的变量类型来访问该纹理。
            //GL_TEXTURE_EXTERNAL_OES 实际上就是两个OpenGL Thread共享一个Texture，不再需要数据导入导出，从Camera采集的数据直接在GPU中完成转换和渲染
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, OVERWATCH_TEXTURE_ID);
            //将离屏程序的片元着色器uniform的变量设置为0
            GLES20.glUniform1i(offScreenGLWapper.cam2dTextureLoc, 0);
            synchronized (syncCameraTextureVerticesBuffer) {
                //顶点坐标、纹理坐标，赋值     shapeVerticesBuffer顶点坐标的绘制限制，camera2dTextureVerticesBuffer纹理坐标的绘制限制
                GLHelper.enableVertex(offScreenGLWapper.cam2dPostionLoc, offScreenGLWapper.cam2dTextureCoordLoc,
                        shapeVerticesBuffer, camera2dTextureVerticesBuffer);
            }
            //OpenGl最多支持16个纹理单元
            textureMatrix = new float[16];
            //得到SurfaceTexture的变换矩阵(缩放、平移等)
            cameraTexture.getTransformMatrix(textureMatrix);
            //encoder mp4 start
            //processStMatrix(textureMatrix, mCameraID == Camera.CameraInfo.CAMERA_FACING_FRONT);

            /**将矩阵传入camera2d程序中的顶点着色器的矩阵常量
             * location-坐标
             * count-矩阵数量
             * transpose-是否转置
             * 矩阵
             */

            GLES20.glUniformMatrix4fv(offScreenGLWapper.cam2dTextureMatrix, 1, false, textureMatrix, 0);
            //设置视口大小
            GLES20.glViewport(0, 0, pushConfig.getPreviewHeight(), pushConfig.getPreviewWidth());//resCoreParameters.videoWidth, resCoreParameters.videoHeight
            doGLDraw();
            //通知OpenGL渲染管线阻塞执行前面所有的指令
            GLES20.glFinish();
            GLHelper.disableVertex(offScreenGLWapper.cam2dPostionLoc, offScreenGLWapper.cam2dTextureCoordLoc);
            //解绑纹理
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
            //解除使用程序
            GLES20.glUseProgram(0);
            //解除帧缓冲
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        }

        private void doGLDraw() {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            /**从数组数据中渲染图元
             * mode 指定要渲染的图元类型
             * count 指定要渲染的元素数
             * type 指定indices中值的类型
             * indeices 指定指向存储索引的位置的指针
             */
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawIndecesBuffer.limit(), GLES20.GL_UNSIGNED_SHORT, drawIndecesBuffer);
        }


        private void initBuffer() {
            shapeVerticesBuffer = GLHelper.getShapeVerticesBuffer();
            mediaCodecTextureVerticesBuffer = GLHelper.getMediaCodecTextureVerticesBuffer();
            screenTextureVerticesBuffer = GLHelper.getScreenTextureVerticesBuffer();
            updateCameraIndex(1);
            drawIndecesBuffer = GLHelper.getDrawIndecesBuffer();
            cameraTextureVerticesBuffer = GLHelper.getCameraTextureVerticesBuffer();
        }

        public void updateCameraIndex(int cameraIndex) {
            synchronized (syncCameraTextureVerticesBuffer) {
//                currCamera = cameraIndex;
//                if (currCamera == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//                    directionFlag = resCoreParameters.frontCameraDirectionMode ^ RESConfig.DirectionMode.FLAG_DIRECTION_FLIP_HORIZONTAL;
//                } else {
//                    directionFlag = resCoreParameters.backCameraDirectionMode;
//                }
                camera2dTextureVerticesBuffer = GLHelper.getCamera2DTextureVerticesBuffer(ROTATION_90, 0.0f);
            }
        }





        private void drawFrameBuffer() {
            GLHelper.makeCurrent(offScreenGLWapper);
//            boolean isFilterLocked = lockVideoFilter();
            long starttime = System.currentTimeMillis();
//            if (isFilterLocked) {
//                if (videoFilter != innerVideoFilter) {
//                    if (innerVideoFilter != null) {
//                        innerVideoFilter.onDestroy();
//                    }
//                    innerVideoFilter = videoFilter;
//                    if (innerVideoFilter != null) {
//                        innerVideoFilter.onInit(resCoreParameters.pre·viewVideoHeight, resCoreParameters.previewVideoWidth);//resCoreParameters.videoWidth, resCoreParameters.videoHeight
//                    }
//                }
//                if (innerVideoFilter != null) {
//                    synchronized (syncCameraTextureVerticesBuffer) {
//                        innerVideoFilter.onDirectionUpdate(directionFlag);
//                        innerVideoFilter.onDraw(sample2DFrameBufferTexture, frameBuffer, shapeVerticesBuffer, cameraTextureVerticesBuffer);
//                    }
//                } else {
//                    drawOriginFrameBuffer();
//                }
//                unlockVideoFilter();
//            } else {
                drawOriginFrameBuffer();
//            }
            LogUtils.d("滤镜耗时："+(System.currentTimeMillis()-starttime));
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        }

        private void drawMediaCodec(long currTime) {
            if (mediaCodecGLWapper != null) {
                GLHelper.makeCurrent(mediaCodecGLWapper);
                GLES20.glUseProgram(mediaCodecGLWapper.drawProgram);
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferTexture);
                GLES20.glUniform1i(mediaCodecGLWapper.drawTextureLoc, 0);
                GLHelper.enableVertex(mediaCodecGLWapper.drawPostionLoc, mediaCodecGLWapper.drawTextureCoordLoc,
                        shapeVerticesBuffer, mediaCodecTextureVerticesBuffer);
                doGLDraw();
                GLES20.glFinish();
                GLHelper.disableVertex(mediaCodecGLWapper.drawPostionLoc, mediaCodecGLWapper.drawTextureCoordLoc);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                GLES20.glUseProgram(0);
                EGLExt.eglPresentationTimeANDROID(mediaCodecGLWapper.eglDisplay, mediaCodecGLWapper.eglSurface, currTime);
                if (!EGL14.eglSwapBuffers(mediaCodecGLWapper.eglDisplay, mediaCodecGLWapper.eglSurface)) {
                    throw new RuntimeException("eglSwapBuffers,failed!");
                }
            }
        }

        private void drawOriginFrameBuffer() {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);
            GLES20.glUseProgram(offScreenGLWapper.camProgram);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, sample2DFrameBufferTexture);
            GLES20.glUniform1i(offScreenGLWapper.camTextureLoc, 0);
            synchronized (syncCameraTextureVerticesBuffer) {
                GLHelper.enableVertex(offScreenGLWapper.camPostionLoc, offScreenGLWapper.camTextureCoordLoc,
                        shapeVerticesBuffer, cameraTextureVerticesBuffer);
            }
            GLES20.glViewport(0, 0, pushConfig.getPreviewHeight(), pushConfig.getPreviewWidth());
//            GLES20.glViewport(0, 0, pushConfig.getPreviewWidth(), pushConfig.getPreviewHeight());

            doGLDraw();
            GLES20.glFinish();
            GLHelper.disableVertex(offScreenGLWapper.camPostionLoc, offScreenGLWapper.camTextureCoordLoc);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glUseProgram(0);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        }

        private void drawScreen() {
            if (screenGLWapper != null) {
                GLHelper.makeCurrent(screenGLWapper);
                GLES20.glUseProgram(screenGLWapper.drawProgram);
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferTexture);
                GLES20.glUniform1i(screenGLWapper.drawTextureLoc, 0);
                GLHelper.enableVertex(screenGLWapper.drawPostionLoc, screenGLWapper.drawTextureCoordLoc,
                        shapeVerticesBuffer, screenTextureVerticesBuffer);
                GLES20.glViewport(0, 0, screenSize.getWidth(), screenSize.getHeight());
                doGLDraw();
                GLES20.glFinish();
                GLHelper.disableVertex(screenGLWapper.drawPostionLoc, screenGLWapper.drawTextureCoordLoc);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                GLES20.glUseProgram(0);
                if (!EGL14.eglSwapBuffers(screenGLWapper.eglDisplay, screenGLWapper.eglSurface)) {
                    throw new RuntimeException("eglSwapBuffers,failed!");
                }
            }
        }


        private boolean lockVideoFilter() {
            try {
                return lockVideoFilter.tryLock(FILTER_LOCK_TOLERATION, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                return false;
            }
        }

        private void unlockVideoFilter() {
            lockVideoFilter.unlock();
        }
        public int getBufferTexture(){
            return frameBufferTexture;
        }

        private void encoderMp4(int BufferTexture) {
            synchronized (this) {
                if (mVideoEncoder != null) {
                    processStMatrix(textureMatrix, mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT);
                    if (mNeedResetEglContext) {
                        mVideoEncoder.setEglContext(EGL14.eglGetCurrentContext(), videoGLHander.getBufferTexture());
                        mNeedResetEglContext = false;
                    }
                    mVideoEncoder.setPreviewWH(pushConfig.getPreviewHeight(), pushConfig.getPreviewWidth());
                    mVideoEncoder.frameAvailableSoon(textureMatrix, mVideoEncoder.getMvpMatrix());
                }
            }
        }

    }


    public void setVideoEncoder(final MediaVideoEncoder encoder) {
        synchronized (this) {
            if (encoder != null) {
                encoder.setEglContext(EGL14.eglGetCurrentContext(), videoGLHander.getBufferTexture());
            }
            mVideoEncoder = encoder;
        }
    }

    private void processStMatrix(float[] matrix, boolean needMirror) {
        if (needMirror && matrix != null && matrix.length == 16) {
            for (int i = 0; i < 3; i++) {
                matrix[4 * i] = -matrix[4 * i];
            }

            if (matrix[4 * 3] == 0) {
                matrix[4 * 3] = 1.0f;
            } else if (matrix[4 * 3] == 1.0f) {
                matrix[4 * 3] = 0f;
            }
        }

        return;
    }

}
