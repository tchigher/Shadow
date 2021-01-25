package com.tencent.shadow.core.loader.infos

class PluginInfo(
        val businessName: String?,
        val partKey: String,
        val packageName: String,
        val applicationClassName: String?
) {

    private val _mActivities: MutableSet<PluginActivityInfo> = HashSet()
    private val _mServices: MutableSet<PluginServiceInfo> = HashSet()
    private val _mProviders: MutableSet<PluginProviderInfo> = HashSet()
    internal val mActivities: Set<PluginActivityInfo>
        get() = _mActivities
    internal val mServices: Set<PluginServiceInfo>
        get() = _mServices
    internal val mProviders: Set<PluginProviderInfo>
        get() = _mProviders

    internal var appComponentFactory: String? = null

    fun putActivityInfo(pluginActivityInfo: PluginActivityInfo) {
        _mActivities.add(pluginActivityInfo)
    }

    fun putServiceInfo(pluginServiceInfo: PluginServiceInfo) {
        _mServices.add(pluginServiceInfo)
    }

    fun putPluginProviderInfo(pluginProviderInfo: PluginProviderInfo) {
        _mProviders.add(pluginProviderInfo)
    }

}
