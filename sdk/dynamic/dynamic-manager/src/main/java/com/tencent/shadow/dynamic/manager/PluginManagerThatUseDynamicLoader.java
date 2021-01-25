package com.tencent.shadow.dynamic.manager;

import android.content.ComponentName;
import android.content.Context;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;
import com.tencent.shadow.dynamic.host.FailedException;
import com.tencent.shadow.dynamic.host.PPSController;
import com.tencent.shadow.dynamic.host.PPSStatus;
import com.tencent.shadow.dynamic.host.PluginManager;
import com.tencent.shadow.dynamic.host.PluginProcessService;
import com.tencent.shadow.dynamic.loader.PluginLoader;

public abstract class PluginManagerThatUseDynamicLoader
        extends BaseDynamicPluginManager
        implements PluginManager {

    private static final Logger mLogger =
            LoggerFactory.getLogger(PluginManagerThatUseDynamicLoader.class);

    /*
     * 插件进程 PluginProcessService 的接口
     */
    protected PPSController mPPSController;

    /*
     * 插件加载服务端接口
     */
    protected PluginLoader mPluginLoader;

    protected PluginManagerThatUseDynamicLoader(
            @NonNull Context context
    ) {
        super(context);
    }

    @Override
    protected void onPluginServiceConnected(
            ComponentName componentName,
            IBinder serviceBinder
    ) {
        mPPSController = PluginProcessService.wrapBinder(serviceBinder);

        try {
            mPPSController.setUUIDManager(new UUIDManagerBinder(PluginManagerThatUseDynamicLoader.this));
        } catch (DeadObjectException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("onServiceConnected RemoteException: " + e);
            }
        } catch (RemoteException e) {
            if (e.getClass().getSimpleName().equals("TransactionTooLargeException")) {
                if (mLogger.isErrorEnabled()) {
                    mLogger.error("onServiceConnected TransactionTooLargeException: " + e);
                }
            } else {
                throw new RuntimeException(e);
            }
        }

        try {
            IBinder iBinder = mPPSController.getPluginLoader();
            if (iBinder != null) {
                mPluginLoader = new BinderPluginLoader(iBinder);
            }
        } catch (RemoteException exception) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("onServiceConnected mPPSController getPluginLoader: ", exception);
            }
        }
    }

    @Override
    protected void onPluginServiceDisconnected(
            ComponentName componentName
    ) {
        mPPSController = null;
        mPluginLoader = null;
    }

    public final void loadRunTime(
            @NonNull String uuid
    ) throws RemoteException, FailedException {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("loadRunTime mPPSController: " + mPPSController);
        }

        PPSStatus ppsStatus = mPPSController.getPPSStatus();
        if (!ppsStatus.runtimeLoaded) {
            mPPSController.loadRuntime(uuid);
        }
    }

    public final void loadPluginLoader(
            @NonNull String uuid
    ) throws RemoteException, FailedException {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("loadPluginLoader mPluginLoader: " + mPluginLoader);
        }

        if (mPluginLoader == null) {
            PPSStatus ppsStatus = mPPSController.getPPSStatus();
            if (!ppsStatus.loaderLoaded) {
                mPPSController.loadPluginLoader(uuid);
            }
            IBinder iBinder = mPPSController.getPluginLoader();
            mPluginLoader = new BinderPluginLoader(iBinder);
        }
    }

}
