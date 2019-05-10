package com.smart.im.media.listeners;

/**
 * @date : 2019/5/10 上午10:47
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public interface LiveConnectionListener {

    void onOpenConnectionResult(int result);

    void onWriteError(int errno);

    void onCloseConnectionResult(int result);

    class RESWriteErrorRunable implements Runnable {
        LiveConnectionListener connectionListener;
        int errno;

        public RESWriteErrorRunable(LiveConnectionListener connectionListener, int errno) {
            this.connectionListener = connectionListener;
            this.errno = errno;
        }

        @Override
        public void run() {
            if (connectionListener != null) {
                connectionListener.onWriteError(errno);
            }
        }
    }
}
