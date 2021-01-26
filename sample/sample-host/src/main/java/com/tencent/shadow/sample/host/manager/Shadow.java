package com.tencent.shadow.sample.host.manager;

import com.tencent.shadow.dynamic.host.DynamicPluginManager;
import com.tencent.shadow.dynamic.host.PluginManager;

import java.io.File;

public class Shadow {

    public static PluginManager createPluginManagerWrappedByUpdater(
            File pluginManagerApk
    ) {
        final SampleJustSetAndGetPluginManagerUpdater sampleJustSetAndGetPluginManagerUpdater =
                new SampleJustSetAndGetPluginManagerUpdater(pluginManagerApk);

        File localLatestPluginManagerApk = sampleJustSetAndGetPluginManagerUpdater.getLocalLatestApk();
        if (localLatestPluginManagerApk != null) {
            return new DynamicPluginManager(sampleJustSetAndGetPluginManagerUpdater);
        }

        return null;
    }

}
