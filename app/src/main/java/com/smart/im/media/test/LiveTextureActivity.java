package com.smart.im.media.test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.smart.im.media.bean.PushConfig;
import com.smart.im.media.filter.BaseHardVideoFilter;
import com.smart.im.media.filter.hard.GPUImageBeautyFilter;
import com.smart.im.media.filter.hard.GPUImageCompatibleFilter;
import com.smart.im.media.filter.hard.HardVideoGroupFilter;
import com.smart.im.media.filter.hard.WatermarkFilter;
import com.smart.im.media.push.SmartLivePusher;

import java.util.LinkedList;

import static com.smart.im.media.enums.DirectionEnum.ORIENTATION_LANDSCAPE_HOME_LEFT;
import static com.smart.im.media.enums.DirectionEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT;

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
        PushConfig config = PushConfig.obtain();
//        String url = "rtmp://livepush.changguwen.com/changdao/xwRoom?auth_key=1552960379-cbbf3a97f8ea48b694dad7c139d07732-0-8296a36418ff764c576463f577c7e70d";
//        String url="rtmp://169.254.143.255:1935/rtmplive/room";
        String url = "rtmp://livepush.changguwen.com/changdao/fancyRoom?auth_key=1554106513-c1f6b59621d845ada67f68929f2e98cd-0-92e59e323431c8cc4cd327ad71746b83";
//        String url="rtmp://192.168.2.1:1935/rtmplive/room";


        config.setUrl(url);
        livePusher = new SmartLivePusher();
        livePusher.init(this, config);
        LinkedList<BaseHardVideoFilter> files = new LinkedList<>();
        files.add(new GPUImageCompatibleFilter(new GPUImageBeautyFilter()));

        int left = 0;
        int right = 0;
        int top = 0;
        int bottom = 0;
        int width = ScreenUtils.getScreenWidth();
        int height = ScreenUtils.getScreenHeight();
        Bitmap bitmap = null;
        TextView textView = new TextView(this);
        textView.setText("Smart");
        textView.setTextSize(30);
//        bitmap=loadBitmapFromView(textView);
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.live);
        if (bitmap == null) {
            LogUtils.e("bitmap  is null");
        } else {
            LogUtils.e("bitmap  is  not null");

        }
        top = 50;
        bottom = 50 + 100;
        if (ORIENTATION_LANDSCAPE_HOME_RIGHT == config.getDirection() || ORIENTATION_LANDSCAPE_HOME_LEFT == config.getDirection()) {
            left = Math.max(width, height) - 100 - 50;
            right = left + 100;

        } else {
            left = Math.min(width, height) - 100 - 50;
            right = left + 100;

        }
        files.add(new WatermarkFilter(bitmap, new Rect(left, top, right, bottom)));
        livePusher.setHardVideoFileter(new HardVideoGroupFilter(files));
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


    /**
     * 获取View的Bitmap的方法
     * 自己在Canvas上画图，并得到Canvas上的Bitmap
     *
     * @param v
     * @return
     */
    public Bitmap loadBitmapFromView(View v) {
        if (v == null || v.getWidth() <= 0 || v.getHeight() <= 0) {
            return null;
        }
        Bitmap screenshot;
        screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(screenshot);
        canvas.translate(-v.getScrollX(), -v.getScrollY());//我们在用滑动View获得它的Bitmap时候，获得的是整个View的区域（包括隐藏的），如果想得到当前区域，需要重新定位到当前可显示的区域
        v.draw(canvas);// 将 view 画到画布上
        return screenshot;
    }
}
