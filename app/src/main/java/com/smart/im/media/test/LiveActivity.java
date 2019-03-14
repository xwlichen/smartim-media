package com.smart.im.media.test;

import android.app.Activity;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceView;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        surfaceView = findViewById(R.id.surfaceView);
        initPusher();
    }


    public void initPusher() {
        LivePushConfig config=new LivePushConfig();
        livePusher = new SmartLivePusher();
        livePusher.init(this,config);
    }

    public void initPushConfig(){
        LivePushConfig config=new LivePushConfig();
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
