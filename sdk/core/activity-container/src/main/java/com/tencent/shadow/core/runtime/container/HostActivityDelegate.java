package com.tencent.shadow.core.runtime.container;

/*
 * HostActivity的被委托者接口
 * <p>
 * 被委托者通过实现这个接口中声明的方法达到替代委托者实现的目的，从而将HostActivity的行为动态化。
 */
public interface HostActivityDelegate extends GeneratedHostActivityDelegate {
    void setDelegator(HostActivityDelegator delegator);

    Object getPluginActivity();

    String getLoaderVersion();
}
