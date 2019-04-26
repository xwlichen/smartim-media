package com.smart.im.media.core;

import android.graphics.SurfaceTexture;

import com.smart.im.media.bean.ESConfig;

/**
 * @date : 2019/4/9 下午3:39
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public interface ESVideoCore {

    int OVERWATCH_TEXTURE_ID = 10;

    /**
     * 配置准备
     */
    boolean prepare();


    /**
     * 预览
     * @param surfaceTexture
     * @param visualWidth  TextureView的宽
     * @param visualHeight TextureView的高
     */
    void startPreview(SurfaceTexture surfaceTexture, int visualWidth, int visualHeight);


    void updateCamTexture(SurfaceTexture camTex);

}
