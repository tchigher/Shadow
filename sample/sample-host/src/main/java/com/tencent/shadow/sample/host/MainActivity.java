package com.tencent.shadow.sample.host;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.tencent.shadow.sample.constant.Constant;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.TestHostTheme);

        setContentView(R.layout.activity__welcome);
        findViewById(R.id.tvStartMgMovie).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoadMgMoviePluginActivity.class);
            intent.putExtra(Constant.KEY__TARGET_PLUGIN_APP, Constant.KEY__TARGET_PLUGIN_APP__MGMOVIE);
            intent.putExtra(
                    Constant.KEY__TARGET_PLUGIN_ACTIVITY__CLASS_NAME,
                    "com.cmvideo.migumovie.activity.SplashActivity"
            );

            startActivity(intent);
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        throw new RuntimeException("必须赋予权限.");
                    }
                }
            }
        }
    }

}
