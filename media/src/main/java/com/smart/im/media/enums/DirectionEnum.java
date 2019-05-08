package com.smart.im.media.enums;

/**
 * @date : 2019/4/9 下午4:05
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public enum DirectionEnum {
    /**
     * 空值
     */
    ORIENTATION_NUll(-1),
    /**
     * 垂直
     */
    ORIENTATION_PORTRAIT(0),
    /**
     * 水平右
     */
    ORIENTATION_LANDSCAPE_HOME_RIGHT(90),
    /**
     * 水平左
     */
    ORIENTATION_LANDSCAPE_HOME_LEFT(270);

    private int direction = -1;

    DirectionEnum(int direction) {
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
