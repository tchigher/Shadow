package com.tencent.shadow.core.runtime.container;

/**
 * HostActivity作为委托者的接口。主要提供它的委托方法的super方法，
 * 以便Delegate可以通过这个接口调用到Activity的super方法。
 * <p>
 * cubershi
 */
public interface HostActivityDelegator
        extends GeneratedHostActivityDelegator {

    HostActivity getHostActivity(
    );

}
