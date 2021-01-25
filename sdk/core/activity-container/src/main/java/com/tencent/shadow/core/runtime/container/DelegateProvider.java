package com.tencent.shadow.core.runtime.container;

/*
 * 宿主容器委托提供者
 * <p>
 * 负责提供宿主容器委托实现
 */
public interface DelegateProvider {

    String LOADER_VERSION_KEY = "LOADER_VERSION";

    String PROCESS_ID_KEY = "PROCESS_ID_KEY";

    /**
     * 获取与delegator相应的HostActivityDelegate
     *
     * @param delegator HostActivity委托者
     * @return HostActivity被委托者
     */
    HostActivityDelegate getHostActivityDelegate(
            Class<? extends HostActivityDelegator> delegator
    );

}

