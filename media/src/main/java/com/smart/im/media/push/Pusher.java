package com.smart.im.media.push;

/**
 * @date : 2019/3/12 下午3:51
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public abstract class Pusher {

    public abstract void prepare();

    public abstract void startPush();

    public abstract void stopPush();

    public abstract void release();
}
