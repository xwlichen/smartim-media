package com.smart.im.media.utils.opengl;

import com.smart.im.media.enums.DirectionEnum;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static com.smart.im.media.OpenGLConstants.Cam2dTextureVertices;
import static com.smart.im.media.OpenGLConstants.Cam2dTextureVertices_180;
import static com.smart.im.media.OpenGLConstants.Cam2dTextureVertices_90;
import static com.smart.im.media.OpenGLConstants.FLOAT_SIZE_BYTES;
import static com.smart.im.media.enums.DirectionEnum.ORIENTATION_NUll;
import static com.smart.im.media.enums.DirectionEnum.ORIENTATION_PORTRAIT;

/**
 * @date : 2019/4/18 下午3:15
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class VertexArray {

    public static FloatBuffer initFloatBuffer(float[] data, int size) {
        FloatBuffer result = ByteBuffer.allocateDirect(size * data.length).
                order(ByteOrder.nativeOrder()).
                asFloatBuffer();
        result.put(data);
        result.position(0);
        return result;
    }

    public static ShortBuffer initShortBuffer(short[] data, int size) {
        ShortBuffer result = ByteBuffer.allocateDirect(size * data.length).
                order(ByteOrder.nativeOrder()).
                asShortBuffer();
        result.put(data);
        result.position(0);
        return result;
    }


    public static FloatBuffer initCamera2DTextureVerticesBuffer(final DirectionEnum direction, final float cropRatio) {
        if (direction == ORIENTATION_NUll) {
            FloatBuffer result = ByteBuffer.allocateDirect(FLOAT_SIZE_BYTES * Cam2dTextureVertices.length).
                    order(ByteOrder.nativeOrder()).
                    asFloatBuffer();
            result.put(Cam2dTextureVertices);
            result.position(0);
            return result;
        }
        float[] buffer;
        switch (direction) {
            case ORIENTATION_PORTRAIT:
                buffer = Cam2dTextureVertices_90.clone();

                break;
            case ORIENTATION_LANDSCAPE_HOME_RIGHT:
                buffer = Cam2dTextureVertices.clone();
                break;
            case ORIENTATION_LANDSCAPE_HOME_LEFT:
                buffer = Cam2dTextureVertices_180.clone();
                break;
            default:
                buffer = Cam2dTextureVertices.clone();
                break;
        }
        if (direction == ORIENTATION_PORTRAIT) {
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


//        if (direction == ORIENTATION_LANDSCAPE_HOME_LEFT || direction == ORIENTATION_LANDSCAPE_HOME_RIGHT) {
//            buffer[0] = flip(buffer[0]);
//            buffer[2] = flip(buffer[2]);
//            buffer[4] = flip(buffer[4]);
//            buffer[6] = flip(buffer[6]);
//        }
//        if (direction == ORIENTATION_PORTRAIT) {
//            buffer[1] = flip(buffer[1]);
//            buffer[3] = flip(buffer[3]);
//            buffer[5] = flip(buffer[5]);
//            buffer[7] = flip(buffer[7]);
//        }
        FloatBuffer result = ByteBuffer.allocateDirect(FLOAT_SIZE_BYTES * buffer.length).
                order(ByteOrder.nativeOrder()).
                asFloatBuffer();
        result.put(buffer);
        result.position(0);
        return result;
    }

    public static float flip(final float i) {
        return (1.0f - i);
    }

    public static float flip2(final float i) {
        if (i == 0.0f) {
            return 1.0f;
        }
        return 0.0f;
    }

}
