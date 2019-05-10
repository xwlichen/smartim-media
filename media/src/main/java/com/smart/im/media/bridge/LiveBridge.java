package com.smart.im.media.bridge;

import com.smart.im.media.bean.PushConfig;

/**
 * @date : 2019/3/12 下午4:15
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class LiveBridge {

    static {
        System.loadLibrary("smart-live");
//        System.loadLibrary("smart-rtmp");

    }


    public native void initVideoConfig(int width, int height, int bitRate, int frameRate);

    public native void initAudioConfig(int numChannels, int sampleRate, int bitRate);


    public native void initRtmp(String url);

    public native void pushVideoData(byte[] data);

    public native void pushAudioData(byte[] data);


    public void initLivePushConfig(PushConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config cann't be null");
        }

        initVideoConfig(config.getWidth(),
                config.getHeight(),
                config.getVideoBitRate(),
                config.getFps().value());

        initAudioConfig(config.getNumChannels(),
                config.getAudioSampleRate(),
                config.getAudioBitRate());

    }


}
