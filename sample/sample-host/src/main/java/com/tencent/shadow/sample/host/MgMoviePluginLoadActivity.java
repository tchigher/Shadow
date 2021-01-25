package com.tencent.shadow.sample.host;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.shadow.dynamic.host.PluginAppEnterCallback;
import com.tencent.shadow.sample.constant.Constant;

public class MgMoviePluginLoadActivity extends Activity {

    private ViewGroup mViewGroup;

    private final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity__load_mgmovie_plugin);
        mViewGroup = findViewById(R.id.container);

        startPlugin();
    }


    public void startPlugin() {
        PluginHelper.getInstance().mSinglePool.execute(() -> {
            HostApplication.getApp().loadPluginManager(PluginHelper.getInstance().mPluginManagerApkFile);

            Bundle bundle = new Bundle();
            bundle.putString(
                    Constant.KEY__PLUGIN_ZIP_FILE__ABSOLUTE_PATH,
                    PluginHelper.getInstance().mMgMoviePluginsZipFile.getAbsolutePath()
            );
            bundle.putString(
                    Constant.KEY__TARGET_PLUGIN_APP,
                    getIntent().getStringExtra(Constant.KEY__TARGET_PLUGIN_APP)
            );
            bundle.putString(
                    Constant.KEY__TARGET_PLUGIN_ACTIVITY__CLASS_NAME,
                    getIntent().getStringExtra(Constant.KEY__TARGET_PLUGIN_ACTIVITY__CLASS_NAME)
            );

            HostApplication.getApp().getPluginManager().enter(
                    MgMoviePluginLoadActivity.this,
                    (long) Constant.FROM_ID__START_ACTIVITY,
                    bundle,
                    new PluginAppEnterCallback() {
                        @Override
                        public void onShowLoadingView(@NonNull final View view) {
                            mHandler.post(() -> mViewGroup.addView(view));
                        }

                        @Override
                        public void onCloseLoadingView() {
                            finish();
                        }

                        @Override
                        public void onComplete() {
                        }
                    }
            );
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mViewGroup.removeAllViews();
    }

}
