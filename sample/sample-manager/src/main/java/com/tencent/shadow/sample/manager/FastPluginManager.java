package com.tencent.shadow.sample.manager;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
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

public abstract class FastPluginManager extends PluginManagerThatUseDynamicLoader {

    private final ExecutorService mFixedPool = Executors.newFixedThreadPool(4);

    public FastPluginManager(Context context) {
        super(context);
    }

    @SuppressWarnings("rawtypes")
    public InstalledPlugin installPlugin(
            String pluginsZipFileAbsolutePath,
            @Nullable String pluginsZipFileHash,
            boolean odex
    ) throws IOException, JSONException, InterruptedException, ExecutionException {
        final PluginConfig pluginConfig = installPluginsFromZipFile(
                new File(pluginsZipFileAbsolutePath),
                pluginsZipFileHash
        );
        final String pluginsUUID = pluginConfig.UUID;

        List<Future> futures = new LinkedList<>();

        if (pluginConfig.runTime != null && pluginConfig.pluginLoader != null) {
            Future odexRuntime = mFixedPool.submit(new Callable() {
                @Override
                public Object call() throws Exception {
                    odexPluginLoaderOrRunTime(
                            pluginsUUID,
                            InstalledType.TYPE_PLUGIN_RUNTIME,
                            pluginConfig.runTime.file
                    );
                    return null;
                }
            });
            futures.add(odexRuntime);

            Future odexLoader = mFixedPool.submit(new Callable() {
                @Override
                public Object call() throws Exception {
                    odexPluginLoaderOrRunTime(
                            pluginsUUID,
                            InstalledType.TYPE_PLUGIN_LOADER,
                            pluginConfig.pluginLoader.file
                    );
                    return null;
                }
            });
            futures.add(odexLoader);
        }

        for (Map.Entry<String, PluginConfig.PluginFileInfo> plugin : pluginConfig.plugins.entrySet()) {
            final String pluginAppPartKey = plugin.getKey();
            final File pluginApkFile = plugin.getValue().file;
            Future extractedSo = mFixedPool.submit(new Callable() {
                @Override
                public Object call() throws Exception {
                    extractSo(pluginsUUID, pluginAppPartKey, pluginApkFile);
                    return null;
                }
            });
            futures.add(extractedSo);

            if (odex) {
                Future odexedPlugin = mFixedPool.submit(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        odexPlugin(pluginsUUID, pluginAppPartKey, pluginApkFile);
                        return null;
                    }
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
            InstalledPlugin installedPlugins,
            String pluginAppPartKey,
            Intent pluginActivityIntent
    ) throws RemoteException, TimeoutException, FailedException {
        Intent intent = convertActivityIntent(installedPlugins, pluginAppPartKey, pluginActivityIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mPluginLoader.startActivityInPluginProcess(intent);
    }

    @SuppressWarnings("rawtypes")
    public Intent convertActivityIntent(
            InstalledPlugin installedPlugins,
            String pluginAppPartKey,
            Intent pluginActivityIntent
    ) throws RemoteException, TimeoutException, FailedException {
        loadPlugin(installedPlugins.UUID, pluginAppPartKey);
        Map loadedPlugins = mPluginLoader.getLoadedPlugin();
        Boolean isCall = (Boolean) loadedPlugins.get(pluginAppPartKey);
        if (isCall == null || !isCall) {
            mPluginLoader.callApplicationOnCreate(pluginAppPartKey);
        }
        return mPluginLoader.convertActivityIntent(pluginActivityIntent);
    }

    @SuppressWarnings("rawtypes")
    private void loadPlugin(
            String uuid,
            String pluginAppPartKey
    ) throws RemoteException, TimeoutException, FailedException {
        loadPluginLoaderAndRuntime(uuid, pluginAppPartKey);
        Map map = mPluginLoader.getLoadedPlugin();
        if (!map.containsKey(pluginAppPartKey)) {
            mPluginLoader.loadPlugin(pluginAppPartKey);
        }
    }

    private void loadPluginLoaderAndRuntime(
            String uuid,
            String pluginAppPartKey
    ) throws RemoteException, TimeoutException, FailedException {
        if (mPpsController == null) {
            bindPluginProcessService(getPluginProcessServiceName(pluginAppPartKey));
            waitServiceConnected(10, TimeUnit.SECONDS);
        }
        loadRunTime(uuid);
        loadPluginLoader(uuid);
    }

    protected abstract String getPluginProcessServiceName(String partKey);

}
