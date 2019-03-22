package com.smart.im.media.push;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.smart.im.media.bean.LivePushConfig;
import com.smart.im.media.bridge.LiveBridge;
import com.smart.im.media.utils.CameraUtil;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @date : 2019/3/12 下午3:52
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class VideoPusher implements ILivePusher, SurfaceHolder.Callback, Camera.PreviewCallback {

    private SurfaceView surfaceView;
    private Camera camera;
    private boolean isPushing = false;

    private CameraUtil cameraUtil;
    private LiveBridge liveBridge;




    public VideoPusher(LiveBridge liveBridge) {
        this.liveBridge = liveBridge;
        cameraUtil = new CameraUtil();

    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (isPushing) {
            if (data!=null) {
                liveBridge.pushVideoData(data);
            }

//            if (data != null) {
//                try {
//                    mQueue.put(data);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        cameraUtil.startPreview(surfaceView.getHolder(), this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    @Override
    public void init(Context context, LivePushConfig livePushConfig) {

    }

    @Override
    public void destroy() {

    }

    public void startPreview(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;
        cameraUtil.setContext(surfaceView.getContext());
        cameraUtil.initCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        cameraUtil.startPreview(surfaceView.getHolder(), this);
        surfaceView.getHolder().addCallback(this);
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




}
