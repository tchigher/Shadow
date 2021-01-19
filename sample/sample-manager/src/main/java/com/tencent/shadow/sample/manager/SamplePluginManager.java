package com.tencent.shadow.sample.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.tencent.shadow.core.manager.installplugin.InstalledPlugin;
import com.tencent.shadow.dynamic.host.EnterCallback;
import com.tencent.shadow.sample.constant.Constant;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.tencent.shadow.sample.constant.Constant.KEY__TARGET_PLUGIN_APP__MGMOVIE;

public class SamplePluginManager extends FastPluginManager {

    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    private final Context mCurrentContext;

    public SamplePluginManager(Context context) {
        super(context);
        mCurrentContext = context;
    }

    /**
     * @return PluginManager 实现的别名，用于区分不同 PluginManager 实现的数据存储路径
     */
    @Override
    protected String getName() {
        return "samplePluginManager";
    }

    /**
     * @return 宿主 so 的 ABI. 插件必须和宿主使用相同的 ABI
     */
    @Override
    public String getAbi() {
        return "armeabi-v7a";
    }

    /**
     * @return 宿主中注册的 PluginProcessService 实现的类名
     */
    @Override
    protected String getPluginProcessServiceName(String partKey) {
        if (KEY__TARGET_PLUGIN_APP__MGMOVIE.equals(partKey)) {
            return "com.tencent.shadow.sample.host.PluginMgMovieProcessPPS"; // 在这里支持多个插件
        } else {
            // 如果有默认 PPS，可用 return 代替 throw
            throw new IllegalArgumentException("unexpected plugin load request: " + partKey);
        }
    }

    @Override
    public void enter(
            final Context context,
            long fromId,
            Bundle bundle,
            final EnterCallback callback
    ) {
        if (fromId == Constant.FROM_ID_NOOP) {
            // do nothing
        } else if (fromId == Constant.FROM_ID_START_ACTIVITY) {
            onStartActivity(context, bundle, callback);
        } else {
            throw new IllegalArgumentException("不认识的 fromId == " + fromId);
        }
    }

    private void onStartActivity(
            final Context context,
            Bundle bundle,
            final EnterCallback callback
    ) {
        final String pluginsZipFileAbsolutePath = bundle.getString(Constant.KEY__PLUGINS_ZIP_FILE__ABSOLUTE_PATH);
        final String targetPluginApp = bundle.getString(Constant.KEY__TARGET_PLUGIN_APP);
        final String targetPluginActivityClassFullName = bundle.getString(Constant.KEY__TARGET_PLUGIN_ACTIVITY__CLASS_FULL_NAME);
        if (targetPluginActivityClassFullName == null) {
            throw new NullPointerException("classFullName == null");
        }

        final Bundle targetPluginActivityIntentExtras = bundle.getBundle(Constant.KEY__TARGET_PLUGIN_ACTIVITY__INTENT_EXTRAS);

        if (callback != null) {
            final View view = LayoutInflater.from(mCurrentContext).inflate(R.layout.activity_load_plugin, null);
            callback.onShowLoadingView(view);
        }

        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    InstalledPlugin installedPlugins = installPlugin(pluginsZipFileAbsolutePath, null, true);

                    Intent targetPluginActivityIntent = new Intent();
                    targetPluginActivityIntent.setClassName(
                            context.getPackageName(),
                            targetPluginActivityClassFullName
                    );
                    if (targetPluginActivityIntentExtras != null) {
                        targetPluginActivityIntent.replaceExtras(targetPluginActivityIntentExtras);
                    }

                    startPluginActivity(installedPlugins, targetPluginApp, targetPluginActivityIntent);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                if (callback != null) {
                    callback.onCloseLoadingView();
                }
            }
        });
    }

}
