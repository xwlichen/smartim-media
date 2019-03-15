package com.smart.im.media.bean;

import android.media.AudioFormat;

/**
 * @date : 2019/3/14 下午4:10
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class LivePushConfig {

    private String url;

    private int width = 1920;//分辨率设置很重要
    private int height = 1080;
    private int bitRate = 1500;//kb/s jason-->480kb
    private int frameRate = 25;//fps
    private int sampleRate = 44100;//采样率：Hz
    private int channelConfig = AudioFormat.CHANNEL_IN_STEREO;//立体声道
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;//pcm16位
    private int numChannels = 2;//声道数

    public LivePushConfig() {
    }

    public LivePushConfig(int width, int height, int bitRate, int frameRate, int sampleRate, int channelConfig, int audioFormat, int numChannels) {
        this.width = width;
        this.height = height;
        this.bitRate = bitRate;
        this.frameRate = frameRate;
        this.sampleRate = sampleRate;
        this.channelConfig = channelConfig;
        this.audioFormat = audioFormat;
        this.numChannels = numChannels;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getChannelConfig() {
        return channelConfig;
    }

    public void setChannelConfig(int channelConfig) {
        this.channelConfig = channelConfig;
    }

    public int getAudioFormat() {
        return audioFormat;
    }

    public void setAudioFormat(int audioFormat) {
        this.audioFormat = audioFormat;
    }

    public int getNumChannels() {
        return numChannels;
    }

    public void setNumChannels(int numChannels) {
        this.numChannels = numChannels;
    }


    @Override
    public String toString() {
        return "LivePushConfig{" +
                "width=" + width +
                ", height=" + height +
                ", bitRate=" + bitRate +
                ", frameRate=" + frameRate +
                ", sampleRate=" + sampleRate +
                ", channelConfig=" + channelConfig +
                ", audioFormat=" + audioFormat +
                ", numChannels=" + numChannels +
                '}';
    }
}
