package com.smart.im.media.inter;

/**
 * @date : 2019/3/12 下午2:56
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public interface CameraYUVListener {

    void onYUVDataReceiver(byte[] data, int width, int height);
}
