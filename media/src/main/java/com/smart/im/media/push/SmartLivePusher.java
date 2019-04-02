package com.smart.im.media.push;

import android.content.Context;
import android.view.SurfaceView;
import android.view.View;

import com.smart.im.media.bean.LivePushConfig;
import com.smart.im.media.bridge.LiveBridge;

/**
 * @date : 2019/3/12 下午4:03
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class SmartLivePusher implements ILivePusher {

    private VideoPusher videoPusher;
    private AudioPusher audioPusher;
    private LiveBridge liveBridge;

    private LivePushConfig config;
    private Context context;


    public SmartLivePusher() {

    }


    @Override
    public void init(Context context, LivePushConfig config) {
        this.config = config;
        this.context = context;

        liveBridge = new LiveBridge();
//        videoPusher = new VideoPusher(liveBridge);
        audioPusher = new AudioPusher(liveBridge);

        liveBridge.initLivePushConfig(config);
    }

    @Override
    public void destroy() {

    }

    public void startPreview(View surfaceView) {
        videoPusher.startPreview(surfaceView);
    }

    public void startPreviewAysnc(SurfaceView surfaceView) {

    }

    public void stopPreview() {

    }

    @Override
    public void startPush() {
        liveBridge.initRtmp(config.getUrl());
        videoPusher.startPush();
//        audioPusher.startPush();

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
        videoPusher.stopPush();
//        audioPusher.stopPush();
    }


}
