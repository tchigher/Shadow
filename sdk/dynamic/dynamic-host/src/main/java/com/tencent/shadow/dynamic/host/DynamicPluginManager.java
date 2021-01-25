package com.tencent.shadow.dynamic.host;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;

import java.io.File;

public final class DynamicPluginManager
        implements PluginManager {

    final private PluginManagerUpdater mPluginManagerUpdater;
    private PluginManager mPluginManager;

    private long mLastModified;

    private static final Logger mLogger = LoggerFactory.getLogger(DynamicPluginManager.class);

    public DynamicPluginManager(
            PluginManagerUpdater pluginManagerUpdater
    ) {
        if (pluginManagerUpdater.getLocalLatestApk() == null) {
            throw new IllegalArgumentException("构造 DynamicPluginManager 时传入的 PluginManagerUpdater "
                    + "必须已经有本地文件, getLocalLatestApk() != null");
        }

        mPluginManagerUpdater = pluginManagerUpdater;
    }

    @Override
    public void enter(
            @NonNull Context context,
            @NonNull Long fromId,
            @NonNull Bundle bundle,
            @Nullable PluginAppEnterCallback callback
    ) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("enter fromId:" + fromId + " callback:" + callback);
        }

        updatePluginManagerImpl(context);

        mPluginManager.enter(context, fromId, bundle, callback);

        mPluginManagerUpdater.update();
    }

    public void release(
    ) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("release");
        }

        if (mPluginManager != null) {
            mPluginManager.onDestroy();
            mPluginManager = null;
        }
    }

    private void updatePluginManagerImpl(
            Context context
    ) {
        File localLatestPluginManagerApk = mPluginManagerUpdater.getLocalLatestApk();
        long lastModified = localLatestPluginManagerApk.lastModified();

        if (mLogger.isInfoEnabled()) {
            mLogger.info("mLastModified != lastModified: " + (mLastModified != lastModified));
        }

        if (mLastModified != lastModified) {
            PluginManagerLoaderImpl pluginManagerLoaderImpl = new PluginManagerLoaderImpl(
                    context,
                    localLatestPluginManagerApk
            );
            PluginManager newImpl = pluginManagerLoaderImpl.load();
            Bundle state;
            if (mPluginManager != null) {
                state = new Bundle();
                mPluginManager.onSaveInstanceState(state);
                mPluginManager.onDestroy();
            } else {
                state = null;
            }
            newImpl.onCreate(state);
            mPluginManager = newImpl;
            mLastModified = lastModified;
        }
    }

}
