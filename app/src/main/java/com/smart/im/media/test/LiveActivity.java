package com.smart.im.media.test;

import android.app.Activity;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.smart.im.media.bean.LivePushConfig;
import com.smart.im.media.push.SmartLivePusher;

/**
 * @date : 2019/3/12 下午1:43
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class LiveActivity extends Activity {
    SmartLivePusher livePusher;
    SurfaceView surfaceView;
    Button btnStart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        surfaceView = findViewById(R.id.surfaceView);
        btnStart = findViewById(R.id.btnStart);
        initPusher();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                livePusher.startPush();
            }
        });
    }


    public void initPusher() {
        LivePushConfig config = new LivePushConfig();
//        String url = "rtmp://livepush.changguwen.com/changdao/xwRoom?auth_key=1552960379-cbbf3a97f8ea48b694dad7c139d07732-0-8296a36418ff764c576463f577c7e70d";
//        String url="rtmp://169.254.143.255:1935/rtmplive/room";
        String url = "rtmp://livepush.changguwen.com/changdao/xwRoom?auth_key=1553500526-009d419a47254988b39033171c2c3bc6-0-4c778cf9ec54ced06b7ff43aca8d55ee";
//        String url="rtmp://192.168.1.96:1935/rtmplive/room";


        config.setUrl(url);
        livePusher = new SmartLivePusher();
        livePusher.init(this, config);
        livePusher.startPreview(surfaceView);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
