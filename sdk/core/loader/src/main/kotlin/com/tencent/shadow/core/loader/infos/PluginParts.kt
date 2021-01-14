package com.tencent.shadow.core.loader.infos

import android.content.res.Resources
import com.tencent.shadow.core.loader.classloaders.PluginClassLoader
import com.tencent.shadow.core.runtime.PluginPackageManager
import com.tencent.shadow.core.runtime.ShadowAppComponentFactory
import com.tencent.shadow.core.runtime.ShadowApplication

class PluginParts(
        val appComponentFactory: ShadowAppComponentFactory,
        val application: ShadowApplication,
        val classLoader: PluginClassLoader,
        val resources: Resources,
        val businessName: String?,
        val pluginPackageManager: PluginPackageManager
)