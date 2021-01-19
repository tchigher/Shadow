package com.tencent.shadow.core.loader.blocs

import com.tencent.shadow.core.common.InstalledApk
import com.tencent.shadow.core.common.Logger
import com.tencent.shadow.core.load_parameters.LoadParameters
import com.tencent.shadow.core.loader.classloaders.CombineClassLoader
import com.tencent.shadow.core.loader.classloaders.PluginClassLoader
import com.tencent.shadow.core.loader.exceptions.LoadApkException
import com.tencent.shadow.core.loader.infos.PluginParts
import java.io.File

/**
 * 加载插件到 ClassLoader 中
 */
object LoadApkBloc {

    /**
     * 加载插件到 ClassLoader 中
     *
     * @param installedApk 已安装(PluginManager已经下载解包) 的插件
     * @return 加载了插件的 ClassLoader
     */
    @Throws(LoadApkException::class)
    fun loadPlugin(
            installedApk: InstalledApk,
            loadParameters: LoadParameters,
            pluginPartsMap: MutableMap<String, PluginParts>
    ): PluginClassLoader {
        val apk = File(installedApk.mApkFilePath)
        val odexDir = if (installedApk.odexPath == null) null else File(installedApk.odexPath)
        val dependsOn = loadParameters.dependsOn
        // Logger 类一定打包在宿主中, 所在的 classLoader 即为加载宿主的 classLoader
        val hostClassLoader: ClassLoader = Logger::class.java.classLoader!!
        val hostParentClassLoader = hostClassLoader.parent
        if (dependsOn == null || dependsOn.isEmpty()) {
            return PluginClassLoader(
                    apk.absolutePath,
                    odexDir,
                    installedApk.libraryPath,
                    hostClassLoader,
                    hostParentClassLoader,
                    loadParameters.hostWhiteList
            )
        } else if (dependsOn.size == 1) {
            val partKey = dependsOn[0]
            val pluginParts = pluginPartsMap[partKey]
            if (pluginParts == null) {
                throw LoadApkException("加载 " + loadParameters.partKey + " 时它的依赖 " + partKey + " 还没有加载")
            } else {
                return PluginClassLoader(
                        apk.absolutePath,
                        odexDir,
                        installedApk.libraryPath,
                        pluginParts.classLoader,
                        null,
                        loadParameters.hostWhiteList
                )
            }
        } else {
            val dependsOnClassLoaders = dependsOn.map {
                val pluginParts = pluginPartsMap[it]
                if (pluginParts == null) {
                    throw LoadApkException("加载 " + loadParameters.partKey + " 时它的依赖 " + it + " 还没有加载")
                } else {
                    pluginParts.classLoader
                }
            }.toTypedArray()
            val combineClassLoader = CombineClassLoader(dependsOnClassLoaders, hostParentClassLoader)
            return PluginClassLoader(
                    apk.absolutePath,
                    odexDir,
                    installedApk.libraryPath,
                    combineClassLoader,
                    null,
                    loadParameters.hostWhiteList
            )
        }
    }

}
