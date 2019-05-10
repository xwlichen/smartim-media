package com.smart.im.media.bean;

/**
 * @date : 2019/5/9 下午5:04
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class FlvData {

    public final static int FLV_RTMP_PACKET_TYPE_VIDEO = 9;
    public final static int FLV_RTMP_PACKET_TYPE_AUDIO = 8;
    public final static int FLV_RTMP_PACKET_TYPE_INFO = 18;
    public final static int NALU_TYPE_IDR = 5;

    private boolean droppable;

    private int dts;//解码时间戳

    private byte[] byteBuffer; //数据

    private int size; //字节长度

    private int flvTagType; //视频和音频的分类

    private int videoFrameType;

    public boolean isDroppable() {
        return droppable;
    }

    public void setDroppable(boolean droppable) {
        this.droppable = droppable;
    }

    public int getDts() {
        return dts;
    }

    public void setDts(int dts) {
        this.dts = dts;
    }

    public byte[] getByteBuffer() {
        return byteBuffer;
    }

    public void setByteBuffer(byte[] byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getFlvTagType() {
        return flvTagType;
    }

    public void setFlvTagType(int flvTagType) {
        this.flvTagType = flvTagType;
    }

    public int getVideoFrameType() {
        return videoFrameType;
    }

    public void setVideoFrameType(int videoFrameType) {
        this.videoFrameType = videoFrameType;
    }

    private boolean isKeyframe() {
        return videoFrameType == NALU_TYPE_IDR;
    }
}
