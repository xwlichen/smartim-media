package com.smart.im.media.utils.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.blankj.utilcode.util.LogUtils;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;
import static com.smart.im.media.OpenGLConstants.NO_TEXTURE;

/**
 * @date : 2019/4/22 下午3:53
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class TextureHelper {

    public static int CUBE = 0x01;
    public static int ANIMAL = 0x02;

    /**
     * 返回加载图像后的 OpenGl 纹理的 ID
     *
     * @param context
     * @param resourceId
     * @return
     */
    public static int loadTexture(Context context, int resourceId) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

        if (bitmap == null) {
            LogUtils.e("resource Id could not be decoded");
            return 0;
        }

        return loadTextureByBitmap(bitmap);
    }


    public static int loadTextureByBitmap(Bitmap bitmap) {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);
        if (textureObjectIds[0] == 0) {
            LogUtils.e("Could not generate a new OpenGL texture object.");
            return 0;
        }

        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

        // 设置缩小的情况下过滤方式
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        // 设置放大的情况下过滤方式
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // 加载纹理到 OpenGL，读入 Bitmap 定义的位图数据，并把它复制到当前绑定的纹理对象
        // 当前绑定的纹理对象就会被附加上纹理图像。
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

        bitmap.recycle();

        // 为当前绑定的纹理自动生成所有需要的多级渐远纹理
        // 生成 MIP 贴图
        glGenerateMipmap(GL_TEXTURE_2D);

        // 解除与纹理的绑定，避免用其他的纹理方法意外地改变这个纹理
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureObjectIds[0];
    }


    public static int loadTextureByBitmap(final Bitmap image, final int reUseTexture) {
        int[] texture = new int[1];
        if (reUseTexture == NO_TEXTURE) {
            GLES20.glGenTextures(1, texture, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, image, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        } else {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, reUseTexture);
            GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, image);
            texture[0] = reUseTexture;
        }
        return texture[0];
    }


    /**
     * 立方体纹理 生成多个纹理贴图
     *
     * @param context
     * @return
     */
    public static int[] loadCubeTexture(Context context, int type, int[] imageFileIDs) {

        final int[] cubeTextureIds = new int[7];
        Bitmap bitmap;
        glGenTextures(6, cubeTextureIds, 0);

        int[] images = imageFileIDs;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        for (int i = 0; i < 7; i++) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), images[i], options);

            glBindTexture(GL_TEXTURE_2D, cubeTextureIds[i]);
            // 设置缩小的情况下过滤方式
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            // 设置放大的情况下过滤方式
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

            bitmap.recycle();

            glGenerateMipmap(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, 0);
        }

        return cubeTextureIds;
    }
}
