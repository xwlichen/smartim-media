package com.smart.im.media.enums;

/**
 * @date : 2019/4/9 下午4:05
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public enum  DirectionEnum {
    /**
     * 垂直
     */
    FILP_VERTICAL(1),
    /**
     * 水平
     */
    FLIP_HORIZONTAL(2),

    /**
     * 旋转0度
     */
    ROTATION_0(0),
    /**
     * 旋转90度
     */
    ROTATION_90(90),
    /**
     * 旋转180度
     */
    ROTATION_180(180),
    /**
     * 旋转270度
     */
    ROTATION_270(270);


    private int duration=-1;

    DirectionEnum(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
