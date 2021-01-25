package com.tencent.shadow.dynamic.host;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 实现方需要实现的接口
 */
public interface PluginManager {

    /**
     * @param context                context
     * @param fromId                 标识本次请求的来源位置, 用于区分入口
     * @param bundle                 参数列表
     * @param pluginAppEnterCallback 用于从 PluginManager 的实现中返回 view
     */
    void enter(
            @NonNull Context context,
            @NonNull Long fromId,
            @NonNull Bundle bundle,
            @Nullable PluginAppEnterCallback pluginAppEnterCallback
    );

    default void onCreate(
            Bundle bundle
    ) {
    }

    default void onSaveInstanceState(
            Bundle outState
    ) {
    }

    default void onDestroy(
    ) {
    }

}
