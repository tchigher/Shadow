package com.tencent.shadow.sample.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

    public SamplePluginManager(
            @NonNull Context context
    ) {
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
    protected String getPluginProcessServiceName(
            @NonNull String partKey
    ) {
        if (KEY__TARGET_PLUGIN_APP__MGMOVIE.equals(partKey)) {
            return "com.tencent.shadow.sample.host.PPS4MgMovie"; // 在这里支持多个插件
        } else {
            // 如果有默认的 PPS，可用 return 代替 throw
            throw new IllegalArgumentException("unexpected plugin load request: " + partKey);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
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

    @SuppressLint("InflateParams")
    private void onStartActivity(
            final Context context,
            Bundle bundle,
            final EnterCallback callback
    ) {
        final String pluginZipFileAbsolutePath =
                bundle.getString(Constant.KEY__PLUGIN_ZIP_FILE__ABSOLUTE_PATH);
        final String targetPluginApp =
                bundle.getString(Constant.KEY__TARGET_PLUGIN_APP);
        final String targetPluginActivityClassName =
                bundle.getString(Constant.KEY__TARGET_PLUGIN_ACTIVITY__CLASS_NAME);

        if (targetPluginActivityClassName == null) {
            throw new NullPointerException("classFullName == null");
        }

        final Bundle targetPluginActivityIntentExtras =
                bundle.getBundle(Constant.KEY__TARGET_PLUGIN_ACTIVITY__INTENT_EXTRAS);

        if (callback != null) {
            final View view = LayoutInflater.from(mCurrentContext)
                    .inflate(R.layout.activity__load_plugin, null);
            callback.onShowLoadingView(view);
        }

        mExecutorService.execute(() -> {
            try {
                InstalledPlugin installedPlugins = installPlugin(
                        pluginZipFileAbsolutePath,
                        null,
                        true
                );

                Intent targetPluginActivityIntent = new Intent();
                targetPluginActivityIntent.setClassName(
                        context.getPackageName(),
                        targetPluginActivityClassName
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
        });
    }

}
