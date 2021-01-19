package com.tencent.shadow.dynamic.host;

import android.app.Application;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.shadow.core.common.InstalledApk;

import java.io.File;
import java.util.HashMap;

import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_FILE_NOT_FOUND_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_RELOAD_LOADER_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_RELOAD_RUNTIME_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_RESET_UUID_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_RUNTIME_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_UUID_MANAGER_DEAD_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_UUID_MANAGER_NULL_EXCEPTION;

public class MultiLoaderPluginProcessService extends BasePluginProcessService {

    static final ActivityHolder sActivityHolder = new ActivityHolder();
    private final MultiLoaderPpsBinder mPpsControllerBinder = new MultiLoaderPpsBinder(this);

    private final HashMap<String, String> mUUIDMap = new HashMap<>();
    private final HashMap<String, UUIDManager> mUUIDManagerMap = new HashMap<>();
    private final HashMap<String, PluginLoaderImpl> mPluginLoaderMap = new HashMap<>();
    private final HashMap<String, Boolean> mRuntimeLoadedMap = new HashMap<>();

    public static Application.ActivityLifecycleCallbacks getActivityHolder() {
        return sActivityHolder;
    }

    public static MultiLoaderPpsController wrapBinder(
            IBinder ppsBinder
    ) {
        return new MultiLoaderPpsController(ppsBinder);
    }

    @Override
    public IBinder onBind(
            Intent intent
    ) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onBind: " + this);
        }
        return mPpsControllerBinder;
    }

    synchronized void loadRuntimeForPlugin(
            String pluginKey,
            String uuid
    ) throws FailedException {
        String logIdentity = "pluginKey = " + pluginKey + ", uuid = " + uuid;
        if (mLogger.isInfoEnabled()) {
            mLogger.info("loadRuntimeForPlugin: " + logIdentity);
        }

        UUIDManager uuidManager = checkUUIDManagerNotNull(pluginKey);
        addUUIDForPlugin(pluginKey, uuid);
        if (isRuntimeLoaded(pluginKey)) {
            throw new FailedException(ERROR_CODE_RELOAD_RUNTIME_EXCEPTION, "重复调用 loadRuntime, " + logIdentity);
        }
        try {

            InstalledApk installedApk;
            try {
                installedApk = uuidManager.getRuntime(uuid);
            } catch (RemoteException e) {
                Log.i("PluginProcessService", "uuidManager.getRuntime new FailedException");
                throw new FailedException(ERROR_CODE_UUID_MANAGER_DEAD_EXCEPTION, e.getMessage());
            } catch (NotFoundException e) {
                Log.i("PluginProcessService", "uuidManager.getRuntime new NotFoundException");
                throw new FailedException(
                        ERROR_CODE_FILE_NOT_FOUND_EXCEPTION,
                        "pluginKey = " + pluginKey + ", uuid = " + uuid + " 的 Runtime 没有找到. Cause: " + e.getMessage()
                );
            }

            InstalledApk installedRuntimeApk = new InstalledApk(installedApk.mApkFilePath, installedApk.odexPath, installedApk.libraryPath);
            MultiDynamicContainer.loadContainerApk(pluginKey, installedRuntimeApk);
            markRuntimeLoaded(pluginKey);
        } catch (RuntimeException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("loadRuntimeForPlugin 发生 RuntimeException: ", e);
            }
            throw new FailedException(e);
        }
    }

    synchronized void loadPluginLoaderForPlugin(
            String pluginKey,
            String uuid
    ) throws FailedException {
        String logIdentity = "pluginKey = " + pluginKey + ", uuid = " + uuid;
        if (mLogger.isInfoEnabled()) {
            mLogger.info("loadPluginLoader: " + logIdentity);
        }
        UUIDManager uuidManager = checkUUIDManagerNotNull(pluginKey);
        addUUIDForPlugin(pluginKey, uuid);
        if (mPluginLoaderMap.get(pluginKey) != null) {
            throw new FailedException(ERROR_CODE_RELOAD_LOADER_EXCEPTION, "重复调用 loadPluginLoader");
        }
        try {
            InstalledApk installedApk;
            try {
                installedApk = uuidManager.getPluginLoader(uuid);
                if (mLogger.isInfoEnabled()) {
                    mLogger.info("取出 " + logIdentity + " 的 Loader apk: " + installedApk.mApkFilePath);
                }
            } catch (RemoteException e) {
                if (mLogger.isErrorEnabled()) {
                    mLogger.error("获取 Loader Apk 失败: ", e);
                }
                throw new FailedException(ERROR_CODE_UUID_MANAGER_DEAD_EXCEPTION, e.getMessage());
            } catch (NotFoundException e) {
                throw new FailedException(
                        ERROR_CODE_FILE_NOT_FOUND_EXCEPTION,
                        logIdentity + " 的 PluginLoader 没有找到. Cause: " + e.getMessage()
                );
            }
            File file = new File(installedApk.mApkFilePath);
            if (!file.exists()) {
                throw new FailedException(ERROR_CODE_FILE_NOT_FOUND_EXCEPTION, file.getAbsolutePath() + " 文件不存在");
            }

            PluginLoaderImpl pluginLoader = new LoaderImplLoader().load(installedApk, uuid, getApplicationContext());
            pluginLoader.setUUIDManager(uuidManager);
            mPluginLoaderMap.put(pluginKey, pluginLoader);
        } catch (RuntimeException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("loadPluginLoader 发生 RuntimeException: ", e);
            }
            throw new FailedException(e);
        } catch (FailedException e) {
            throw e;
        } catch (Exception e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("loadPluginLoader 发生 Exception: ", e);
            }
            String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            throw new FailedException(ERROR_CODE_RUNTIME_EXCEPTION, "加载动态实现失败. Cause: " + msg);
        }
    }

    synchronized void setUUIDManagerForPlugin(
            String pluginKey,
            UUIDManager uuidManager
    ) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("setUUIDManagerForPlugin pluginKey = " + pluginKey + ", uuidManager = " + uuidManager);
        }
        mUUIDManagerMap.put(pluginKey, uuidManager);
        PluginLoaderImpl pluginLoader = mPluginLoaderMap.get(pluginKey);
        if (pluginLoader != null) {
            if (mLogger.isInfoEnabled()) {
                mLogger.info("更新 PluginLoader 的 uuidManager");
            }
            pluginLoader.setUUIDManager(uuidManager);
        }
    }

    synchronized PpsStatus getPpsStatusForPlugin(
            String pluginKey
    ) {
        return new PpsStatus(
                mUUIDMap.get(pluginKey),
                isRuntimeLoaded(pluginKey),
                mPluginLoaderMap.get(pluginKey) != null,
                mUUIDManagerMap.get(pluginKey) != null
        );
    }

    synchronized IBinder getPluginLoaderForPlugin(
            String pluginKey
    ) {
        return mPluginLoaderMap.get(pluginKey);
    }

    void exit() {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("exit");
        }
        MultiLoaderPluginProcessService.sActivityHolder.finishAll();
        System.exit(0);
        try {
            wait();
        } catch (InterruptedException ignored) {
        }
    }

    private UUIDManager checkUUIDManagerNotNull(
            String pluginKey
    ) throws FailedException {
        UUIDManager uuidManager = mUUIDManagerMap.get(pluginKey);
        if (uuidManager == null) {
            throw new FailedException(ERROR_CODE_UUID_MANAGER_NULL_EXCEPTION, "mUUIDManager == null");
        }
        return uuidManager;
    }

    private boolean isRuntimeLoaded(
            String pluginKey
    ) {
        Boolean result = mRuntimeLoadedMap.get(pluginKey);
        return result != null && result;
    }

    private void markRuntimeLoaded(
            String pluginKey
    ) {
        mRuntimeLoadedMap.put(pluginKey, true);
    }

    private void addUUIDForPlugin(
            String pluginKey,
            String uuid
    ) throws FailedException {
        String preUUID = mUUIDMap.get(pluginKey);
        if (preUUID != null && !TextUtils.equals(uuid, preUUID)) {
            throw new FailedException(
                    ERROR_CODE_RESET_UUID_EXCEPTION,
                    "Plugin = " + pluginKey + "已设置过 uuid = " + preUUID + ", 试图设置 uuid = " + uuid
            );
        } else if (preUUID == null) {
            mUUIDMap.put(pluginKey, uuid);
        }
    }

}
