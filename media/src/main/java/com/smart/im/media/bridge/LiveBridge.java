package com.smart.im.media.bridge;

import com.smart.im.media.bean.LivePushConfig;

/**
 * @date : 2019/3/12 下午4:15
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class LiveBridge {


    private native void initVideoConfig(int width, int height, int bitRate, int frameRate);

    private native void initAudioConfig(int sampleRate, int numChannels);


    private native void initRtmp(String url);

    private native void pushVideoData(byte[] data);


    public void initLivePushConfig(LivePushConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config cann't be null");
        }

        initVideoConfig(config.getWidth(),
                config.getHeight(),
                config.getBitRate(),
                config.getFrameRate());

        initAudioConfig(config.getSampleRate(),
                config.getNumChannels());

    }
}
