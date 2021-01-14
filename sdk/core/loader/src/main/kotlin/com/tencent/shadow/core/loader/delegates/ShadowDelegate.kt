package com.tencent.shadow.core.loader.delegates

import android.content.res.Resources
import com.tencent.shadow.core.loader.classloaders.PluginClassLoader
import com.tencent.shadow.core.loader.managers.ComponentManager
import com.tencent.shadow.core.runtime.ShadowAppComponentFactory
import com.tencent.shadow.core.runtime.ShadowApplication

abstract class ShadowDelegate() {

    fun inject(shadowApplication: ShadowApplication) {
        _pluginApplication = shadowApplication
    }

    fun inject(appComponentFactory: ShadowAppComponentFactory) {
        _appComponentFactory = appComponentFactory
    }

    fun inject(pluginClassLoader: PluginClassLoader) {
        _pluginClassLoader = pluginClassLoader
    }

    fun inject(resources: Resources) {
        _pluginResources = resources
    }

    fun inject(componentManager: ComponentManager) {
        _componentManager = componentManager
    }

    private lateinit var _appComponentFactory: ShadowAppComponentFactory
    private lateinit var _pluginApplication: ShadowApplication
    private lateinit var _pluginClassLoader: PluginClassLoader
    private lateinit var _pluginResources: Resources
    private lateinit var _componentManager: ComponentManager

    protected val mAppComponentFactory: ShadowAppComponentFactory
        get() = _appComponentFactory
    protected val mPluginApplication: ShadowApplication
        get() = _pluginApplication
    protected val mPluginClassLoader: PluginClassLoader
        get() = _pluginClassLoader
    protected val mPluginResources: Resources
        get() = _pluginResources
    protected val mComponentManager: ComponentManager
        get() = _componentManager
}
