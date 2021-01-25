package com.tencent.shadow.core.loader.blocs

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.res.Resources
import com.tencent.shadow.core.loader.classloaders.PluginClassLoader
import com.tencent.shadow.core.loader.exceptions.CreateApplicationException
import com.tencent.shadow.core.loader.infos.PluginInfo
import com.tencent.shadow.core.loader.managers.ComponentManager
import com.tencent.shadow.core.runtime.ShadowAppComponentFactory
import com.tencent.shadow.core.runtime.ShadowApplication

/*
 * 初始化插件Application类
 */
object CreateApplicationBloc {

    @Throws(CreateApplicationException::class)
    fun createShadowApplication(
            pluginClassLoader: PluginClassLoader,
            pluginInfo: PluginInfo,
            resources: Resources,
            hostAppContext: Context,
            componentManager: ComponentManager,
            applicationInfo: ApplicationInfo,
            appComponentFactory: ShadowAppComponentFactory
    ): ShadowApplication {
        try {
            val appClassName = pluginInfo.applicationClassName
                    ?: ShadowApplication::class.java.name
            val shadowApplication = appComponentFactory.instantiateApplication(pluginClassLoader, appClassName)
            val partKey = pluginInfo.partKey
            shadowApplication.setPluginResources(resources)
            shadowApplication.setPluginClassLoader(pluginClassLoader)
            shadowApplication.setPluginComponentLauncher(componentManager)
            shadowApplication.setBroadcasts(componentManager.getBroadcastsByPartKey(partKey))
            shadowApplication.setAppComponentFactory(appComponentFactory)
            shadowApplication.applicationInfo = applicationInfo
            shadowApplication.setBusinessName(pluginInfo.businessName)
            shadowApplication.setPluginPartKey(partKey)

            //和ShadowActivityDelegate.initPluginActivity一样，attachBaseContext放到最后
            shadowApplication.setHostApplicationContextAsBase(hostAppContext)
            shadowApplication.setTheme(applicationInfo.theme)
            return shadowApplication
        } catch (e: Exception) {
            throw CreateApplicationException(e)
        }
    }

}
