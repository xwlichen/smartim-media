package com.smart.im.media.push;

import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.smart.im.media.bean.VideoParam;
import com.smart.im.media.bridge.LiveBridge;
import com.smart.im.media.utils.CameraUtil;

/**
 * @date : 2019/3/12 下午3:52
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class VideoPusher extends Pusher implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private SurfaceView surfaceView;
    private VideoParam videoParam;
    private Camera camera;
    private boolean isPushing;

    private CameraUtil cameraUtil;
    private LiveBridge liveBridge;

    public VideoPusher(SurfaceView surfaceView, VideoParam videoParam, LiveBridge liveBridge) {
        this.surfaceView = surfaceView;
        this.videoParam = videoParam;
        this.liveBridge = liveBridge;

        cameraUtil = new CameraUtil(surfaceView.getContext());
        surfaceView.getHolder().addCallback(this);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

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
    public void prepare() {
        cameraUtil.initCamera(cameraUtil.getCurrentType());
        cameraUtil.startPreview(surfaceView.getHolder(), this);
    }

    @Override
    public void startPush() {

    }

    @Override
    public void stopPush() {

    }

    @Override
    public void release() {

    }
}
