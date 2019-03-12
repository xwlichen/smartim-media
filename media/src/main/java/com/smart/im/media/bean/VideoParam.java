package com.smart.im.media.bean;

/**
 * @date : 2019/3/12 下午3:49
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class VideoParam {

    private int width;
    private int height;
    private int cameraId;
    private int bitRate;
    private int frameRate;

    public VideoParam(int width, int height, int cameraId, int bitRate, int frameRate) {
        this.width = width;
        this.height = height;
        this.cameraId = cameraId;
        this.bitRate = bitRate;
        this.frameRate = frameRate;
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

    public int getCameraId() {
        return cameraId;
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
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

    @Override
    public String toString() {
        return "VideoParam{" +
                "width=" + width +
                ", height=" + height +
                ", cameraId=" + cameraId +
                ", bitRate=" + bitRate +
                ", frameRate=" + frameRate +
                '}';
    }
}
