package com.smart.im.media.enums;

/**
 * @date : 2019/4/10 下午5:42
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :FPS枚举
 */
public enum FpsEnum {
    /**
     * fps的值范围
     */
    FPS_8(8),
    FPS_10(10),
    FPS_12(12),
    FPS_15(15),
    FPS_20(20),
    FPS_25(25),
    FPS_30(30);


    private int fps;

    FpsEnum(int fps) {
        this.fps = fps;
    }

    public int value() {
        return fps;
    }
}
