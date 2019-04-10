package com.smart.im.media.bean;

import android.hardware.Camera;
import android.media.AudioFormat;

import com.smart.im.media.enums.EncodeEnum;
import com.smart.im.media.enums.FpsEnum;

/**
 * @date : 2019/3/14 下午4:10
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class PushConfig {


    private String url;

    private int width = 640;//分辨率设置很重要
    private int height = 480;
    private int previewWidth;
    private int previewHeight;

    private int bitRate = 1500;//kb/s jason-->480kb
    private FpsEnum fps;//fps

    private int sampleRate = 44100;//采样率：Hz
    /**
     * 声道数
     */
    private int numChannels = 2;
    private int audioBitRate = 64000;
    private int channelConfig = AudioFormat.CHANNEL_IN_STEREO;//立体声道
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;//pcm16位

    private int cameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private EncodeEnum endcoderType = EncodeEnum.HARD;

    public PushConfig() {
    }

    public PushConfig(int width, int height, int bitRate, int sampleRate, int channelConfig, int audioFormat, int numChannels) {
        this.width = width;
        this.height = height;
        this.bitRate = bitRate;
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

    public FpsEnum getFps() {
        return fps;
    }

    public void setFps(FpsEnum fps) {
        this.fps = fps;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getNumChannels() {
        return numChannels;
    }

    public void setNumChannels(int numChannels) {
        this.numChannels = numChannels;
    }

    public int getAudioBitRate() {
        return audioBitRate;
    }

    public void setAudioBitRate(int audioBitRate) {
        this.audioBitRate = audioBitRate;
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

    public int getPreviewWidth() {
        return previewWidth;
    }

    public void setPreviewWidth(int previewWidth) {
        this.previewWidth = previewWidth;
    }

    public int getPreviewHeight() {
        return previewHeight;
    }

    public void setPreviewHeight(int previewHeight) {
        this.previewHeight = previewHeight;
    }

    public EncodeEnum getEndcoderType() {
        return endcoderType;
    }

    public void setEndcoderType(EncodeEnum endcoderType) {
        this.endcoderType = endcoderType;
    }
}
