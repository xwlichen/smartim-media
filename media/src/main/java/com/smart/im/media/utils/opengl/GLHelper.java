package com.smart.im.media.utils.opengl;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.blankj.utilcode.util.LogUtils;
import com.smart.im.media.bean.MediaCodecGLWapper;
import com.smart.im.media.bean.OffScreenGLWapper;
import com.smart.im.media.bean.ScreenGLWapper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGL10;

import static com.smart.im.media.OpenGLConstants.COORDS_PER_VERTEX;
import static com.smart.im.media.OpenGLConstants.Cam2dTextureVertices;
import static com.smart.im.media.OpenGLConstants.FRAGMENT_SHADER_2D;
import static com.smart.im.media.OpenGLConstants.FRAGMENT_SHADER_CAMERA;
import static com.smart.im.media.OpenGLConstants.FRAGMENT_SHADER_CAMERA2D;
import static com.smart.im.media.OpenGLConstants.TEXTURE_COORDS_PER_VERTEX;
import static com.smart.im.media.OpenGLConstants.VERTEX_SHADER;
import static com.smart.im.media.OpenGLConstants.VERTEX_SHADER_CAMERA2D;
import static com.smart.im.media.utils.opengl.VertexArray.flip2;

/**
 * @date : 2019/4/9 下午3:16
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class GLHelper {


    /**
     * 创建离屏的渲染表面
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
     * 创建渲染到屏幕的表面
     *
     * @param wapper
     * @param sharedContext
     * @param screenSurface
     */
    public static void initScreenGL(ScreenGLWapper wapper, EGLContext sharedContext, SurfaceTexture screenSurface) {
        wapper.eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (EGL14.EGL_NO_DISPLAY == wapper.eglDisplay) {
            throw new RuntimeException("eglGetDisplay,failed:" + GLUtils.getEGLErrorString(EGL14.eglGetError()));
        }
        int versions[] = new int[2];
        if (!EGL14.eglInitialize(wapper.eglDisplay, versions, 0, versions, 1)) {
            throw new RuntimeException("eglInitialize,failed:" + GLUtils.getEGLErrorString(EGL14.eglGetError()));
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
            throw new RuntimeException("eglChooseConfig,failed:" + GLUtils.getEGLErrorString(EGL14.eglGetError()));
        }
        wapper.eglConfig = configs[0];
        int[] surfaceAttribs = {
                EGL14.EGL_NONE
        };
        int contextSpec[] = new int[]{
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
        };
        wapper.eglContext = EGL14.eglCreateContext(wapper.eglDisplay, wapper.eglConfig, sharedContext, contextSpec, 0);
        if (EGL14.EGL_NO_CONTEXT == wapper.eglContext) {
            throw new RuntimeException("eglCreateContext,failed:" + GLUtils.getEGLErrorString(EGL14.eglGetError()));
        }
        int[] values = new int[1];
        EGL14.eglQueryContext(wapper.eglDisplay, wapper.eglContext, EGL14.EGL_CONTEXT_CLIENT_VERSION, values, 0);
        wapper.eglSurface = EGL14.eglCreateWindowSurface(wapper.eglDisplay, wapper.eglConfig, screenSurface, surfaceAttribs, 0);
        if (null == wapper.eglSurface || EGL14.EGL_NO_SURFACE == wapper.eglSurface) {
            throw new RuntimeException("eglCreateWindowSurface,failed:" + GLUtils.getEGLErrorString(EGL14.eglGetError()));
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

    public static void makeCurrent(MediaCodecGLWapper wapper) {
        if (!EGL14.eglMakeCurrent(wapper.eglDisplay, wapper.eglSurface, wapper.eglSurface, wapper.eglContext)) {
            throw new RuntimeException("eglMakeCurrent,failed:" + GLUtils.getEGLErrorString(EGL14.eglGetError()));
        }
    }

    public static void makeCurrent(ScreenGLWapper wapper) {
        if (!EGL14.eglMakeCurrent(wapper.eglDisplay, wapper.eglSurface, wapper.eglSurface, wapper.eglContext)) {
            throw new RuntimeException("eglMakeCurrent,failed:" + GLUtils.getEGLErrorString(EGL14.eglGetError()));
        }
    }


    public static int createCameraProgram() {
        return ShaderHelper.buildProgram(VERTEX_SHADER, FRAGMENT_SHADER_CAMERA);
    }

    public static int createCamera2DProgram() {
        return ShaderHelper.buildProgram(VERTEX_SHADER_CAMERA2D, FRAGMENT_SHADER_CAMERA2D);
    }

    public static int createScreenProgram() {
        return ShaderHelper.buildProgram(VERTEX_SHADER, FRAGMENT_SHADER_2D);
    }

    public static void createFrameBuff(int[] frameBuffer, int[] frameBufferTex, int width, int height) {
        //创建FBO
        GLES20.glGenFramebuffers(1, frameBuffer, 0);
        //创建FBO纹理
        GLES20.glGenTextures(1, frameBufferTex, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferTex[0]);
        //设置FBO分配内存
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        //设置材质的一些属性
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


    public static void enableVertex(int posLoc, int texLoc, FloatBuffer shapeBuffer, FloatBuffer texBuffer) {
        //允许使用顶点坐标数组
        GLES20.glEnableVertexAttribArray(posLoc);
        //允许使用纹理坐标数组
        GLES20.glEnableVertexAttribArray(texLoc);
        /**定义顶点属性数组
         * index 指定要修改的顶点着色器中顶点变量id；
         * size 指定每个顶点属性的组件数量。必须为1、2、3或者4。如position是由3个（x,y,z）组成，而颜色是4个（r,g,b,a））；
         * type 指定数组中每个组件的数据类型。可用的符号常量有GL_BYTE, GL_UNSIGNED_BYTE, GL_SHORT,GL_UNSIGNED_SHORT, GL_FIXED, 和 GL_FLOAT，初始值为GL_FLOAT；
         * normalized 指定当被访问时，固定点数据值是否应该被归一化（GL_TRUE）或者直接转换为固定点值（GL_FALSE）；
         * stride 指定连续顶点属性之间的偏移量。如果为0，那么顶点属性会被理解为：它们是紧密排列在一起的。初始值为0。如果normalized被设置为GL_TRUE，意味着整数型的值会被映射至区间-1,1，或者区间[0,1]（无符号整数），反之，这些值会被直接转换为浮点值而不进行归一化处理；
         * ptr 顶点的缓冲数据。
         */
        GLES20.glVertexAttribPointer(posLoc, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                COORDS_PER_VERTEX * 4, shapeBuffer);
        GLES20.glVertexAttribPointer(texLoc, TEXTURE_COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                TEXTURE_COORDS_PER_VERTEX * 4, texBuffer);
    }


    public static void disableVertex(int posLoc, int texLoc) {
        //禁用index指定顶点属性数组
        GLES20.glDisableVertexAttribArray(posLoc);
        GLES20.glDisableVertexAttribArray(texLoc);
    }


    /**
     * 根据pushconfig的镜像相关参数设置纹理的坐标缓冲区
     *
     * @param flipHorizontal
     * @return
     */
    public static FloatBuffer adjustTextureFlip(boolean flipHorizontal) {
        //对纹理坐标对角轴翻转
        float[] textureCords = getFlip(flipHorizontal, false);
        FloatBuffer mTextureBuffer = null;
        if (mTextureBuffer == null) {
            //allocateDirect直接从系统级内存获取数据更高效   allocate是先从系统级内存copy到jvm内存中共java使用
            mTextureBuffer = ByteBuffer.allocateDirect(textureCords.length * 4)
                    .order(ByteOrder.nativeOrder())  //返回本地jvm运行的硬件的字节顺序.使用和硬件一致的字节顺序可能使buffer更加有效，
                    // 本地字节序是指，当一个值占用多个字节时，比如 32 位整型数，字节按照从最重要位到最不重要位或者相反顺序排列。
                    .asFloatBuffer();  //转成FloatBuffer
        }
        mTextureBuffer.clear();
        mTextureBuffer.put(textureCords).position(0);//把数组写入缓冲区，设置缓冲区的初始位置

        return mTextureBuffer;
    }

    public static float[] getFlip(final boolean flipHorizontal,
                                  final boolean flipVertical) {
        float[] rotatedTex = Cam2dTextureVertices;

        if (flipHorizontal) {
            rotatedTex = new float[]{
                    flip2(rotatedTex[0]), rotatedTex[1],
                    flip2(rotatedTex[2]), rotatedTex[3],
                    flip2(rotatedTex[4]), rotatedTex[5],
                    flip2(rotatedTex[6]), rotatedTex[7],
            };
        }
        if (flipVertical) {
            rotatedTex = new float[]{
                    rotatedTex[0], flip2(rotatedTex[1]),
                    rotatedTex[2], flip2(rotatedTex[3]),
                    rotatedTex[4], flip2(rotatedTex[5]),
                    rotatedTex[6], flip2(rotatedTex[7]),
            };
        }
        return rotatedTex;
    }


    /**
     * 检查每一步操作是否有错误的方法
     */
    public static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            LogUtils.e(op + ": glError " + Integer.toHexString(error));
            throw new RuntimeException(op + ": glError " + error);
        }
    }


}
