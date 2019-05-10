package com.smart.im.media.threads;

import android.media.MediaCodec;
import android.media.MediaFormat;

import com.blankj.utilcode.util.LogUtils;
import com.smart.im.media.bean.FlvData;
import com.smart.im.media.bean.Packager;
import com.smart.im.media.listeners.VideoDataListener;
import com.smart.im.media.rtmp.RtmpSender;

import java.nio.ByteBuffer;


public class VideoSenderThread extends Thread {
    private static final long WAIT_TIME = 5000;
    private MediaCodec.BufferInfo eInfo;
    private long startTime = 0;
    private MediaCodec dstVideoEncoder;
    private final Object syncDstVideoEncoder = new Object();
    private VideoDataListener videoDataListener;

    public VideoSenderThread(String name, MediaCodec encoder, VideoDataListener videoDataListener) {
        super(name);
        eInfo = new MediaCodec.BufferInfo();
        startTime = 0;
        dstVideoEncoder = encoder;
        videoDataListener = videoDataListener;
    }

    public void updateMediaCodec(MediaCodec encoder) {
        synchronized (syncDstVideoEncoder) {
            dstVideoEncoder = encoder;
        }
    }

    private boolean shouldQuit = false;

    void quit() {
        shouldQuit = true;
        this.interrupt();
    }

    @Override
    public void run() {
        while (!shouldQuit) {
            synchronized (syncDstVideoEncoder) {
                int eobIndex = MediaCodec.INFO_TRY_AGAIN_LATER;
                try {
                    eobIndex = dstVideoEncoder.dequeueOutputBuffer(eInfo, WAIT_TIME);
                } catch (Exception ignored) {
                }
                switch (eobIndex) {
                    case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                        LogUtils.d("VideoSenderThread,MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED");
                        break;
                    case MediaCodec.INFO_TRY_AGAIN_LATER:
//                        LogUtils.d("VideoSenderThread,MediaCodec.INFO_TRY_AGAIN_LATER");
                        break;
                    case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                        LogUtils.d("VideoSenderThread,MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:" +
                                dstVideoEncoder.getOutputFormat().toString());
                        sendAVCDecoderConfigurationRecord(0, dstVideoEncoder.getOutputFormat());
                        break;
                    default:
                        LogUtils.d("VideoSenderThread,MediaCode,eobIndex=" + eobIndex);
                        if (startTime == 0) {
                            startTime = eInfo.presentationTimeUs / 1000;
                        }
                        /**
                         * we send sps pps already in INFO_OUTPUT_FORMAT_CHANGED
                         * so we ignore MediaCodec.BUFFER_FLAG_CODEC_CONFIG
                         */
                        if (eInfo.flags != MediaCodec.BUFFER_FLAG_CODEC_CONFIG && eInfo.size != 0) {
                            ByteBuffer realData = dstVideoEncoder.getOutputBuffers()[eobIndex];
                            realData.position(eInfo.offset + 4);
                            realData.limit(eInfo.offset + eInfo.size);
                            sendRealData((eInfo.presentationTimeUs / 1000) - startTime, realData);
                        }
                        dstVideoEncoder.releaseOutputBuffer(eobIndex, false);
                        break;
                }
            }
            try {
                sleep(5);
            } catch (InterruptedException ignored) {
            }
        }
        eInfo = null;
    }

    private void sendAVCDecoderConfigurationRecord(long tms, MediaFormat format) {
        byte[] AVCDecoderConfigurationRecord = Packager.H264Packager.generateAVCDecoderConfigurationRecord(format);
        int packetLen = Packager.FLVPackager.FLV_VIDEO_TAG_LENGTH +
                AVCDecoderConfigurationRecord.length;
        byte[] finalBuff = new byte[packetLen];
        Packager.FLVPackager.fillFlvVideoTag(finalBuff,
                0,
                true,
                true,
                AVCDecoderConfigurationRecord.length);
        System.arraycopy(AVCDecoderConfigurationRecord, 0,
                finalBuff, Packager.FLVPackager.FLV_VIDEO_TAG_LENGTH, AVCDecoderConfigurationRecord.length);
        FlvData resFlvData = new FlvData();
        resFlvData.setDroppable(false);
        resFlvData.setByteBuffer(finalBuff);
        resFlvData.setSize(finalBuff.length);
        resFlvData.setDts((int) tms);
        resFlvData.setFlvTagType(FlvData.FLV_RTMP_PACKET_TYPE_VIDEO);
        resFlvData.setVideoFrameType(FlvData.NALU_TYPE_IDR);


        videoDataListener.collect(resFlvData, RtmpSender.FROM_VIDEO);
    }

    private void sendRealData(long tms, ByteBuffer realData) {
        int realDataLength = realData.remaining();
        int packetLen = Packager.FLVPackager.FLV_VIDEO_TAG_LENGTH +
                Packager.FLVPackager.NALU_HEADER_LENGTH +
                realDataLength;
        byte[] finalBuff = new byte[packetLen];
        realData.get(finalBuff, Packager.FLVPackager.FLV_VIDEO_TAG_LENGTH +
                        Packager.FLVPackager.NALU_HEADER_LENGTH,
                realDataLength);
        int frameType = finalBuff[Packager.FLVPackager.FLV_VIDEO_TAG_LENGTH +
                Packager.FLVPackager.NALU_HEADER_LENGTH] & 0x1F;
        Packager.FLVPackager.fillFlvVideoTag(finalBuff,
                0,
                false,
                frameType == 5,
                realDataLength);
        FlvData resFlvData = new FlvData();
        resFlvData.setDroppable(true);
        resFlvData.setByteBuffer(finalBuff);
        resFlvData.setSize(finalBuff.length);
        resFlvData.setDts((int) tms);
        resFlvData.setFlvTagType(FlvData.FLV_RTMP_PACKET_TYPE_VIDEO);
        resFlvData.setVideoFrameType(frameType);
        videoDataListener.collect(resFlvData, RtmpSender.FROM_VIDEO);
    }
}