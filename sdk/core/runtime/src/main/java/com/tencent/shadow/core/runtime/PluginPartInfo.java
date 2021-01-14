package com.tencent.shadow.core.runtime;

import android.content.res.Resources;

public class PluginPartInfo {

    public ShadowApplication application;

    public Resources resources;

    public ClassLoader classLoader;

    PluginPackageManager packageManager;


    public PluginPartInfo(ShadowApplication application, Resources resources, ClassLoader classLoader, PluginPackageManager packageManager) {
        this.application = application;
        this.resources = resources;
        this.classLoader = classLoader;
        this.packageManager = packageManager;
    }
}
