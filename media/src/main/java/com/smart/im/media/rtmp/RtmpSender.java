package com.smart.im.media.rtmp;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.blankj.utilcode.util.LogUtils;
import com.smart.im.media.bean.ByteSpeedometer;
import com.smart.im.media.bean.FLvMetaData;
import com.smart.im.media.bean.FlvData;
import com.smart.im.media.bean.FrameRateMeter;
import com.smart.im.media.bean.PushConfig;
import com.smart.im.media.listeners.CallbackDelivery;
import com.smart.im.media.listeners.LiveConnectionListener;


public class RtmpSender {
    private static final int TIMEGRANULARITY = 3000;
    public static final int FROM_AUDIO = 8;
    public static final int FROM_VIDEO = 6;
    private WorkHandler workHandler;
    private HandlerThread workHandlerThread;
    private final Object syncOp = new Object();

    public void prepare(PushConfig config) {
        synchronized (syncOp) {
            workHandlerThread = new HandlerThread("RtmpSender,workHandlerThread");
            workHandlerThread.start();
            workHandler = new WorkHandler(config.getSenderQueueLength(),
                    new FLvMetaData(config),
                    workHandlerThread.getLooper());
        }
    }

    public void setConnectionListener(LiveConnectionListener connectionListener) {
        synchronized (syncOp) {
            workHandler.setConnectionListener(connectionListener);
        }
    }

    public String getServerIpAddr() {
        synchronized (syncOp) {
            return workHandler == null ? null : workHandler.getServerIpAddr();
        }
    }

    public float getSendFrameRate() {
        synchronized (syncOp) {
            return workHandler == null ? 0 : workHandler.getSendFrameRate();
        }
    }

    public float getSendBufferFreePercent() {
        synchronized (syncOp) {
            return workHandler == null ? 0 : workHandler.getSendBufferFreePercent();
        }
    }

    public void start(String rtmpAddr) {
        synchronized (syncOp) {
            workHandler.sendStart(rtmpAddr);
        }
    }

    public void feed(FlvData flvData, int type) {
        synchronized (syncOp) {
            workHandler.sendFood(flvData, type);
        }
    }

    public void stop() {
        synchronized (syncOp) {
            workHandler.sendStop();
        }
    }

    public void destroy() {
        synchronized (syncOp) {
            workHandler.removeCallbacksAndMessages(null);
            //workHandlerThread.quit();
            workHandler.sendStop();
            workHandlerThread.quitSafely();
            /**
             * do not wait librtmp to quit
             */
//        try {
//            workHandlerThread.join();
//        } catch (InterruptedException ignored) {
//        }
        }
    }

    public int getTotalSpeed() {
        synchronized (syncOp) {
            if (workHandler != null) {
                return workHandler.getTotalSpeed();
            } else {
                return 0;
            }
        }
    }


    public WorkHandler getWorkHandler() {
        return workHandler;
    }


    public static class WorkHandler extends Handler {
        private final static int MSG_START = 1;
        private final static int MSG_WRITE = 2;
        private final static int MSG_STOP = 3;
        private long jniRtmpPointer = 0;
        private String serverIpAddr = null;
        private int maxQueueLength;
        private int writeMsgNum = 0;
        private final Object syncWriteMsgNum = new Object();
        private ByteSpeedometer videoByteSpeedometer = new ByteSpeedometer(TIMEGRANULARITY);
        private ByteSpeedometer audioByteSpeedometer = new ByteSpeedometer(TIMEGRANULARITY);
        private FrameRateMeter sendFrameRateMeter = new FrameRateMeter();
        private FLvMetaData fLvMetaData;
        private LiveConnectionListener connectionListener;
        private final Object syncConnectionListener = new Object();
        private int errorTime = 0;

        private enum STATE {
            IDLE,
            RUNNING,
            STOPPED
        }

        private STATE state;

        WorkHandler(int maxQueueLength, FLvMetaData fLvMetaData, Looper looper) {
            super(looper);
            this.maxQueueLength = maxQueueLength;
            this.fLvMetaData = fLvMetaData;
            state = STATE.IDLE;
        }

        public String getServerIpAddr() {
            return serverIpAddr;
        }

        public float getSendFrameRate() {
            return sendFrameRateMeter.getFps();
        }

        public float getSendBufferFreePercent() {
            synchronized (syncWriteMsgNum) {
                float res = (float) (maxQueueLength - writeMsgNum) / (float) maxQueueLength;
                return res <= 0 ? 0f : res;
            }
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START:
                    if (state == STATE.RUNNING) {
                        break;
                    }
                    sendFrameRateMeter.reSet();
                    LogUtils.d("RtmpSender,WorkHandler,tid=" + Thread.currentThread().getId());
//                    jniRtmpPointer = RtmpClient.open((String) msg.obj, true);
                    final int openR = jniRtmpPointer == 0 ? 1 : 0;
                    if (openR == 0) {
//                        serverIpAddr = RtmpClient.getIpAddr(jniRtmpPointer);
                    }
                    synchronized (syncConnectionListener) {
                        if (connectionListener != null) {
                            CallbackDelivery.i().post(new Runnable() {
                                @Override
                                public void run() {
                                    connectionListener.onOpenConnectionResult(openR);
                                }
                            });
                        }
                    }
                    if (jniRtmpPointer == 0) {
                        break;
                    } else {
                        byte[] MetaData = fLvMetaData.getMetaData();
//                        RtmpClient.write(jniRtmpPointer,
//                                MetaData,
//                                MetaData.length,
//                                FlvData.FLV_RTMP_PACKET_TYPE_INFO, 0);
                        state = STATE.RUNNING;
                    }
                    break;
                case MSG_STOP:
                    if (state == STATE.STOPPED || jniRtmpPointer == 0) {
                        break;
                    }
                    errorTime = 0;
//                    final int closeR = RtmpClient.close(jniRtmpPointer);
                    serverIpAddr = null;
                    synchronized (syncConnectionListener) {
                        if (connectionListener != null) {
                            CallbackDelivery.i().post(new Runnable() {
                                @Override
                                public void run() {
//                                    connectionListener.onCloseConnectionResult(closeR);
                                }
                            });
                        }
                    }
                    state = STATE.STOPPED;
                    break;
                case MSG_WRITE:
                    synchronized (syncWriteMsgNum) {
                        --writeMsgNum;
                    }
                    if (state != STATE.RUNNING) {
                        break;
                    }

                    if (mListener != null) {
                        mListener.getBufferFree(getSendBufferFreePercent());
                    }

                    FlvData flvData = (FlvData) msg.obj;
                    if (writeMsgNum >= (maxQueueLength * 3 / 4) && flvData.getFlvTagType() == FlvData.FLV_RTMP_PACKET_TYPE_VIDEO && flvData.isDroppable()) {
                        LogUtils.d("senderQueue is crowded,abandon video");
                        break;
                    }
                    final int res = 0;
//                    final int res = RtmpClient.write(jniRtmpPointer, flvData.getByteBuffer(), flvData.getByteBuffer().length, flvData.getFlvTagType(), flvData.getDts());
                    if (res == 0) {
                        errorTime = 0;
                        if (flvData.getFlvTagType() == FlvData.FLV_RTMP_PACKET_TYPE_VIDEO) {
                            videoByteSpeedometer.gain(flvData.getSize());
                            sendFrameRateMeter.count();
                        } else {
                            audioByteSpeedometer.gain(flvData.getSize());
                        }
                    } else {
                        ++errorTime;
                        synchronized (syncConnectionListener) {
                            if (connectionListener != null) {
                                CallbackDelivery.i().post(new LiveConnectionListener.RESWriteErrorRunable(connectionListener, res));
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        public void sendStart(String rtmpAddr) {
            this.removeMessages(MSG_START);
            synchronized (syncWriteMsgNum) {
                this.removeMessages(MSG_WRITE);
                writeMsgNum = 0;
            }
            this.sendMessage(this.obtainMessage(MSG_START, rtmpAddr));
        }

        public void sendStop() {
            this.removeMessages(MSG_STOP);
            synchronized (syncWriteMsgNum) {
                this.removeMessages(MSG_WRITE);
                writeMsgNum = 0;
            }
            this.sendEmptyMessage(MSG_STOP);
        }

        public void sendFood(FlvData flvData, int type) {
            synchronized (syncWriteMsgNum) {
                //LAKETODO optimize
                if (writeMsgNum <= maxQueueLength) {
                    this.sendMessage(this.obtainMessage(MSG_WRITE, type, 0, flvData));
                    ++writeMsgNum;
                } else {
                    LogUtils.d("senderQueue is full,abandon");
                }
            }
        }

        public void setConnectionListener(LiveConnectionListener connectionListener) {
            synchronized (syncConnectionListener) {
                this.connectionListener = connectionListener;
            }
        }

        public int getTotalSpeed() {
            return getVideoSpeed() + getAudioSpeed();
        }

        public int getVideoSpeed() {
            return videoByteSpeedometer.getSpeed();
        }

        public int getAudioSpeed() {
            return audioByteSpeedometer.getSpeed();
        }


        private BufferFreeListener mListener = null;

        public interface BufferFreeListener {
            void getBufferFree(float free);
        }

        public void setBufferFreeListener(BufferFreeListener listener) {
            mListener = listener;
        }

    }
}
