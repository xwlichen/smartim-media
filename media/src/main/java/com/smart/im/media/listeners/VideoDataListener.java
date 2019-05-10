package com.smart.im.media.listeners;

import com.smart.im.media.bean.FlvData;

/**
 * @date : 2019/5/9 下午5:03
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public interface VideoDataListener {

    void collect(FlvData data, int type);
}
