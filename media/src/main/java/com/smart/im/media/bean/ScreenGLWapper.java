package com.smart.im.media.bean;

import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;

/**
 * @date : 2019/4/11 上午10:24
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class ScreenGLWapper {
    public EGLDisplay eglDisplay;
    public EGLConfig eglConfig;
    public EGLSurface eglSurface;
    public EGLContext eglContext;

    public int drawProgram;
    public int drawTextureLoc;
    public int drawPostionLoc;
    public int drawTextureCoordLoc;
}
