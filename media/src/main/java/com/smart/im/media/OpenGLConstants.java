package com.smart.im.media;

/**
 * @date : 2019/5/6 下午5:20
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public interface OpenGLConstants {

    /**
     * 顶点着色器脚本代码
     */
    String VERTEX_SHADER =
            "attribute vec4 aPosition;\n" +          //属性-4维浮点型矢量 aPosition（位置坐标）
                    "attribute vec2 aTextureCoord;\n" +      //属性-2维浮点型矢量 aTextureCoord（纹理坐标）
                    "varying vec2 vTextureCoord;\n" +        //顶点着色器和片元着色器间的通信接口变量，顶点着色器和片元着色器公有，用于着色器间交互(如传递纹理坐标)
                    "void main(){\n" +
                    "    gl_Position= aPosition;\n" +        //是着色器中的特殊全局变量，接受输入
                    "    vTextureCoord = aTextureCoord;\n" + //纹理坐标的赋值，与片元着色器共享
                    "}";


    String VERTEX_SHADER_CAMERA2D =
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
    String FRAGMENT_SHADER_CAMERA =
            "#extension GL_OES_EGL_image_external : require\n" +        //#extension 扩展列表， require:需要全部扩展
                    "precision highp float;\n" +                                //设置float的精度（precision）  高精度，默认是中等精度
                    "varying highp vec2 vTextureCoord;\n" +                     //与顶点着色器通信
                    "uniform sampler2D uTexture;\n" +                           //常量-2D贴图采样器，基础贴图
                    "void main(){\n" +
                    "    vec4  color = texture2D(uTexture, vTextureCoord);\n" +
                    "    gl_FragColor = color;\n" +                             //根据纹理坐标渲染到内存中
                    "}";

    String FRAGMENT_SHADER_CAMERA2D =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision highp float;\n" +
                    "varying highp vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES uTexture;\n" +
                    "void main(){\n" +
                    "    vec4  color = texture2D(uTexture, vTextureCoord);\n" +
                    "    gl_FragColor = color;\n" +                             //opengl最终渲染出来的颜色的全局变量
                    "}";


    String FRAGMENT_SHADER_2D =
            "precision highp float;\n" +
                    "varying highp vec2 vTextureCoord;\n" +
                    "uniform sampler2D uTexture;\n" +
                    "void main(){\n" +
                    "    vec4  color = texture2D(uTexture, vTextureCoord);\n" +
                    "    gl_FragColor = color;\n" +
                    "}";


    short DrawIndices[] = {0, 1, 2, 0, 2, 3};
    /**
     * 纹理坐标
     * 1
     * |
     * |
     * |
     * 0----------1
     */
    float SquareVertices[] = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, -1.0f,
            1.0f, 1.0f};
    float CamTextureVertices[] = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f};
    float Cam2dTextureVertices[] = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f};
    float Cam2dTextureVertices_90[] = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f};
    float Cam2dTextureVertices_180[] = {
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f};
    float Cam2dTextureVertices_270[] = {
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f};
    float MediaCodecTextureVertices[] = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f};

    float ScreenTextureVertices[] = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f};


    int FLOAT_SIZE_BYTES = 4;
    int SHORT_SIZE_BYTES = 2;
    int COORDS_PER_VERTEX = 2;
    int TEXTURE_COORDS_PER_VERTEX = 2;
    int NO_TEXTURE = -1;
}
