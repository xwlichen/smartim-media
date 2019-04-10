package com.smart.im.media.utils;

import android.opengl.GLES20;
import android.util.Log;

/**
 * @date : 2019/4/10 上午11:24
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class GLShaderUtils {

    public static final String TAG = GLShaderUtils.class.getSimpleName();

    /**
     * 创建shader程序方法
     *
     * @param vertexShaderCode   顶点着色器的脚本字符串
     * @param fragmentShaderCode 片元着色器的脚本字符串
     * @return program
     */
    public static int createProgram(String vertexShaderCode, String fragmentShaderCode) {
        if (vertexShaderCode == null || fragmentShaderCode == null) {
            throw new RuntimeException("invalid shader code");
        }

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        if (vertexShader == 0) {
            return 0;
        }
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, vertexShaderCode);
        if (fragmentShader == 0) {
            return 0;
        }
        int[] status = new int[1];

        //创建程序
        int program = GLES20.glCreateProgram();
        //
        if (program != 0) {
            //向程序加入顶点着色器
            GLES20.glAttachShader(program, vertexShader);
            //向程序加入片元着色器
            GLES20.glAttachShader(program, fragmentShader);
            //链接程序
            GLES20.glLinkProgram(program);
            //获取程序链接情况
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);
            //若链接失败则报错
            if (GLES20.GL_FALSE == status[0]) {
                throw new RuntimeException("link program,failed:" + GLES20.glGetProgramInfoLog(program));
            }
        }
        return program;
    }


    /**
     * 加载制定shader的方法
     *
     * @param shaderType shader的类型  GLES20.GL_VERTEX_SHADER   GLES20.GL_FRAGMENT_SHADER
     * @param source     shader的脚本字符串
     * @return 着色器id
     */
    private static int loadShader(int shaderType, String source) {
        // 创建一个新shader
        int shader = GLES20.glCreateShader(shaderType);
        // 若创建成功则加载shader
        if (shader != 0) {
            //加载shader的源代码
            GLES20.glShaderSource(shader, source);
            //编译shader
            GLES20.glCompileShader(shader);
            //存放编译成功shader数量的数组
            int[] compiled = new int[1];
            //获取Shader的编译情况
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {//若编译失败则显示错误日志并删除此shader
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }


}
