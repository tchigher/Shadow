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
import com.tencent.shadow.dynamic.host.MultiLoaderPPSController;
import com.tencent.shadow.dynamic.host.MultiLoaderPluginProcessService;
import com.tencent.shadow.dynamic.host.PPSStatus;
import com.tencent.shadow.dynamic.host.PluginManager;
import com.tencent.shadow.dynamic.loader.PluginLoader;

abstract public class PluginManagerThatSupportMultiLoader
        extends BaseDynamicPluginManager
        implements PluginManager {

    private static final Logger mLogger =
            LoggerFactory.getLogger(PluginManagerThatUseDynamicLoader.class);

    /*
     * 插件进程 MultiLoaderPluginProcessService 的接口
     */
    protected MultiLoaderPPSController mPPSController;

    /*
     * 插件加载服务端接口
     */
    protected PluginLoader mPluginLoader;

    public PluginManagerThatSupportMultiLoader(
            @NonNull Context context
    ) {
        super(context);
    }

    /*
     * 多 Loader 的 PPS，需要 hack 多个 RuntimeContainer, 因此需要使用 pluginKey 来作为插件业务的身份标识
     * 注意: 一个插件包有一份 loader、一份 runtime、多个 pluginPart, 该 key 与插件包一一对应
     */
    public abstract String getPluginKey();

    @Override
    protected void onPluginServiceConnected(
            ComponentName name,
            IBinder service
    ) {
        mPPSController = MultiLoaderPluginProcessService.wrapBinder(service);
        try {
            mPPSController.setUUIDManagerForPlugin(
                    getPluginKey(),
                    new UUIDManagerBinder(PluginManagerThatSupportMultiLoader.this)
            );
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
            IBinder iBinder = mPPSController.getPluginLoaderForPlugin(getPluginKey());
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
            ComponentName name
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

        PPSStatus ppsStatus = mPPSController.getPPSStatusForPlugin(getPluginKey());
        if (!ppsStatus.runtimeLoaded) {
            mPPSController.loadRuntimeForPlugin(getPluginKey(), uuid);
        }
    }

    public final void loadPluginLoader(
            @NonNull String uuid
    ) throws RemoteException, FailedException {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("loadPluginLoader mPluginLoader: " + mPluginLoader);
        }

        if (mPluginLoader == null) {
            PPSStatus ppsStatus = mPPSController.getPPSStatusForPlugin(getPluginKey());
            if (!ppsStatus.loaderLoaded) {
                mPPSController.loadPluginLoaderForPlugin(getPluginKey(), uuid);
            }
            IBinder iBinder = mPPSController.getPluginLoaderForPlugin(getPluginKey());
            mPluginLoader = new BinderPluginLoader(iBinder);
        }
    }

}
