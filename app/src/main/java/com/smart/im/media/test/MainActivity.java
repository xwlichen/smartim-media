package com.smart.im.media.test;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.smart.im.media.test.util.PermissionsUtils;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_CODE_PERMISSIONS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.textView);
//        String s = FFmpegBridge.test();
//        textView.setText(s);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA};
                    PermissionsUtils.checkAndRequestMorePermissions(MainActivity.this, PERMISSIONS, REQUEST_CODE_PERMISSIONS,
                            new PermissionsUtils.PermissionRequestSuccessCallBack() {

                                @Override
                                public void onHasPermission() {
                                    startActivity(new Intent(MainActivity.this, LiveTextureActivity.class));

                                }
                            });
                }

            }
        });
    }
}
