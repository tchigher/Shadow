package com.tencent.shadow.dynamic.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.tencent.shadow.dynamic.host.ManagerFactory;
import com.tencent.shadow.dynamic.host.PluginManager;
import com.tencent.shadow.sample.manager.SamplePluginManager;

/**
 * 此类の包名和类名固定
 */
public final class ManagerFactoryImpl implements ManagerFactory {
    @Override
    public PluginManager buildManager(
            @NonNull Context context
    ) {
        return new SamplePluginManager(context);
    }
}
