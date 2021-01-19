package com.tencent.shadow.sample.host;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.tencent.shadow.sample.constant.Constant;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.TestHostTheme);

        LinearLayout rootView = new LinearLayout(this);
        rootView.setOrientation(LinearLayout.VERTICAL);

        TextView tvInfo = new TextView(this);
        tvInfo.setText(R.string.main_activity__info);
        rootView.addView(tvInfo);

        final Spinner spinnerTargetPluginApp = new Spinner(this);
        ArrayAdapter<String> targetPluginAppAdapter = new ArrayAdapter<>(this, R.layout.adapter__target_plugin_app);
//        targetPluginAppAdapter.addAll(Constant.KEY__TARGET_PLUGIN_APP__ONE, Constant.KEY__TARGET_PLUGIN_APP__TWO, Constant.KEY__TARGET_PLUGIN_APP__MGMOVIE);
        targetPluginAppAdapter.addAll(Constant.KEY__TARGET_PLUGIN_APP__MGMOVIE);
        spinnerTargetPluginApp.setAdapter(targetPluginAppAdapter);
        rootView.addView(spinnerTargetPluginApp);

        Button btnStartPluginApp = new Button(this);
        btnStartPluginApp.setText(R.string.main_activity__start_plugin_app);
        btnStartPluginApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String targetPluginAppKey = (String) spinnerTargetPluginApp.getSelectedItem();

                Intent intent = new Intent(MainActivity.this, PluginLoadActivity.class);
                intent.putExtra(Constant.KEY__TARGET_PLUGIN_APP, targetPluginAppKey);
                switch (targetPluginAppKey) {
                    /// 为了演示多进程多插件
                    /// 其实两个插件の内容完全一样, 除了其所在进程
//                    case Constant.KEY__TARGET_PLUGIN_APP__ONE:
//                    case Constant.KEY__TARGET_PLUGIN_APP__TWO:
                    case Constant.KEY__TARGET_PLUGIN_APP__MGMOVIE:
                        intent.putExtra(
                                Constant.KEY__TARGET_PLUGIN_ACTIVITY__CLASS_FULL_NAME,
//                                "com.tencent.shadow.sample.plugin.app.lib.gallery.splash.SplashActivity"
                                "com.cmvideo.migumovie.activity"
                        );
                        break;
                }

                startActivity(intent);
            }
        });
        rootView.addView(btnStartPluginApp);

        setContentView(rootView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
