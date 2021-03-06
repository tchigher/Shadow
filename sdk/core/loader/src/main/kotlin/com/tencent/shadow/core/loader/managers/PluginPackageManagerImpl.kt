package com.tencent.shadow.core.loader.managers

import android.content.ComponentName
import android.content.Intent
import android.content.pm.*
import com.tencent.shadow.core.runtime.PluginPackageManager

internal class PluginPackageManagerImpl(
        private val hostPackageManager: PackageManager,
        private val packageInfo: PackageInfo,
        private val allPluginPackageInfo: () -> (Array<PackageInfo>)
) : PluginPackageManager {

    override fun getApplicationInfo(
            packageName: String,
            flags: Int
    ): ApplicationInfo =
            if (packageInfo.applicationInfo.packageName == packageName) {
                packageInfo.applicationInfo
            } else {
                hostPackageManager.getApplicationInfo(packageName, flags)
            }

    override fun getPackageInfo(
            packageName: String,
            flags: Int
    ): PackageInfo? =
            if (packageInfo.applicationInfo.packageName == packageName) {
                packageInfo
            } else {
                hostPackageManager.getPackageInfo(packageName, flags)
            }

    override fun getActivityInfo(
            component: ComponentName,
            flags: Int
    ): ActivityInfo {
        if (component.packageName == packageInfo.applicationInfo.packageName) {
            val pluginActivityInfo = allPluginPackageInfo()
                    .mapNotNull { it.activities }
                    .flatMap { it.asIterable() }.find {
                        it.name == component.className
                    }
            if (pluginActivityInfo != null) {
                return pluginActivityInfo
            }
        }
        return hostPackageManager.getActivityInfo(component, flags)
    }

    override fun resolveContentProvider(
            name: String,
            flags: Int
    ): ProviderInfo? {
        val pluginProviderInfo = allPluginPackageInfo()
                .flatMap { it.providers.asIterable() }.find {
                    it.authority == name
                }
        if (pluginProviderInfo != null) {
            return pluginProviderInfo
        }

        return hostPackageManager.resolveContentProvider(name, flags)
    }

    override fun queryContentProviders(
            processName: String?,
            uid: Int,
            flags: Int
    ): List<ProviderInfo> {
        return if (processName == null) {
            val allNormalProviders = hostPackageManager.queryContentProviders(null, 0, flags)
            val allPluginProviders = allPluginPackageInfo()
                    .flatMap { it.providers.asIterable() }
            listOf(allNormalProviders, allPluginProviders).flatten()
        } else {
            allPluginPackageInfo().filter {
                it.applicationInfo.processName == processName
                        && it.applicationInfo.uid == uid
            }.flatMap { it.providers.asIterable() }
        }
    }

    override fun resolveActivity(
            intent: Intent,
            flags: Int
    ): ResolveInfo {
        val hostResolveInfo = hostPackageManager.resolveActivity(intent, flags)
        return if (hostResolveInfo?.activityInfo == null) {
            ResolveInfo().apply {
                activityInfo = allPluginPackageInfo()
                        .flatMap { it.activities.asIterable() }
                        .find {
                            it.name == intent.component?.className
                        }
            }
        } else {
            hostResolveInfo
        }
    }

}