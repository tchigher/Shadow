package com.tencent.shadow.dynamic.host;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;

import java.io.File;

public final class DynamicPluginManager implements PluginManager {

    final private PluginManagerUpdater mPluginManagerUpdater;
    private PluginManagerImpl mPluginManagerImpl;
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
            @NonNull long fromId,
            Bundle bundle,
            PluginAppEnterCallback callback
    ) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("enter fromId:" + fromId + " callback:" + callback);
        }

        updatePluginManagerImpl(context);

        mPluginManagerImpl.enter(context, fromId, bundle, callback);

        mPluginManagerUpdater.update();
    }

    public void release() {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("release");
        }
        if (mPluginManagerImpl != null) {
            mPluginManagerImpl.onDestroy();
            mPluginManagerImpl = null;
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
            PluginManagerImpl newImpl = pluginManagerLoaderImpl.load();
            Bundle state;
            if (mPluginManagerImpl != null) {
                state = new Bundle();
                mPluginManagerImpl.onSaveInstanceState(state);
                mPluginManagerImpl.onDestroy();
            } else {
                state = null;
            }
            newImpl.onCreate(state);
            mPluginManagerImpl = newImpl;
            mLastModified = lastModified;
        }
    }

    public PluginManager getManagerImpl() {
        return mPluginManagerImpl;
    }
}
