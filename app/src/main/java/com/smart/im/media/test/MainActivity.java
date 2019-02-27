package com.smart.im.media.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.smart.im.media.FFmpegBridge;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView=findViewById(R.id.textView);
        String s= FFmpegBridge.test();
        textView.setText(s);
    }
}
