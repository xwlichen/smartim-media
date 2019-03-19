package com.smart.im.media.push;

import android.content.Context;
import android.view.SurfaceView;

import com.smart.im.media.bean.LivePushConfig;
import com.smart.im.media.bridge.LiveBridge;
import com.smart.im.media.inter.AudioDataListener;
import com.smart.im.media.manager.AudioRecordManager;

/**
 * @date : 2019/3/12 下午4:35
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class AudioPusher implements ILivePusher, AudioDataListener {

    private LiveBridge liveBridge;
    private AudioRecordManager audioRecordManager;
    private boolean isPush=false;


    public AudioPusher(LiveBridge liveBridge){
        this.liveBridge=liveBridge;

        audioRecordManager=new AudioRecordManager();
        audioRecordManager.setAudioDataListener(this);
    }

    @Override
    public void init(Context context, LivePushConfig livePushConfig) {

    }

    @Override
    public void destroy() {

    }


    @Override
    public void startPush() {
        isPush=true;
        audioRecordManager.startRecord();

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
        isPush=false;

    }


    @Override
    public void audioData(byte[] data) {

        if (isPush){
            liveBridge.pushAudioData(data);
        }
    }
}
