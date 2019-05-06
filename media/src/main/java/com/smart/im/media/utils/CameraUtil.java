package com.smart.im.media.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

/**
 * @date : 2019/3/12 下午2:09
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class CameraUtil {

    private Context context;

    private Camera camera;
    private boolean isFocusing;  //是否正在对焦
    private boolean isStartPreview; //是否已经开始预览了

    private int rotation; //摄像头旋转角度
    private int preWidth = 640; //
    private int preHeight = 480;
    private int currentType = Camera.CameraInfo.CAMERA_FACING_FRONT; //摄像头的的前后置


    public CameraUtil() {
        super();
    }

    public CameraUtil(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getCurrentType() {
        return currentType;
    }

    public void setCurrentType(int currentType) {
        this.currentType = currentType;
    }

    /**
     * 选择合适的Camera 尺寸
     *
     * @param parms
     * @param width
     * @param height
     */
    public static Camera.Size choosePreviewSize(Camera.Parameters parms, int width, int height) {
        //先判断是否支持该分辨率
        Camera.Size resultSize = null;
        for (Camera.Size size : parms.getSupportedPreviewSizes()) {

            if (size.width == width && size.height == height) {
                resultSize = size;
                break;
            }

            //满足16:9的才进行使用并且小于等于width的才进行使用
            if (size.width * height == size.height * width && size.width <= width) {
                if (resultSize == null) {
                    resultSize = size;
                } else if (resultSize.width < size.width) {
                    resultSize = size;
                }
            }
        }
        if (resultSize == null) {
            resultSize = parms.getPreferredPreviewSizeForVideo();
        }
        return resultSize;
    }

    private int setCameraDisplayRotation(int cameraId) {

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            //前置摄像头需要镜像,转化后进行设置
            camera.setDisplayOrientation((360 - result) % 360);
        } else {
            result = (info.orientation - degrees + 360) % 360;
            //后置摄像头直接进行显示
            camera.setDisplayOrientation(result);
        }
        return result;
    }


    public void initCamera(int cameraType) {
        if (camera != null) {
            //释放camera
            releaseCamera();
        }
        Camera.CameraInfo info = new Camera.CameraInfo();
        int cameraId = 0;
//        int nucameras = Camera.getNumberOfCameras();
//        for (int i = 0; i < nucameras; i++) {
//            Camera.getCameraInfo(i, info);
//            if (info.facing == cameraType) {
//                cameraId = i;
//                camera = Camera.open(i);
//                currentType = cameraType;
//                break;
//            }
//        }
        try {
            camera = Camera.open(cameraType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (camera == null) {
            throw new RuntimeException("unable to open camera");
        }

        //这边是设置旋转的
//        rotation = setCameraDisplayRotation(cameraId);

        Camera.Parameters parameters = camera.getParameters();
//        chooseCameraSize(parameters, preWidth, preHeight, 1);
        List<String> focusModes = parameters.getSupportedFocusModes();
        //这边采用自动对焦的模式
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        parameters.setPreviewFormat(ImageFormat.NV21);
        parameters.setPictureFormat(ImageFormat.NV21);
        camera.setParameters(parameters);


        /**
         * 请注意这个地方, camera返回的图像并不一定是设置的大小（因为可能并不支持）
         */
        Camera.Size size = camera.getParameters().getPreviewSize();
        preWidth = size.width;
        preHeight = size.height;


    }

    public void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
            context = null;
            isStartPreview = false;
            isFocusing = false;
        }
    }

    public void startPreview(SurfaceHolder surfaceHolder, Camera.PreviewCallback callback) {
        camera.setPreviewCallbackWithBuffer(callback);
        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
        isStartPreview = true;
        isFocusing = false;
        //进行一次自动对焦
        startAutoFocus();
    }


    public void startPreview(SurfaceTexture surfaceTexture) {
        try {
            camera.setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
            camera.release();
        }
        camera.startPreview();
        isStartPreview = true;
        isFocusing = false;
        //进行一次自动对焦
        startAutoFocus();
    }

    public void startAutoFocus() {
        try {
            if (camera != null && !isFocusing && isStartPreview) { //camera不为空，并且isFocusing=false的时候才去对焦
                camera.cancelAutoFocus();
                isFocusing = true;
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        isFocusing = false;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
