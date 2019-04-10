package com.smart.im.media.test;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import com.smart.im.media.bean.PushConfig;
import com.smart.im.media.push.SmartLivePusher;

/**
 * @date : 2019/3/12 下午1:43
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class LiveTextureActivity extends Activity {
    SmartLivePusher livePusher;
    TextureView textureView;
    Button btnStart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texture_live);
        textureView = findViewById(R.id.textureView);
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
        PushConfig config = new PushConfig();
//        String url = "rtmp://livepush.changguwen.com/changdao/xwRoom?auth_key=1552960379-cbbf3a97f8ea48b694dad7c139d07732-0-8296a36418ff764c576463f577c7e70d";
//        String url="rtmp://169.254.143.255:1935/rtmplive/room";
        String url = "rtmp://livepush.changguwen.com/changdao/fancyRoom?auth_key=1554106513-c1f6b59621d845ada67f68929f2e98cd-0-92e59e323431c8cc4cd327ad71746b83";
//        String url="rtmp://192.168.2.1:1935/rtmplive/room";


        config.setUrl(url);
        livePusher = new SmartLivePusher();
        livePusher.init(this, config);
        livePusher.startPreview(textureView);
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
