package com.smart.im.media.push;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import com.smart.im.media.bean.LivePushConfig;
import com.smart.im.media.bridge.LiveBridge;
import com.smart.im.media.utils.CameraUtil;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @date : 2019/3/12 下午3:52
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class VideoPusher implements ILivePusher {

    private SurfaceView surfaceView;
    private TextureView textureView;
    private boolean isPushing = false;

    private CameraUtil cameraUtil;
    private LiveBridge liveBridge;

    //阻塞线程安全队列，生产者和消费者
    private LinkedBlockingQueue<byte[]> mQueue = new LinkedBlockingQueue<>();
    private Thread workThread;


    public VideoPusher(LiveBridge liveBridge) {
        this.liveBridge = liveBridge;
        cameraUtil = new CameraUtil();
//        initWorkThread();
//        workThread.start();

    }




    @Override
    public void init(Context context, LivePushConfig livePushConfig) {

    }

    @Override
    public void destroy() {

    }

    public void startPreview(View view) {

        cameraUtil.setContext(view.getContext());
        cameraUtil.initCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);

        if (view instanceof SurfaceView) {
            this.surfaceView = (SurfaceView) view;
            this.surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {

                }
            });
        } else if (view instanceof TextureView) {
            this.textureView = (TextureView) view;
            this.textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {

                }
            });

        }

        cameraUtil.startPreview(surfaceView.getHolder(), new );

    }


    public void startPreviewAysnc(SurfaceView surfaceView) {

    }

    public void stopPreview() {

    }

    @Override
    public void startPush() {
        this.isPushing = true;

    }

    @Override
    public void startPushAysnc() {

    }

    @Override
    public void restartPush() {

    }

    @Override
    public void restartPushAync() {

    }

    @Override
    public void reconnectPushAsync(String url) {

    }

    @Override
    public void stopPush() {
        this.isPushing = false;

    }


    public void initWorkThread() {
        if (workThread == null) {
            workThread = new Thread() {
                @Override
                public void run() {
                    try {
                        if (isPushing) {
                            byte[] data = mQueue.take();
                            liveBridge.pushVideoData(data);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            };
        }
    }


//
//    if (isPushing) {
//        if (data != null) {
//
//            liveBridge.pushVideoData(data);
//
//
//        }
//
//    }

}
