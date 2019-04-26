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

    public static int getResolutionWidth(ResolutionEnum resolution) {
        if (RESOLUTION_180P.equals(resolution)) {
            return 192;
        } else if (RESOLUTION_240P.equals(resolution)) {
            return 240;
        } else if (RESOLUTION_360P.equals(resolution)) {
            return 368;
        } else if (RESOLUTION_480P.equals(resolution)) {
            return 480;
        } else if (RESOLUTION_540P.equals(resolution)) {
            return 544;
        } else if (RESOLUTION_720P.equals(resolution)) {
            return 720;
        } else if (RESOLUTION_1080P.equals(resolution)) {
            return 1088;
        } else {
            return resolution.equals(RESOLUTION_SELFDEFINE) ? resolution.mSelfDefineWidth : 192;
        }
    }

    public static int getResolutionHeight(ResolutionEnum resolution) {
        if (resolution.equals(RESOLUTION_180P)) {
            return 320;
        } else if (resolution.equals(RESOLUTION_240P)) {
            return 320;
        } else if (resolution.equals(RESOLUTION_360P)) {
            return 640;
        } else if (resolution.equals(RESOLUTION_480P)) {
            return 640;
        } else if (resolution.equals(RESOLUTION_540P)) {
            return 960;
        } else if (resolution.equals(RESOLUTION_720P)) {
            return 1280;
        } else if (resolution.equals(RESOLUTION_1080P)) {
            return 1920;
        } else {
            return resolution.equals(RESOLUTION_SELFDEFINE) ? resolution.mSelfDefineHeight : 320;
        }
    }


}
