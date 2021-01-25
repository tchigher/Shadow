package com.tencent.shadow.core.runtime.container;


import android.os.SystemClock;

import java.util.HashMap;
import java.util.Map;

/*
 * DelegateProvider依赖注入类
 * <p>
 * dynamic-pluginloader通过这个类实现将PluginLoader中的DelegateProvider实现注入到plugincontainer中。
 */
public class DelegateProviderHolder {

    public static final String DEFAULT_KEY = "DEFAULT_KEY";
    private static final Map<String, DelegateProvider> delegateProviderMap = new HashMap<>();

    /*
     * 为了防止系统有一定概率出现进程号重启后一致的问题，我们使用开机时间作为进程号来判断进程是否重启
     */
    public static long sCustomPid;

    static {
        sCustomPid = SystemClock.elapsedRealtime();
    }

    public static void setDelegateProvider(
            String key,
            DelegateProvider delegateProvider
    ) {
        delegateProviderMap.put(key, delegateProvider);
    }

    public static DelegateProvider getDelegateProvider(String key) {
        return delegateProviderMap.get(key);
    }

}
