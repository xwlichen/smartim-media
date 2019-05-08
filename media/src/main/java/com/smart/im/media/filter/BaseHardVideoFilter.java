package com.smart.im.media.filter;

import com.smart.im.media.enums.DirectionEnum;
import com.smart.im.media.utils.opengl.VertexArray;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static com.smart.im.media.OpenGLConstants.DrawIndices;
import static com.smart.im.media.OpenGLConstants.SHORT_SIZE_BYTES;
import static com.smart.im.media.enums.DirectionEnum.ORIENTATION_NUll;

/**
 * @date : 2019/5/7 下午4:06
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class BaseHardVideoFilter {

    protected int SIZE_WIDTH;
    protected int SIZE_HEIGHT;
    protected DirectionEnum direction = ORIENTATION_NUll;
    protected ShortBuffer drawIndecesBuffer;

    public void onInit(int VWidth, int VHeight) {
        SIZE_WIDTH = VWidth;
        SIZE_HEIGHT = VHeight;
        drawIndecesBuffer = VertexArray.initShortBuffer(DrawIndices, SHORT_SIZE_BYTES);
    }

    public void onDraw(final int cameraTexture, final int targetFrameBuffer, final FloatBuffer shapeBuffer, final FloatBuffer textrueBuffer) {
    }

    public void onDestroy() {

    }

    public void onDirectionUpdate(DirectionEnum direction) {
        this.direction = direction;
    }
}
