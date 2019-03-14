package com.smart.im.media.push;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.smart.im.media.bean.LivePushConfig;
import com.smart.im.media.bridge.LiveBridge;
import com.smart.im.media.utils.CameraUtil;

/**
 * @date : 2019/3/12 下午3:52
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class VideoPusher implements ILivePusher, SurfaceHolder.Callback, Camera.PreviewCallback {

    private SurfaceView surfaceView;
    private Camera camera;
    private boolean isPushing;

    private CameraUtil cameraUtil;
    private LiveBridge liveBridge;

    public VideoPusher(LiveBridge liveBridge) {
        this.liveBridge = liveBridge;
        cameraUtil = new CameraUtil();
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
    public void init(Context context, LivePushConfig livePushConfig) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void startPreview(SurfaceView surfaceView) {
        cameraUtil.initCamera(cameraUtil.getCurrentType());
        cameraUtil.startPreview(surfaceView.getHolder(), this);
        cameraUtil = new CameraUtil(surfaceView.getContext());
        surfaceView.getHolder().addCallback(this);
    }

    @Override
    public void startPreviewAysnc(SurfaceView surfaceView) {

    }

    @Override
    public void stopPreview() {

    }

    @Override
    public void startPush(String url) {

    }

    @Override
    public void startPushAysnc(String url) {

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

    }


}
