package com.smart.im.media.push;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.TextureView;

import com.blankj.utilcode.util.LogUtils;
import com.smart.im.media.bean.PushConfig;
import com.smart.im.media.bridge.LiveBridge;
import com.smart.im.media.client.LiveVideoClient;

/**
 * @date : 2019/3/12 下午3:52
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class VideoPusher implements ILivePusher {

    private boolean isPushing = false;

    private LiveBridge liveBridge;

    private LiveVideoClient liveVideoClient;


    public VideoPusher(LiveBridge liveBridge) {
        this.liveBridge = liveBridge;


    }


    @Override
    public void init(Context context, PushConfig pushConfig) {
        liveVideoClient = new LiveVideoClient(pushConfig);
        if (!liveVideoClient.prepare()) {
            LogUtils.e("!!!!! LiveVideoClient prepare() failed !!!!");
            return;
        }

    }

    @Override
    public void destroy() {

    }

    public void startPreview(TextureView textureView) {
        initTexture(textureView);
    }


    public void stopPreview() {

    }

    @Override
    public void startPush() {
        this.isPushing = true;

    }

    @Override
    public void startPushAysnc() {

    }

    @Override
    public void restartPush() {

    }

    @Override
    public void restartPushAync() {

    }


    @Override
    public void reconnectPushAsync(String url) {

    }

    @Override
    public void stopPush() {
        this.isPushing = false;

    }


    public void initTexture(TextureView textureView) {
        if (textureView == null) {
            return;
        }
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                liveVideoClient.startPreview(surface, width, height);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });


    }


    public void buildESConfig() {
    }

}
