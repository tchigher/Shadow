package com.tencent.shadow.dynamic.loader;

import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

public interface PluginLoader {

    String DESCRIPTOR = PluginLoader.class.getName();

    int TRANSACTION_loadPlugin = (IBinder.FIRST_CALL_TRANSACTION);
    int TRANSACTION_getLoadedPlugin = (IBinder.FIRST_CALL_TRANSACTION + 1);
    int TRANSACTION_callApplicationOnCreate = (IBinder.FIRST_CALL_TRANSACTION + 2);
    int TRANSACTION_convertActivityIntent = (IBinder.FIRST_CALL_TRANSACTION + 3);
    int TRANSACTION_startPluginService = (IBinder.FIRST_CALL_TRANSACTION + 4);
    int TRANSACTION_stopPluginService = (IBinder.FIRST_CALL_TRANSACTION + 5);
    int TRANSACTION_bindPluginService = (IBinder.FIRST_CALL_TRANSACTION + 6);
    int TRANSACTION_unbindService = (IBinder.FIRST_CALL_TRANSACTION + 7);
    int TRANSACTION_startActivityInPluginProcess = (IBinder.FIRST_CALL_TRANSACTION + 8);

    void loadPlugin(
            @NonNull String partKey
    ) throws RemoteException;

    @SuppressWarnings("rawtypes")
    Map getLoadedPlugins(
    ) throws RemoteException;

    void callApplicationOnCreate(
            @NonNull String partKey
    ) throws RemoteException;

    Intent convertActivityIntent(
            @NonNull Intent pluginActivityIntent
    ) throws RemoteException;

    ComponentName startPluginService(
            @NonNull Intent pluginServiceIntent
    ) throws RemoteException;

    boolean stopPluginService(
            @NonNull Intent pluginServiceIntent
    ) throws RemoteException;

    boolean bindPluginService(
            @NonNull Intent pluginServiceIntent,
            @Nullable PluginServiceConnection pluginServiceConnection,
            @NonNull Integer flags
    ) throws RemoteException;

    void unbindPluginService(
            @Nullable PluginServiceConnection pluginServiceConnection
    ) throws RemoteException;

    void startActivityInPluginProcess(
            @NonNull Intent intent
    ) throws RemoteException;

}
