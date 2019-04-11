package com.smart.im.media.utils;

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
import com.smart.im.media.enums.DirectionEnum;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGL10;

import static com.smart.im.media.enums.DirectionEnum.ROTATION_0;
import static com.smart.im.media.enums.DirectionEnum.ROTATION_180;

/**
 * @date : 2019/4/9 下午3:16
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class GLHelper {
    public static int FLOAT_SIZE_BYTES = 4;
    public static int SHORT_SIZE_BYTES = 2;
    public static int COORDS_PER_VERTEX = 2;
    public static int TEXTURE_COORDS_PER_VERTEX = 2;


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
                    "uniform mat4 uTextureMatrix;\n" +  //4*4浮点类型矩阵常量
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


    private static String FRAGMENT_SHADER_2D = "" +
            "precision highp float;\n" +
            "varying highp vec2 vTextureCoord;\n" +
            "uniform sampler2D uTexture;\n" +
            "void main(){\n" +
            "    vec4  color = texture2D(uTexture, vTextureCoord);\n" +
            "    gl_FragColor = color;\n" +
            "}";


    private static short drawIndices[] = {0, 1, 2, 0, 2, 3};
    private static float SquareVertices[] = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, -1.0f,
            1.0f, 1.0f};
    private static float CamTextureVertices[] = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f};
    /**
     * 纹理坐标
     * 1
     * |
     * |
     * |
     * 0----------1
     */
    private static float Cam2dTextureVertices[] = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f};
    private static float Cam2dTextureVertices_90[] = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f};
    private static float Cam2dTextureVertices_180[] = {
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f};
    private static float Cam2dTextureVertices_270[] = {
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f};
    public static float MediaCodecTextureVertices[] = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f};

    private static float ScreenTextureVertices[] = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f};


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
        return GLShaderUtils.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_CAMERA);
    }

    public static int createCamera2DProgram() {
        return GLShaderUtils.createProgram(VERTEX_SHADER_CAMERA2D, FRAGMENT_SHADER_CAMERA2D);
    }

    public static int createScreenProgram() {
        return GLShaderUtils.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_2D);
    }


    public static void createCamFrameBuff(int[] frameBuffer, int[] frameBufferTex, int width, int height) {
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
        GLES20.glDisableVertexAttribArray(posLoc);
        GLES20.glDisableVertexAttribArray(texLoc);
    }


    public static ShortBuffer getDrawIndecesBuffer() {
        ShortBuffer result = ByteBuffer.allocateDirect(SHORT_SIZE_BYTES * drawIndices.length).
                order(ByteOrder.nativeOrder()).
                asShortBuffer();
        result.put(drawIndices);
        result.position(0);
        return result;
    }

    public static FloatBuffer getShapeVerticesBuffer() {
        FloatBuffer result = ByteBuffer.allocateDirect(FLOAT_SIZE_BYTES * SquareVertices.length).
                order(ByteOrder.nativeOrder()).
                asFloatBuffer();
        result.put(SquareVertices);
        result.position(0);
        return result;
    }

    public static FloatBuffer getMediaCodecTextureVerticesBuffer() {
        FloatBuffer result = ByteBuffer.allocateDirect(FLOAT_SIZE_BYTES * MediaCodecTextureVertices.length).
                order(ByteOrder.nativeOrder()).
                asFloatBuffer();
        result.put(MediaCodecTextureVertices);
        result.position(0);
        return result;
    }

    public static FloatBuffer getScreenTextureVerticesBuffer() {
        FloatBuffer result = ByteBuffer.allocateDirect(FLOAT_SIZE_BYTES * ScreenTextureVertices.length).
                order(ByteOrder.nativeOrder()).
                asFloatBuffer();
        result.put(ScreenTextureVertices);
        result.position(0);
        return result;
    }

    public static FloatBuffer getCamera2DTextureVerticesBuffer(final DirectionEnum direction, final float cropRatio) {
        if (direction == -1) {
            FloatBuffer result = ByteBuffer.allocateDirect(FLOAT_SIZE_BYTES * Cam2dTextureVertices.length).
                    order(ByteOrder.nativeOrder()).
                    asFloatBuffer();
            result.put(CamTextureVertices);
            result.position(0);
            return result;
        }
        float[] buffer;
        switch (direction) {
            case ROTATION_90:
                buffer = Cam2dTextureVertices_90.clone();
                break;
            case ROTATION_180:
                buffer = Cam2dTextureVertices_180.clone();
                break;
            case ROTATION_270:
                buffer = Cam2dTextureVertices_270.clone();
                break;
            default:
                buffer = Cam2dTextureVertices.clone();
        }
        if (direction == ROTATION_0 || direction == ROTATION_180) {
            if (cropRatio > 0) {
                buffer[1] = buffer[1] == 1.0f ? (1.0f - cropRatio) : cropRatio;
                buffer[3] = buffer[3] == 1.0f ? (1.0f - cropRatio) : cropRatio;
                buffer[5] = buffer[5] == 1.0f ? (1.0f - cropRatio) : cropRatio;
                buffer[7] = buffer[7] == 1.0f ? (1.0f - cropRatio) : cropRatio;
            } else {
                buffer[0] = buffer[0] == 1.0f ? (1.0f + cropRatio) : -cropRatio;
                buffer[2] = buffer[2] == 1.0f ? (1.0f + cropRatio) : -cropRatio;
                buffer[4] = buffer[4] == 1.0f ? (1.0f + cropRatio) : -cropRatio;
                buffer[6] = buffer[6] == 1.0f ? (1.0f + cropRatio) : -cropRatio;
            }
        } else {
            if (cropRatio > 0) {
                buffer[0] = buffer[0] == 1.0f ? (1.0f - cropRatio) : cropRatio;
                buffer[2] = buffer[2] == 1.0f ? (1.0f - cropRatio) : cropRatio;
                buffer[4] = buffer[4] == 1.0f ? (1.0f - cropRatio) : cropRatio;
                buffer[6] = buffer[6] == 1.0f ? (1.0f - cropRatio) : cropRatio;
            } else {
                buffer[1] = buffer[1] == 1.0f ? (1.0f + cropRatio) : -cropRatio;
                buffer[3] = buffer[3] == 1.0f ? (1.0f + cropRatio) : -cropRatio;
                buffer[5] = buffer[5] == 1.0f ? (1.0f + cropRatio) : -cropRatio;
                buffer[7] = buffer[7] == 1.0f ? (1.0f + cropRatio) : -cropRatio;
            }
        }


        if ((direction & RESCoreParameters.FLAG_DIRECTION_FLIP_HORIZONTAL) != 0) {
            buffer[0] = flip(buffer[0]);
            buffer[2] = flip(buffer[2]);
            buffer[4] = flip(buffer[4]);
            buffer[6] = flip(buffer[6]);
        }
        if ((direction & RESCoreParameters.FLAG_DIRECTION_FLIP_VERTICAL) != 0) {
            buffer[1] = flip(buffer[1]);
            buffer[3] = flip(buffer[3]);
            buffer[5] = flip(buffer[5]);
            buffer[7] = flip(buffer[7]);
        }
        FloatBuffer result = ByteBuffer.allocateDirect(FLOAT_SIZE_BYTES * buffer.length).
                order(ByteOrder.nativeOrder()).
                asFloatBuffer();
        result.put(buffer);
        result.position(0);
        return result;
    }

    public static FloatBuffer getCameraTextureVerticesBuffer() {
        FloatBuffer result = ByteBuffer.allocateDirect(FLOAT_SIZE_BYTES * Cam2dTextureVertices.length).
                order(ByteOrder.nativeOrder()).
                asFloatBuffer();
        result.put(CamTextureVertices);
        result.position(0);
        return result;
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
                    .order(ByteOrder.nativeOrder())  //返回本地jvm运行的硬件的字节顺序.使用和硬件一致的字节顺序可能使buffer更加有效.
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

    private static float flip(final float i) {
        return (1.0f - i);
    }

    private static float flip2(final float i) {
        if (i == 0.0f) {
            return 1.0f;
        }
        return 0.0f;
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
