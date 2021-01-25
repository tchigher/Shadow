package com.tencent.shadow.dynamic.host;

import android.content.Context;
import android.os.Bundle;

/**
 * 使用方持有的接口
 */
public interface PluginManager {

    /**
     * @param context  context
     * @param fromId   标识本次请求的来源位置，用于区分入口
     * @param bundle   参数列表
     * @param callback 用于从 PluginManager 的实现中返回 view
     */
    void enter(
            Context context,
            long fromId,
            Bundle bundle,
            EnterCallback callback
    );

}
