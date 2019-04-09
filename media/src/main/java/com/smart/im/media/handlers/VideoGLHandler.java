package com.smart.im.media.handlers;


import android.os.Handler;

import java.util.logging.LogRecord;

/**
 * @date : 2019/4/9 下午4:20
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class VideoGLHandler extends Handler {
    static final int WHAT_INIT = 0x001;
    static final int WHAT_UNINIT = 0x002;
    static final int WHAT_FRAME = 0x003;
    static final int WHAT_DRAW = 0x004;
    static final int WHAT_RESET_VIDEO = 0x005;
    static final int WHAT_START_PREVIEW = 0x010;
    static final int WHAT_STOP_PREVIEW = 0x020;
    static final int WHAT_START_STREAMING = 0x100;
    static final int WHAT_STOP_STREAMING = 0x200;
    static final int WHAT_RESET_BITRATE = 0x300;

}
