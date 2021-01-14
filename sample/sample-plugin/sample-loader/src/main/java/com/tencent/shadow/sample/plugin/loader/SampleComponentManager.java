package com.tencent.shadow.sample.plugin.loader;

import android.content.ComponentName;
import android.content.Context;

import com.tencent.shadow.core.loader.infos.ContainerProviderInfo;
import com.tencent.shadow.core.loader.managers.ComponentManager;
import com.tencent.shadow.sample.constant.Constant;

import java.util.ArrayList;
import java.util.List;

public class SampleComponentManager extends ComponentManager {

    /**
     * dynamic-runtime-apk 模块中定义的壳子Activity，需要在宿主AndroidManifest.xml注册
     */
    private static final String DEFAULT_ACTIVITY = "com.tencent.shadow.sample.plugin.runtime.PluginDefaultProxyActivity";
    private static final String SINGLE_INSTANCE_ACTIVITY = "com.tencent.shadow.sample.plugin.runtime.PluginSingleInstance1ProxyActivity";
    private static final String SINGLE_TASK_ACTIVITY = "com.tencent.shadow.sample.plugin.runtime.PluginSingleTask1ProxyActivity";

    private Context context;

    public SampleComponentManager(Context context) {
        this.context = context;
    }


    /**
     * 配置插件Activity 到 壳子Activity的对应关系
     *
     * @param pluginActivity 插件Activity
     * @return 壳子Activity
     */
    @Override
    public ComponentName onBindContainerActivity(ComponentName pluginActivity) {
        switch (pluginActivity.getClassName()) {
            /**
             * 这里配置对应的对应关系
             */
        }
        return new ComponentName(context, DEFAULT_ACTIVITY);
    }

    /**
     * 配置对应宿主中预注册的壳子contentProvider的信息
     */
    @Override
    public ContainerProviderInfo onBindContainerContentProvider(ComponentName pluginContentProvider) {
        return new ContainerProviderInfo(
                "com.tencent.shadow.core.runtime.container.PluginContainerContentProvider",
                "com.tencent.shadow.contentprovider.authority.dynamic");
    }

    @Override
    public List<BroadcastInfo> getBroadcastInfoList(String partKey) {
        List<ComponentManager.BroadcastInfo> broadcastInfos = new ArrayList<>();
        if (partKey.equals(Constant.KEY__TARGET_PLUGIN_APP__ONE)) {
            broadcastInfos.add(
                    new ComponentManager.BroadcastInfo(
                            "com.tencent.shadow.sample.plugin.app.lib.usecases.receiver.MyReceiver",
                            new String[]{"com.tencent.test.action"}
                    )
            );
        }
        return broadcastInfos;
    }

}
