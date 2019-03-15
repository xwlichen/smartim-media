package com.smart.im.media.push;

import android.content.Context;
import android.view.SurfaceView;

import com.smart.im.media.bean.LivePushConfig;

/**
 * @date : 2019/3/12 下午3:51
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public interface  ILivePusher {

    void init(Context context, LivePushConfig livePushConfig);

    void destroy();

    void startPreview(SurfaceView surfaceView);

    void startPreviewAysnc(SurfaceView surfaceView);

    void stopPreview();

    void startPush();

    void startPushAysnc();

    void restartPush();

    void restartPushAync();

    void reconnectPushAsync(String url);

    void stopPush();
}
