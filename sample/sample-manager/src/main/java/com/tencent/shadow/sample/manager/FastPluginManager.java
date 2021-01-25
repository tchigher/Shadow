package com.tencent.shadow.sample.manager;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tencent.shadow.core.manager.installplugin.InstalledPlugin;
import com.tencent.shadow.core.manager.installplugin.InstalledType;
import com.tencent.shadow.core.manager.installplugin.PluginConfig;
import com.tencent.shadow.dynamic.host.FailedException;
import com.tencent.shadow.dynamic.manager.PluginManagerThatUseDynamicLoader;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class FastPluginManager
        extends PluginManagerThatUseDynamicLoader {

    private final ExecutorService mFixedThreadPool = Executors.newFixedThreadPool(4);

    public FastPluginManager(
            @NonNull Context context
    ) {
        super(context);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @NonNull
    public InstalledPlugin installPlugin(
            @NonNull String pluginZipFileAbsolutePath,
            @Nullable String pluginsZipFileHash,
            @NonNull Boolean odex
    ) throws IOException, JSONException, InterruptedException, ExecutionException {
        final PluginConfig pluginConfig = installPluginsFromZipFile(
                new File(pluginZipFileAbsolutePath),
                pluginsZipFileHash
        );
        final String pluginUUID = pluginConfig.UUID;

        List<Future> futures = new LinkedList<>();

        if (pluginConfig.runTime != null
                && pluginConfig.pluginLoader != null) {
            Future odexRuntime = mFixedThreadPool.submit((Callable) () -> {
                odexPluginLoaderOrRunTime(
                        pluginUUID,
                        InstalledType.TYPE_PLUGIN_RUNTIME,
                        pluginConfig.runTime.file
                );
                return null;
            });
            futures.add(odexRuntime);

            Future odexLoader = mFixedThreadPool.submit((Callable) () -> {
                odexPluginLoaderOrRunTime(
                        pluginUUID,
                        InstalledType.TYPE_PLUGIN_LOADER,
                        pluginConfig.pluginLoader.file
                );
                return null;
            });
            futures.add(odexLoader);
        }

        for (Map.Entry<String, PluginConfig.PluginFileInfo> plugin : pluginConfig.plugins.entrySet()) {
            final String pluginAppPartKey = plugin.getKey();
            final File pluginApkFile = plugin.getValue().file;
            Future extractedSo = mFixedThreadPool.submit((Callable) () -> {
                extractSo(pluginUUID, pluginAppPartKey, pluginApkFile);
                return null;
            });
            futures.add(extractedSo);

            if (odex) {
                Future odexedPlugin = mFixedThreadPool.submit((Callable) () -> {
                    odexPlugin(pluginUUID, pluginAppPartKey, pluginApkFile);
                    return null;
                });
                futures.add(odexedPlugin);
            }
        }

        for (Future future : futures) {
            future.get();
        }

        onPluginsInstallCompleted(pluginConfig);

        return getInstalledPlugins(1).get(0);
    }

    public void startPluginActivity(
            @NonNull InstalledPlugin installedPlugin,
            @NonNull String pluginAppPartKey,
            @NonNull Intent pluginActivityIntent
    ) throws RemoteException, TimeoutException, FailedException {
        Intent intent = convertActivityIntent(installedPlugin, pluginAppPartKey, pluginActivityIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mPluginLoader.startActivityInPluginProcess(intent);
    }

    @SuppressWarnings("rawtypes")
    public Intent convertActivityIntent(
            @NonNull InstalledPlugin installedPlugin,
            @NonNull String pluginAppPartKey,
            @NonNull Intent pluginActivityIntent
    ) throws RemoteException, TimeoutException, FailedException {
        loadPlugin(installedPlugin.UUID, pluginAppPartKey);

        Map loadedPlugins = mPluginLoader.getLoadedPlugins();
        Boolean hasCalled = (Boolean) loadedPlugins.get(pluginAppPartKey);
        if (hasCalled == null || !hasCalled) {
            mPluginLoader.callApplicationOnCreate(pluginAppPartKey);
        }

        return mPluginLoader.convertActivityIntent(pluginActivityIntent);
    }

    @SuppressWarnings("rawtypes")
    private void loadPlugin(
            @NonNull String uuid,
            @NonNull String pluginAppPartKey
    ) throws RemoteException, TimeoutException, FailedException {
        loadPluginLoaderAndRuntime(uuid, pluginAppPartKey);
        Map map = mPluginLoader.getLoadedPlugins();
        if (!map.containsKey(pluginAppPartKey)) {
            mPluginLoader.loadPlugin(pluginAppPartKey);
        }
    }

    private void loadPluginLoaderAndRuntime(
            @NonNull String uuid,
            @NonNull String pluginAppPartKey
    ) throws RemoteException, TimeoutException, FailedException {
        if (mPPSController == null) {
            bindPluginProcessService(getPluginProcessServiceName(pluginAppPartKey));
            waitServiceConnected(10, TimeUnit.SECONDS);
        }
        loadRunTime(uuid);
        loadPluginLoader(uuid);
    }

    protected abstract String getPluginProcessServiceName(
            @NonNull String partKey
    );

}
