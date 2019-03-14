package com.smart.im.media.push;

import android.content.Context;
import android.view.SurfaceView;

import com.smart.im.media.bean.LivePushConfig;
import com.smart.im.media.bridge.LiveBridge;

/**
 * @date : 2019/3/12 下午4:03
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class SmartLivePusher implements ILivePusher {

    protected VideoPusher videoPusher;
    protected AudioPusher audioPusher;
    protected LiveBridge liveBridge;


    public SmartLivePusher() {
        liveBridge = new LiveBridge();
        videoPusher = new VideoPusher(liveBridge);
        audioPusher = new AudioPusher();
    }


    @Override
    public void init(Context context, LivePushConfig livePushConfig) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void startPreview(SurfaceView surfaceView) {
        videoPusher.startPreview(surfaceView);
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
        videoPusher.stopPush();
        audioPusher.stopPush();
    }


}
