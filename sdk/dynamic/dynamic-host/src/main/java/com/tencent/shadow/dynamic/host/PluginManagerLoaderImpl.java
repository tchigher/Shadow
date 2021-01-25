package com.tencent.shadow.dynamic.host;

import android.content.Context;

import com.tencent.shadow.core.common.InstalledApk;

import java.io.File;

final class PluginManagerLoaderImpl extends ImplLoader {
    private static final String MANAGER_FACTORY_CLASS_NAME = "com.tencent.shadow.dynamic.impl.ManagerFactoryImpl";
    private static final String[] REMOTE_PLUGIN_MANAGER_INTERFACES = new String[]
            {
                    "com.tencent.shadow.core.common",
                    "com.tencent.shadow.dynamic.host"
            };
    final private Context applicationContext;
    final private InstalledApk installedApk;

    PluginManagerLoaderImpl(Context context, File apk) {
        applicationContext = context.getApplicationContext();

        File pluginManagerLoadersDir = new File(applicationContext.getFilesDir(), "shadowPluginManagerLoaders");
        File odexDir = new File(pluginManagerLoadersDir, "odex_" + Long.toString(apk.lastModified(), Character.MAX_RADIX));
        odexDir.mkdirs();

        installedApk = new InstalledApk(
                apk.getAbsolutePath(),
                odexDir.getAbsolutePath(),
                null
        );
    }

    PluginManagerImpl load() {
        ApkClassLoader apkClassLoader = new ApkClassLoader(
                installedApk,
                getClass().getClassLoader(),
                loadWhiteList(installedApk),
                1
        );

        Context pluginManagerContext = new ChangeApkContextWrapper(
                applicationContext,
                installedApk.mApkFilePath,
                apkClassLoader
        );

        try {
            ManagerFactory managerFactory = apkClassLoader.getInterface(
                    ManagerFactory.class,
                    MANAGER_FACTORY_CLASS_NAME
            );
            return managerFactory.buildManager(pluginManagerContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    String[] getCustomWhiteList() {
        return REMOTE_PLUGIN_MANAGER_INTERFACES;
    }
}
