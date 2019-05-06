package com.smart.im.media.push;

import android.content.Context;

import com.smart.im.media.bean.PushConfig;

/**
 * @date : 2019/3/12 下午3:51
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public interface ILivePusher {

    void init(Context context, PushConfig pushConfig);

    void destroy();


    void startPush();

    void startPushAysnc();

    void restartPush();

    void restartPushAync();

    void reconnectPushAsync(String url);

    void stopPush();
}
