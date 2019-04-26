package com.smart.im.media.push;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.TextureView;

import com.blankj.utilcode.util.LogUtils;
import com.smart.im.media.bean.ESConfig;
import com.smart.im.media.bean.PushConfig;
import com.smart.im.media.bridge.LiveBridge;
import com.smart.im.media.client.ESVideoClient;

/**
 * @date : 2019/3/12 下午3:52
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class VideoPusher implements ILivePusher {

    private static final String TAG = VideoPusher.class.getSimpleName();

    public int OVERWATCH_TEXTURE_ID = 10;

    private SurfaceTexture cameraTexture;
    private boolean isPushing = false;

    private LiveBridge liveBridge;

    private ESVideoClient esVideoClient;


    public VideoPusher(LiveBridge liveBridge) {
        this.liveBridge = liveBridge;


    }


    @Override
    public void init(Context context, PushConfig pushConfig) {
        ESConfig esConfig = new ESConfig();
        esVideoClient = new ESVideoClient(pushConfig);
        if (!esVideoClient.prepare()) {
            LogUtils.e("!!!!! ESVideoClient prepare() failed !!!!");
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
                esVideoClient.startPreview(surface, width, height);
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
