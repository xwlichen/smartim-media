package com.smart.im.media.bean;

/**
 * @date : 2019/3/12 下午3:49
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class AudioParam {

    private int channelConfig;
    private int sampleRate;
    private int audioFormat;
    private int numChannels;

    public AudioParam(int sampleRate, int channelConfig, int audioFormat, int numChannels) {
        this.sampleRate = sampleRate;
        this.channelConfig = channelConfig;
        this.audioFormat = audioFormat;
        this.numChannels = numChannels;
    }

    public int getChannelConfig() {
        return channelConfig;
    }

    public void setChannelConfig(int channelConfig) {
        this.channelConfig = channelConfig;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
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
        return "AudioParam{" +
                "channelConfig=" + channelConfig +
                ", sampleRate=" + sampleRate +
                ", audioFormat=" + audioFormat +
                ", numChannels=" + numChannels +
                '}';
    }
}
