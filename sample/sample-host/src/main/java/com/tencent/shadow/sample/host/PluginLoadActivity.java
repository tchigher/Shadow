package com.tencent.shadow.sample.host;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.shadow.dynamic.host.EnterCallback;
import com.tencent.shadow.sample.constant.Constant;

public class PluginLoadActivity extends Activity {

    private ViewGroup mViewGroup;

    private final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_load);
        mViewGroup = findViewById(R.id.container);

        startPlugin();
    }


    public void startPlugin() {
        PluginHelper.getInstance().mSinglePool.execute(new Runnable() {
            @Override
            public void run() {
                HostApplication.getApp().loadPluginManager(PluginHelper.getInstance().mPluginManagerFile);

                Bundle bundle = new Bundle();
                bundle.putString(Constant.KEY__PLUGINS_ZIP_FILE__ABSOLUTE_PATH, PluginHelper.getInstance().mPluginZipFile.getAbsolutePath());
                bundle.putString(Constant.KEY__TARGET_PLUGIN_APP, getIntent().getStringExtra(Constant.KEY__TARGET_PLUGIN_APP));
                bundle.putString(Constant.KEY__TARGET_PLUGIN_ACTIVITY__CLASS_FULL_NAME, getIntent().getStringExtra(Constant.KEY__TARGET_PLUGIN_ACTIVITY__CLASS_FULL_NAME));

                HostApplication.getApp().getPluginManager().enter(
                        PluginLoadActivity.this,
                        Constant.FROM_ID_START_ACTIVITY,
                        bundle,
                        new EnterCallback() {
                            @Override
                            public void onShowLoadingView(final View view) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mViewGroup.addView(view);
                                    }
                                });
                            }

                            @Override
                            public void onCloseLoadingView() {
                                finish();
                            }

                            @Override
                            public void onEnterComplete() {
                            }
                        }
                );
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mViewGroup.removeAllViews();
    }

}
