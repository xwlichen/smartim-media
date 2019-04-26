package com.smart.im.media.client;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.blankj.utilcode.util.LogUtils;
import com.smart.im.media.bean.PushConfig;
import com.smart.im.media.core.ESHardVideoCore;
import com.smart.im.media.core.ESVideoCore;
import com.smart.im.media.enums.EncodeEnum;
import com.smart.im.media.enums.ResolutionEnum;
import com.smart.im.media.utils.CameraUtil;

import java.io.IOException;

/**
 * @date : 2019/4/10 下午4:52
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class ESVideoClient {
    private final Object syncObj = new Object();


    private PushConfig pushConfig;
    private ESVideoCore esVideoCore;

    private Camera camera;
    private SurfaceTexture camTexture;

    private boolean isStreaming;
    private boolean isPreviewing;


    public ESVideoClient(PushConfig pushConfig) {
        this.pushConfig = pushConfig;
    }

    public boolean prepare() {
        if (pushConfig == null) {
            return false;
        }
        if (null == (camera = createCamera(pushConfig.getCameraType()))) {
            LogUtils.e("open camera failed");
            return false;
        }

        Camera.Size size = CameraUtil.choosePreviewSize(camera.getParameters(),
                ResolutionEnum.getResolutionWidth(pushConfig.getResolution()),
                ResolutionEnum.getResolutionHeight(pushConfig.getResolution()));

        pushConfig.setPreviewWidth(size.width);
        pushConfig.setPreviewHeight(size.height);
        if (pushConfig.getEndcoderType() == EncodeEnum.HARD) {
            esVideoCore = new ESHardVideoCore(pushConfig);
        }

        if (!esVideoCore.prepare()) {
            return false;
        }

        return true;
    }


    public boolean startPreview(SurfaceTexture surfaceTexture, int visualWidth, int visualHeight) {
        synchronized (syncObj) {
            if (!isStreaming && !isPreviewing) {
                if (!setCameraListener()) {
                    LogUtils.e("ESVideoClient start() failed");
                    return false;
                }
                 esVideoCore.updateCamTexture(camTexture);
            }
            esVideoCore.startPreview(surfaceTexture, visualWidth, visualHeight);
            isPreviewing = true;
            LogUtils.e("ESVideoClient start() success");
            return true;
        }
    }


    public boolean setCameraListener() {
        camTexture = new SurfaceTexture(ESVideoCore.OVERWATCH_TEXTURE_ID);
        if (pushConfig.getEndcoderType() == EncodeEnum.SOFT) {
            camera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    synchronized (syncObj) {
                        if (esVideoCore != null && data != null) {
//                            ((ES) esVideoCore).queueVideo(data);
                        }
                        camera.addCallbackBuffer(data);
                    }
                }
            });
        } else {
            camTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    synchronized (syncObj) {
                        if (esVideoCore != null) {
                            ((ESHardVideoCore) esVideoCore).onFrameAvailable();
                        }
                    }
                }
            });
        }
        try {
            camera.setPreviewTexture(camTexture);
        } catch (IOException e) {
            LogUtils.e(e);
            camera.release();
            return false;
        }
        camera.startPreview();
        return true;
    }

    /**
     * 创建camera
     *
     * @param cameraId
     * @return
     */
    public Camera createCamera(int cameraId) {
        try {
            camera = Camera.open(cameraId);
            camera.setDisplayOrientation(0);
        } catch (SecurityException e) {
            LogUtils.e("no permission", e);
            return null;
        } catch (Exception e) {
            LogUtils.e("camera.open()failed", e);
            return null;
        }
        return camera;
    }
}
