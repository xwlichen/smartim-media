package com.smart.im.media.enums;

/**
 * @date : 2019/4/10 下午6:04
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :分辨率枚举
 */
public enum ResolutionEnum {
    /**
     *
     */
    RESOLUTION_180P,
    RESOLUTION_240P,
    RESOLUTION_360P,
    RESOLUTION_480P,
    RESOLUTION_540P,
    RESOLUTION_720P,
    RESOLUTION_1080P,
    RESOLUTION_SELFDEFINE,
    RESOLUTION_PASS_THROUGH;

    private int mSelfDefineWidth;
    private int mSelfDefineHeight;

    private ResolutionEnum() {
    }

    public void setSelfDefineResolution(int var1, int var2) {
        if (this.equals(RESOLUTION_SELFDEFINE)) {
            this.mSelfDefineWidth = var1;
            if (this.mSelfDefineWidth % 16 != 0) {
                this.mSelfDefineWidth = (this.mSelfDefineWidth / 16 + 1) * 16;
            }

            this.mSelfDefineHeight = var2;
            if (this.mSelfDefineHeight % 16 != 0) {
                this.mSelfDefineHeight = (this.mSelfDefineHeight / 16 + 1) * 16;
            }
        }

    }

    public static int GetResolutionWidth(ResolutionEnum var0) {
        if (var0.equals(RESOLUTION_180P)) {
            return 192;
        } else if (var0.equals(RESOLUTION_240P)) {
            return 240;
        } else if (var0.equals(RESOLUTION_360P)) {
            return 368;
        } else if (var0.equals(RESOLUTION_480P)) {
            return 480;
        } else if (var0.equals(RESOLUTION_540P)) {
            return 544;
        } else if (var0.equals(RESOLUTION_720P)) {
            return 720;
        } else if (var0.equals(RESOLUTION_1080P)) {
            return 1088;
        } else {
            return var0.equals(RESOLUTION_SELFDEFINE) ? var0.mSelfDefineWidth : 192;
        }
    }

    public static int GetResolutionHeight(ResolutionEnum var0) {
        if (var0.equals(RESOLUTION_180P)) {
            return 320;
        } else if (var0.equals(RESOLUTION_240P)) {
            return 320;
        } else if (var0.equals(RESOLUTION_360P)) {
            return 640;
        } else if (var0.equals(RESOLUTION_480P)) {
            return 640;
        } else if (var0.equals(RESOLUTION_540P)) {
            return 960;
        } else if (var0.equals(RESOLUTION_720P)) {
            return 1280;
        } else if (var0.equals(RESOLUTION_1080P)) {
            return 1920;
        } else {
            return var0.equals(RESOLUTION_SELFDEFINE) ? var0.mSelfDefineHeight : 320;
        }
    }


}
