package com.smart.im.media.bean;

import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;

/**
 * @date : 2019/4/9 下午3:11
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class OffScreenGLWapper {

    public EGLDisplay eglDisplay;
    public EGLConfig eglConfig;
    public EGLSurface eglSurface;
    public EGLContext eglContext;

    /**
     * 离屏Camera2D程序
     */
    public int cam2dProgram;
    public int cam2dTextureMatrix;
    /**
     * 离屏程序中的片元着色器的uniform常量uTexture
     */
    public int cam2dTextureLoc;
    /**
     * 离屏程序中的顶点着色器 aPosition（位置坐标）
     */
    public int cam2dPostionLoc;
    /**
     * 离屏程序中的顶点着色器 aTextureCoord（纹理坐标）
     */
    public int cam2dTextureCoordLoc;

    public int camProgram;
    public int camTextureLoc;
    public int camPostionLoc;
    public int camTextureCoordLoc;
}
