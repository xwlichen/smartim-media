package com.smart.im.media.filter.hard;

import android.opengl.GLES20;

import com.smart.im.media.OpenGLConstants;
import com.smart.im.media.enums.DirectionEnum;
import com.smart.im.media.filter.BaseHardVideoFilter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;


public class GPUImageCompatibleFilter<T extends GPUImageFilter> extends BaseHardVideoFilter {
    private T innerGPUImageFilter;

    private FloatBuffer innerShapeBuffer;
    private FloatBuffer innerTextureBuffer;

    public GPUImageCompatibleFilter(T filter) {
        innerGPUImageFilter = filter;
    }

    public T getGPUImageFilter() {
        return innerGPUImageFilter;
    }

    @Override
    public void onInit(int VWidth, int VHeight) {
        super.onInit(VWidth, VHeight);
        innerGPUImageFilter.ifNeedInit();
        innerGPUImageFilter.onOutputSizeChanged(VWidth, VHeight);
    }

    @Override
    public void onDraw(int cameraTexture, int targetFrameBuffer, FloatBuffer shapeBuffer, FloatBuffer textrueBuffer) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, targetFrameBuffer);
        innerGPUImageFilter.onDraw(cameraTexture, innerShapeBuffer, innerTextureBuffer);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        innerGPUImageFilter.destroy();
    }

    @Override
    public void onDirectionUpdate(DirectionEnum _direction) {

        if (direction != _direction) {
            direction = _direction;
            innerShapeBuffer = getGPUImageCompatShapeVerticesBuffer();
            innerTextureBuffer = getGPUImageCompatTextureVerticesBuffer(direction);

        }
    }

    public static final float TEXTURE_NO_ROTATION[] = {
            1.0f, 1.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
    };

    public static final float TEXTURE_ROTATED_90[] = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
    };
    public static final float TEXTURE_ROTATED_180[] = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
    };
    public static final float TEXTURE_ROTATED_270[] = {
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
    };
    static final float CUBE[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };

    public static FloatBuffer getGPUImageCompatShapeVerticesBuffer() {
        FloatBuffer result = ByteBuffer.allocateDirect(OpenGLConstants.FLOAT_SIZE_BYTES * CUBE.length).
                order(ByteOrder.nativeOrder()).
                asFloatBuffer();
        result.put(CUBE);
        result.position(0);
        return result;
    }

    public static FloatBuffer getGPUImageCompatTextureVerticesBuffer(final DirectionEnum direction) {
        float[] buffer;
        buffer = TEXTURE_ROTATED_180.clone();

//        switch (direction) {
//            case ORIENTATION_PORTRAIT:
//                buffer = TEXTURE_ROTATED_180.clone();
//                LogUtils.e("GPUImageCompatibleFilter ORIENTATION_PORTRAIT");
//
//                break;
//            case ORIENTATION_LANDSCAPE_HOME_RIGHT:
//                buffer = TEXTURE_ROTATED_180.clone();
//                LogUtils.e("GPUImageCompatibleFilter ORIENTATION_LANDSCAPE_HOME_RIGHT");
//
//
//                break;
//            case ORIENTATION_LANDSCAPE_HOME_LEFT:
//                buffer = TEXTURE_ROTATED_180.clone();
//                LogUtils.e("GPUImageCompatibleFilter ORIENTATION_LANDSCAPE_HOME_LEFT");
//
//                break;
//            default:
//                buffer = TEXTURE_ROTATED_180.clone();
//                LogUtils.e("GPUImageCompatibleFilter default");
//
//        }
//        if (direction == ORIENTATION_LANDSCAPE_HOME_LEFT||direction ==ORIENTATION_LANDSCAPE_HOME_RIGHT) {
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
        FloatBuffer result = ByteBuffer.allocateDirect(OpenGLConstants.FLOAT_SIZE_BYTES * buffer.length).
                order(ByteOrder.nativeOrder()).
                asFloatBuffer();
        result.put(buffer);
        result.position(0);
        return result;
    }

    private static float flip(final float i) {
        return i == 0.0f ? 1.0f : 0.0f;
    }
}
