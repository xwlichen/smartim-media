package com.smart.im.media.utils.opengl;

import android.content.Context;
import android.opengl.GLES20;

import com.blankj.utilcode.util.LogUtils;
import com.smart.im.media.utils.TextResourceReader;


/**
 * @date : 2019/4/12 下午3:21
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :着色器相关工具
 */
public class ShaderHelper {

    /**
     * 编译顶点着色器
     * @param shaderCode
     * @return shaderId
     */
    public static int compileVertexShader(String shaderCode) {
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode);
    }


    /**
     * 编译片段着色器
     * @param shaderCode
     * @return shaderId
     */
    public static int compleFragmentShader(String shaderCode) {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode);
    }


    /**
     * 根据类型编译着色器
     * @param type 着色器类型
     * @param shaderCode 主色器的代码
     * @return shaderId
     */
    private static int compileShader(int type, String shaderCode) {
        // 创建一个新shader
        final int shaderId = GLES20.glCreateShader(type);
        if (shaderId == 0) {
            // 若创建失败则返回
            LogUtils.e("could not create new shader");
            return 0;
        }
        //加载shader的源代码
        GLES20.glShaderSource(shaderId, shaderCode);
        //编译shader
        GLES20. glCompileShader(shaderId);
        //存放编译成功shader数量的数组
        final int[] compileStatsu = new int[1];
        //获取Shader的编译情况
        GLES20.glGetShaderiv(shaderId, GLES20.GL_COMPILE_STATUS, compileStatsu, 0);
        if ((compileStatsu[0] == 0)) {
            GLES20.glDeleteShader(shaderId);
            LogUtils.e("Compilation of shader failed");
            return 0;
        }
        return shaderId;
    }

    /**
     * 创建、链接程序
     * @param vertexShaderId
     * @param fragmentShaderId
     * @return programId
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        //创建程序
        final int programId = GLES20.glCreateProgram();
        if (programId == 0) {
            LogUtils.e("Could not create new program");
            return 0;
        }
        //向程序加入顶点着色器
        GLES20.glAttachShader(programId, vertexShaderId);
        //向程序加入片元着色器
        GLES20.glAttachShader(programId, fragmentShaderId);
        //链接程序
        GLES20.glLinkProgram(programId);

        final int[] linkStatus = new int[1];
        //获取程序链接情况
        GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0);

        LogUtils.d("Result of linking program:\n" + GLES20.glGetProgramInfoLog(programId));

        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(programId);
            LogUtils.e("Linking of program failed");
            return 0;
        }
        return programId;
    }

    /**
     * 检验程序
     * @param programId
     * @return boolean
     */
    public static boolean validateProgram(int programId) {
        //检验程序
        GLES20.glValidateProgram(programId);
        final int[] validateStatus = new int[1];
        GLES20.glGetProgramiv(programId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0);

        LogUtils.d("Result of validating program: " + validateStatus[0] + "\nLog:" + GLES20.glGetProgramInfoLog(programId));

        return validateStatus[0] != 0;

    }


    /**
     * 创建程序
     * @param vertexShaderSource 顶点着色器代码
     * @param fragmentShaderSource 片元着色器代码
     * @return programId
     */
    public static int buildProgram(String vertexShaderSource, String fragmentShaderSource) {
        int program;
        //编译顶点着色器
        int vertexShader = compileVertexShader(vertexShaderSource);
        //编译片元着色器
        int fragmentShader = compleFragmentShader(fragmentShaderSource);

        //创建、链接程序
        program = linkProgram(vertexShader, fragmentShader);

        validateProgram(program);

        return program;
    }


    /**
     * 创建程序（resource）
     * @param context
     * @param vertexShaderSource 存放顶点着色器代码resource id
     * @param fragmentShaderSource 存放片元着色器代码resource id
     * @return programId
     */
    public static int buildProgramFromResource(Context context, int vertexShaderSource, int fragmentShaderSource) {

        String vertexString = TextResourceReader.readTxtFileFromResource(context, vertexShaderSource);
        String textureString = TextResourceReader.readTxtFileFromResource(context, fragmentShaderSource);

        return buildProgram(vertexString, textureString);
    }

    /**
     * 创建程序（resource）
     * @param context
     * @param vertexFileName 存放顶点着色器代码文件名
     * @param fragmentFileName 存放片元着色器代码文件名
     * @return programId
     */
    public static int buildProgramFromAssetFile(Context context, String vertexFileName, String fragmentFileName) {
        String vertexString = TextResourceReader.readTxtFileFromAsset(context, vertexFileName);
        String fragmentString = TextResourceReader.readTxtFileFromAsset(context, fragmentFileName);

        LogUtils.d("vertex is " + vertexString + " frag is " + fragmentString);

        return buildProgram(vertexString, fragmentString);
    }

//    /**
//     * 检查GL错误
//     * @param glOperation 操作名
//     */
//    public static void checkGlError(String glOperation) {
//        int error;
//        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
//            LogUtils.e(glOperation + ": glError " + error);
//            throw new RuntimeException(glOperation + ": glError " + error);
//        }
//    }
}
