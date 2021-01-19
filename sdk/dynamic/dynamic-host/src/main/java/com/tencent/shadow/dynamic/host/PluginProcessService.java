package com.tencent.shadow.dynamic.host;

import android.app.Application;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.tencent.shadow.core.common.InstalledApk;

import java.io.File;

import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_FILE_NOT_FOUND_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_RELOAD_LOADER_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_RELOAD_RUNTIME_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_RESET_UUID_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_RUNTIME_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_UUID_MANAGER_DEAD_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_UUID_MANAGER_NULL_EXCEPTION;


public class PluginProcessService extends BasePluginProcessService {

    private final PpsBinder mPpsControllerBinder = new PpsBinder(this);

    static final ActivityHolder sActivityHolder = new ActivityHolder();

    public static Application.ActivityLifecycleCallbacks getActivityHolder() {
        return sActivityHolder;
    }

    public static PpsController wrapBinder(
            IBinder ppsBinder
    ) {
        return new PpsController(ppsBinder);
    }

    @Override
    public IBinder onBind(
            Intent intent
    ) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onBind:" + this);
        }
        return mPpsControllerBinder;
    }

    private UUIDManager mUUIDManager;

    private PluginLoaderImpl mPluginLoader;

    private boolean mRuntimeLoaded = false;

    /**
     * 当前的 UUID. 一旦设置不可修改
     */
    private String mUUID = "";

    private void setUUID(
            String uuid
    ) throws FailedException {
        if (mUUID.isEmpty()) {
            mUUID = uuid;
        } else if (!mUUID.equals(uuid)) {
            throw new FailedException(ERROR_CODE_RESET_UUID_EXCEPTION, "已设置过 uuid == " + mUUID + ", 试图设置 uuid == " + uuid);
        }
    }

    private void checkUUIDManagerNotNull() throws FailedException {
        if (mUUIDManager == null) {
            throw new FailedException(ERROR_CODE_UUID_MANAGER_NULL_EXCEPTION, "mUUIDManager == null");
        }
    }

    void loadRuntime(
            String uuid
    ) throws FailedException {
        checkUUIDManagerNotNull();
        setUUID(uuid);
        if (mRuntimeLoaded) {
            throw new FailedException(ERROR_CODE_RELOAD_RUNTIME_EXCEPTION, "重复调用 loadRuntime");
        }
        try {
            if (mLogger.isInfoEnabled()) {
                mLogger.info("loadRuntime uuid:" + uuid);
            }
            InstalledApk installedApk;
            try {
                installedApk = mUUIDManager.getRuntime(uuid);
            } catch (RemoteException e) {
                throw new FailedException(ERROR_CODE_UUID_MANAGER_DEAD_EXCEPTION, e.getMessage());
            } catch (NotFoundException e) {
                throw new FailedException(ERROR_CODE_FILE_NOT_FOUND_EXCEPTION, "uuid == " + uuid + " 的 Runtime 没有找到. Cause: " + e.getMessage());
            }

            InstalledApk installedRuntimeApk = new InstalledApk(
                    installedApk.mApkFilePath,
                    installedApk.odexPath,
                    installedApk.libraryPath
            );
            boolean loaded = DynamicRuntime.loadRuntime(installedRuntimeApk);
            if (loaded) {
                DynamicRuntime.saveLastRuntimeInfo(this, installedRuntimeApk);
            }
            mRuntimeLoaded = true;
        } catch (RuntimeException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("loadRuntime 发生 RuntimeException: ", e);
            }
            throw new FailedException(e);
        }
    }

    void loadPluginLoader(
            String uuid
    ) throws FailedException {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("loadPluginLoader uuid: " + uuid + ", mPluginLoader: " + mPluginLoader);
        }
        checkUUIDManagerNotNull();
        setUUID(uuid);
        if (mPluginLoader != null) {
            throw new FailedException(ERROR_CODE_RELOAD_LOADER_EXCEPTION, "重复调用 loadPluginLoader ");
        }
        try {
            InstalledApk installedApk;
            try {
                installedApk = mUUIDManager.getPluginLoader(uuid);
                if (mLogger.isInfoEnabled()) {
                    mLogger.info("取出 uuid== " + uuid + "的 Loader apk: " + installedApk.mApkFilePath);
                }
            } catch (RemoteException e) {
                if (mLogger.isErrorEnabled()) {
                    mLogger.error("获取 Loader Apk 失败: ", e);
                }
                throw new FailedException(ERROR_CODE_UUID_MANAGER_DEAD_EXCEPTION, e.getMessage());
            } catch (NotFoundException e) {
                throw new FailedException(ERROR_CODE_FILE_NOT_FOUND_EXCEPTION, "uuid == " + uuid + " 的 PluginLoader 没有找到. Cause: " + e.getMessage());
            }
            File file = new File(installedApk.mApkFilePath);
            if (!file.exists()) {
                throw new FailedException(ERROR_CODE_FILE_NOT_FOUND_EXCEPTION, file.getAbsolutePath() + " 文件不存在");
            }

            PluginLoaderImpl pluginLoader = new LoaderImplLoader().load(installedApk, uuid, getApplicationContext());
            pluginLoader.setUUIDManager(mUUIDManager);
            mPluginLoader = pluginLoader;
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

    void setUUIDManager(
            UUIDManager uuidManager
    ) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("setUUIDManager uuidManager == " + uuidManager);
        }
        mUUIDManager = uuidManager;
        if (mPluginLoader != null) {
            if (mLogger.isInfoEnabled()) {
                mLogger.info("更新 mPluginLoader 的 uuidManager");
            }
            mPluginLoader.setUUIDManager(uuidManager);
        }
    }

    void exit() {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("exit ");
        }
        PluginProcessService.sActivityHolder.finishAll();
        System.exit(0);
        try {
            wait();
        } catch (InterruptedException ignored) {
        }
    }

    PpsStatus getPpsStatus() {
        return new PpsStatus(mUUID, mRuntimeLoaded, mPluginLoader != null, mUUIDManager != null);
    }

    IBinder getPluginLoader() {
        return mPluginLoader;
    }

}
