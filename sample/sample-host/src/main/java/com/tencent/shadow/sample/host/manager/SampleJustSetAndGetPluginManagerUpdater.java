package com.tencent.shadow.sample.host.manager;

import android.support.annotation.NonNull;

import com.tencent.shadow.dynamic.host.PluginManagerUpdater;

import java.io.File;

public class SampleJustSetAndGetPluginManagerUpdater
        implements PluginManagerUpdater {

    final private File mPluginManagerApk;

    SampleJustSetAndGetPluginManagerUpdater(
            @NonNull File pluginManagerApk
    ) {
        mPluginManagerApk = pluginManagerApk;
    }

    @Override
    public File getLocalLatestApk() {
        return mPluginManagerApk;
    }

}