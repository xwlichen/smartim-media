package com.smart.im.media.utils;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.GLES20;

import com.blankj.utilcode.util.LogUtils;
import com.smart.im.media.bean.OffScreenGLWapper;

import javax.microedition.khronos.egl.EGL10;

/**
 * @date : 2019/4/9 下午3:16
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class GLHelper {



    /**
     * 顶点着色器脚本代码
     */
    private static String VERTEX_SHADER = "" +
            "attribute vec4 aPosition;\n" +          //属性-4维浮点型矢量 aPosition（位置坐标）
            "attribute vec2 aTextureCoord;\n" +      //属性-2维浮点型矢量 aTextureCoord（纹理坐标）
            "varying vec2 vTextureCoord;\n" +        //顶点着色器和片元着色器间的通信接口变量，顶点着色器和片元着色器公有，用于着色器间交互(如传递纹理坐标)
            "void main(){\n" +
            "    gl_Position= aPosition;\n" +
            "    vTextureCoord = aTextureCoord;\n" + //纹理坐标的赋值，与片元着色器共享
            "}";


    private static final String VERTEX_SHADER_CAMERA2D =
            "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "uniform mat4 uTextureMatrix;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main(){\n" +
                    "    gl_Position= aPosition;\n" +
                    "    vTextureCoord = (uTextureMatrix * aTextureCoord).xy;\n" +
                    "}";


    /**
     * 片元着色器脚本代码-相机
     */
    private static String FRAGMENT_SHADER_CAMERA = "" +
            "#extension GL_OES_EGL_image_external : require\n" +        //#extension 扩展列表， require:需要全部扩展
            "precision highp float;\n" +                                //设置float的精度（precision）  高精度，默认是中等精度
            "varying highp vec2 vTextureCoord;\n" +                     //与顶点着色器通信
            "uniform sampler2D uTexture;\n" +                           //常量-2D贴图采样器，基础贴图
            "void main(){\n" +
            "    vec4  color = texture2D(uTexture, vTextureCoord);\n" +
            "    gl_FragColor = color;\n" +                             //根据纹理坐标渲染到内存中
            "}";

    private static String FRAGMENT_SHADER_CAMERA2D = "" +
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision highp float;\n" +
            "varying highp vec2 vTextureCoord;\n" +
            "uniform samplerExternalOES uTexture;\n" +
            "void main(){\n" +
            "    vec4  color = texture2D(uTexture, vTextureCoord);\n" +
            "    gl_FragColor = color;\n" +
            "}";


    /**
     * 离屏渲染初始化-EGLSurface创建
     *
     * @param wapper
     */
    public static void initOffScreenGL(OffScreenGLWapper wapper) {
        wapper.eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (EGL14.EGL_NO_DISPLAY == wapper.eglDisplay) {
            throw new RuntimeException("eglGetDisplay,failed:" + android.opengl.GLUtils.getEGLErrorString(EGL14.eglGetError()));
        }
        int versions[] = new int[2];
        if (!EGL14.eglInitialize(wapper.eglDisplay, versions, 0, versions, 1)) {
            throw new RuntimeException("eglInitialize,failed:" + android.opengl.GLUtils.getEGLErrorString(EGL14.eglGetError()));
        }
        int configsCount[] = new int[1];
        EGLConfig configs[] = new EGLConfig[1];
        int configSpec[] = new int[]{
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_DEPTH_SIZE, 0,
                EGL14.EGL_STENCIL_SIZE, 0,
                EGL14.EGL_NONE
        };
        EGL14.eglChooseConfig(wapper.eglDisplay, configSpec, 0, configs, 0, 1, configsCount, 0);
        if (configsCount[0] <= 0) {
            throw new RuntimeException("eglChooseConfig,failed:" + android.opengl.GLUtils.getEGLErrorString(EGL14.eglGetError()));
        }
        wapper.eglConfig = configs[0];
        int[] surfaceAttribs = {
                EGL10.EGL_WIDTH, 1,
                EGL10.EGL_HEIGHT, 1,
                EGL14.EGL_NONE
        };
        int contextSpec[] = new int[]{
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
        };
        wapper.eglContext = EGL14.eglCreateContext(wapper.eglDisplay, wapper.eglConfig, EGL14.EGL_NO_CONTEXT, contextSpec, 0);
        if (EGL14.EGL_NO_CONTEXT == wapper.eglContext) {
            throw new RuntimeException("eglCreateContext,failed:" + android.opengl.GLUtils.getEGLErrorString(EGL14.eglGetError()));
        }
        int[] values = new int[1];
        EGL14.eglQueryContext(wapper.eglDisplay, wapper.eglContext, EGL14.EGL_CONTEXT_CLIENT_VERSION, values, 0);
        wapper.eglSurface = EGL14.eglCreatePbufferSurface(wapper.eglDisplay, wapper.eglConfig, surfaceAttribs, 0);
        if (null == wapper.eglSurface || EGL14.EGL_NO_SURFACE == wapper.eglSurface) {
            throw new RuntimeException("eglCreateWindowSurface,failed:" + android.opengl.GLUtils.getEGLErrorString(EGL14.eglGetError()));
        }
    }


    /**
     * 绑定上下文
     *
     * @param wapper
     */
    public static void makeCurrent(OffScreenGLWapper wapper) {
        // 设置默认的上下文环境和输出缓冲区(小米4上如果不设置有效的eglSurface后面创建着色器会失败，可以先创建一个默认的eglSurface)
        if (!EGL14.eglMakeCurrent(wapper.eglDisplay, wapper.eglSurface, wapper.eglSurface, wapper.eglContext)) {
            throw new RuntimeException("eglMakeCurrent,failed:" + android.opengl.GLUtils.getEGLErrorString(EGL14.eglGetError()));
        }
    }


    public static int createCameraProgram() {
        return GLShaderUtils.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_CAMERA);
    }

    public static int createCamera2DProgram() {
        return GLShaderUtils.createProgram(VERTEX_SHADER_CAMERA2D, FRAGMENT_SHADER_CAMERA2D);
    }


    public static void createCamFrameBuff(int[] frameBuffer, int[] frameBufferTex, int width, int height) {
        //创建FBO
        GLES20.glGenFramebuffers(1, frameBuffer, 0);
        //创建FBO纹理
        GLES20.glGenTextures(1, frameBufferTex, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferTex[0]);
        //设置FBO分配内存
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        //绑定FBO
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);
        //将纹理绑定到FBO
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, frameBufferTex[0], 0);
        //解绑纹理和FBO
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        //解绑FBO
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        checkGlError("createCamFrameBuff");
    }

    //检查每一步操作是否有错误的方法
    public static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            LogUtils.e(op + ": glError " + Integer.toHexString(error));
            throw new RuntimeException(op + ": glError " + error);
        }
    }


}
