package com.smart.im.media.push;

import android.content.Context;
import android.view.SurfaceView;
import android.view.TextureView;

import com.smart.im.media.bean.PushConfig;
import com.smart.im.media.bridge.LiveBridge;
import com.smart.im.media.filter.BaseHardVideoFilter;

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

    private PushConfig config;
    private Context context;


    public SmartLivePusher() {

    }


    @Override
    public void init(Context context, PushConfig config) {
        this.config = config;
        this.context = context;

        liveBridge = new LiveBridge();
        videoPusher = new VideoPusher(liveBridge);
        audioPusher = new AudioPusher(liveBridge);

        liveBridge.initLivePushConfig(config);
        videoPusher.init(context, config);
        audioPusher.init(context, config);
    }


    public void setHardVideoFileter(BaseHardVideoFilter hardVideoFilter) {
        videoPusher.setHardVideoFileter(hardVideoFilter);
    }


    public void startPreview(TextureView textureView) {
        videoPusher.startPreview(textureView);
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

    @Override
    public void destroy() {

    }


}
