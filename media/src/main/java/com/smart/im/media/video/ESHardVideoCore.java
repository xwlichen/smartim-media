package com.smart.im.media.video;

import android.opengl.GLES20;
import android.os.Handler;
import android.os.Message;

import com.smart.im.media.bean.ESConfig;
import com.smart.im.media.bean.PushConfig;
import com.smart.im.media.bean.OffScreenGLWapper;
import com.smart.im.media.utils.GLHelper;

/**
 * @date : 2019/4/9 下午3:40
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class ESHardVideoCore implements ESVideoCore {

    private PushConfig pushConfig;
    private OffScreenGLWapper offScreenGLWapper;


    public ESHardVideoCore(PushConfig pushConfig) {
        this.pushConfig = pushConfig;
    }

    @Override
    public void prepare() {
    }


    private class VideoGLHandler extends Handler {
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

        private int sample2DFrameBuffer;
        private int sample2DFrameBufferTexture;
        private int frameBuffer;
        private int frameBufferTexture;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_INIT:
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
                GLHelper.createCamFrameBuff(fb, fbt, pushConfig.getPreviewWidth(), pushConfig.getPreviewHeight());//pushConfig.videoWidth, pushConfig.videoHeight
                sample2DFrameBuffer = fb[0];
                sample2DFrameBufferTexture = fbt[0];
                GLHelper.createCamFrameBuff(fb, fbt, pushConfig.getPreviewWidth(), pushConfig.getPreviewHeight());//pushConfig.videoWidth, pushConfig.videoHeight
                frameBuffer = fb[0];
                frameBufferTexture = fbt[0];
            }

        }
    }

}
