package com.tencent.shadow.dynamic.loader.impl

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.tencent.shadow.core.loader.ShadowPluginLoader
import com.tencent.shadow.core.runtime.container.ContentProviderDelegateProviderHolder
import com.tencent.shadow.core.runtime.container.DelegateProviderHolder
import com.tencent.shadow.dynamic.host.UUIDManager
import java.util.concurrent.CountDownLatch

internal class DynamicPluginLoader(
        hostContext: Context,
        uuid: String
) {

    companion object {
        private const val CORE_LOADER_FACTORY_IMPL_NAME = "com.tencent.shadow.dynamic.loader.impl.CoreLoaderFactoryImpl"
    }

    fun setUUIDManager(
            uuidManager: UUIDManager?
    ) {
        if (uuidManager != null) {
            mUUIDManager = uuidManager
        }
        // TODO #30 兼容 mUUIDManager 为 null 时的逻辑
    }

    private val mPluginLoader: ShadowPluginLoader

    private val mDynamicLoaderClassLoader: ClassLoader = DynamicPluginLoader::class.java.classLoader!!

    private var mContext: Context;

    private lateinit var mUUIDManager: UUIDManager;

    private var mUUID: String

    private val mUIHandler = Handler(Looper.getMainLooper())

    /*
     * 同一个 IServiceConnection 只会对应一个 ServiceConnection 对象，此 Map 就是保存这种对应关系
     */
    private val mConnectionMap = HashMap<IBinder, ServiceConnection>()

    init {
        try {
            val coreLoaderFactory = mDynamicLoaderClassLoader.getInterface(
                    CoreLoaderFactory::class.java,
                    CORE_LOADER_FACTORY_IMPL_NAME
            )
            mPluginLoader = coreLoaderFactory.build(hostContext)
            DelegateProviderHolder.setDelegateProvider(mPluginLoader.delegateProviderKey, mPluginLoader)
            ContentProviderDelegateProviderHolder.setContentProviderDelegateProvider(mPluginLoader)
            mPluginLoader.onCreate()
        } catch (e: Exception) {
            throw RuntimeException("当前的 classLoader 找不到 PluginLoader 的实现: ", e)
        }
        mContext = hostContext
        mUUID = uuid
    }

    fun loadPlugin(
            partKey: String
    ) {
        val installedApk = mUUIDManager.getPlugin(mUUID, partKey)
        val future = mPluginLoader.loadPlugin(installedApk)
        future.get()
    }

    fun getLoadedPlugin(): MutableMap<String, Boolean> {
        val plugins = mPluginLoader.getAllPluginPart()
        val loadPlugins = hashMapOf<String, Boolean>()
        for (part in plugins) {
            loadPlugins[part.key] = part.value.application.isCallOnCreate
        }
        return loadPlugins
    }

    @Synchronized
    fun callApplicationOnCreate(
            partKey: String
    ) {
        mPluginLoader.callApplicationOnCreate(partKey)
    }

    fun convertActivityIntent(
            pluginActivityIntent: Intent
    ): Intent? {
        return mPluginLoader.mComponentManager.convertPluginActivityIntent(pluginActivityIntent)
    }

    @Synchronized
    fun startPluginService(
            pluginServiceIntent: Intent
    ): ComponentName? {

        fun realAction(): ComponentName? {
            return mPluginLoader.getPluginServiceManager().startPluginService(pluginServiceIntent)
        }

        // 确保在 ui 线程调用
        var componentName: ComponentName? = null
        if (isUiThread()) {
            componentName = realAction()
        } else {
            val waitUiLock = CountDownLatch(1)
            mUIHandler.post {
                componentName = realAction()
                waitUiLock.countDown()
            }
            waitUiLock.await()
        }

        return componentName
    }

    @Synchronized
    fun stopPluginService(
            pluginServiceIntent: Intent
    ): Boolean {

        fun realAction(): Boolean {
            return mPluginLoader.getPluginServiceManager().stopPluginService(pluginServiceIntent)
        }

        // 确保在 ui 线程调用
        var stopped = false
        if (isUiThread()) {
            stopped = realAction()
        } else {
            val waitUiLock = CountDownLatch(1)
            mUIHandler.post {
                stopped = realAction()
                waitUiLock.countDown()
            }
            waitUiLock.await();
        }

        return stopped
    }

    @Synchronized
    fun bindPluginService(
            pluginServiceIntent: Intent,
            binderPsc: BinderPluginServiceConnection,
            flags: Int
    ): Boolean {

        fun realAction(): Boolean {
            if (mConnectionMap[binderPsc.mRemote] == null) {
                mConnectionMap[binderPsc.mRemote] = ServiceConnectionWrapper(binderPsc)
            }

            val connWrapper = mConnectionMap[binderPsc.mRemote]!!
            return mPluginLoader.getPluginServiceManager().bindPluginService(pluginServiceIntent, connWrapper, flags)
        }

        // 确保在 ui 线程调用
        var stop = false
        if (isUiThread()) {
            stop = realAction()
        } else {
            val waitUiLock = CountDownLatch(1)
            mUIHandler.post {
                stop = realAction()
                waitUiLock.countDown()
            }
            waitUiLock.await();
        }

        return stop

    }

    @Synchronized
    fun unbindService(
            connBinder: IBinder
    ) {
        mUIHandler.post {
            mConnectionMap[connBinder]?.let {
                mConnectionMap.remove(connBinder)
                mPluginLoader.getPluginServiceManager().unbindPluginService(it)
            }
        }
    }

    @Synchronized
    fun startActivityInPluginProcess(intent: Intent) {
        mUIHandler.post {
            mContext.startActivity(intent)
        }
    }

    private class ServiceConnectionWrapper(private val mConnection: BinderPluginServiceConnection) : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName) {
            mConnection.onServiceDisconnected(name)
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mConnection.onServiceConnected(name, service)
        }
    }

    private fun isUiThread(): Boolean {
        return Looper.myLooper() == Looper.getMainLooper()
    }

    /**
     * 从apk中读取接口的实现
     *
     * @param clazz     接口类
     * @param className 实现类的类名
     * @param <T>       接口类型
     * @return 所需接口
     * @throws Exception
    */
    @Throws(Exception::class)
    fun <T> ClassLoader.getInterface(
            clazz: Class<T>,
            className: String
    ): T {
        try {
            val interfaceImplementClass = loadClass(className)
            val interfaceImplement = interfaceImplementClass.newInstance()!!
            return clazz.cast(interfaceImplement)!!
        } catch (e: ClassNotFoundException) {
            throw Exception(e)
        } catch (e: InstantiationException) {
            throw Exception(e)
        } catch (e: ClassCastException) {
            throw Exception(e)
        } catch (e: IllegalAccessException) {
            throw Exception(e)
        }
    }

}
