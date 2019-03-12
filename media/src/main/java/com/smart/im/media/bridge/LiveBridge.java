package com.smart.im.media.bridge;

/**
 * @date : 2019/3/12 下午4:15
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class LiveBridge {


    private native void setVideoParam(int width, int height, int bitRate, int frameRate);
    private native void setAudioParam(int sampleRate, int numChannels);
}
