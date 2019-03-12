package com.smart.im.media.test;

import android.app.Activity;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceView;

import com.smart.im.media.bean.AudioParam;
import com.smart.im.media.bean.VideoParam;
import com.smart.im.media.push.LivePusher;

/**
 * @date : 2019/3/12 下午1:43
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class LiveActivity extends Activity {
    LivePusher livePusher;
    SurfaceView surfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        surfaceView = findViewById(R.id.surfaceView);
        initPusher();
    }


    public void initPusher() {
        VideoParam videoParam = initVideoParams();
        AudioParam audioParam = initAudioParams();
        livePusher = new LivePusher(surfaceView, videoParam, audioParam);
    }


    public VideoParam initVideoParams() {
        int width = 640;//分辨率设置很重要
        int height = 480;
        int videoBitRate = 1500;//kb/s jason-->480kb
        int videoFrameRate = 25;//fps
        VideoParam videoParam = new VideoParam(width, height,
                Camera.CameraInfo.CAMERA_FACING_BACK, videoBitRate, videoFrameRate);
        return videoParam;
    }

    public AudioParam initAudioParams() {
        int sampleRate = 44100;//采样率：Hz
        int channelConfig = AudioFormat.CHANNEL_IN_STEREO;//立体声道
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;//pcm16位
        int numChannels = 2;//声道数
        AudioParam audioParam = new AudioParam(sampleRate, channelConfig, audioFormat, numChannels);
        return audioParam;
    }

    @Override
    protected void onResume() {
        super.onResume();
        livePusher.prepare();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        livePusher.release();
    }
}
