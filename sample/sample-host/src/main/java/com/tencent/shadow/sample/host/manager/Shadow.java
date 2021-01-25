package com.tencent.shadow.sample.host.manager;

import com.tencent.shadow.dynamic.host.DynamicPluginManager;
import com.tencent.shadow.dynamic.host.PluginManager;

import java.io.File;

public class Shadow {

    public static PluginManager getPluginManager(
            File pluginManagerApk
    ){
        final SampleJustSetAndGetPluginManagerUpdater sampleJustSetAndGetPluginManagerUpdater = new SampleJustSetAndGetPluginManagerUpdater(pluginManagerApk);
        File tempPm = sampleJustSetAndGetPluginManagerUpdater.getLocalLatestApk();
        if (tempPm != null) {
            return new DynamicPluginManager(sampleJustSetAndGetPluginManagerUpdater);
        }
        return null;
    }

}
