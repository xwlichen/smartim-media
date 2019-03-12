package com.smart.im.media.push;

import android.view.SurfaceView;

import com.smart.im.media.bean.AudioParam;
import com.smart.im.media.bean.VideoParam;
import com.smart.im.media.bridge.LiveBridge;

/**
 * @date : 2019/3/12 下午4:03
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class LivePusher extends Pusher {

    protected VideoPusher videoPusher;
    protected AudioPusher audioPusher;
    protected LiveBridge liveBridge;


    public LivePusher(SurfaceView surfaceView, VideoParam videoParam, AudioParam audioParam) {
        liveBridge=new LiveBridge();
        videoPusher=new VideoPusher(surfaceView,videoParam,liveBridge);
        audioPusher=new AudioPusher();
    }

    @Override
    public void prepare() {
        videoPusher.prepare();
        audioPusher.prepare();

    }

    @Override
    public void startPush() {
        videoPusher.startPush();
        audioPusher.startPush();
    }

    @Override
    public void stopPush() {
        videoPusher.stopPush();
        audioPusher.stopPush();
    }

    @Override
    public void release() {
        videoPusher.release();
        audioPusher.release();
    }

}
