package com.tencent.shadow.dynamic.host;

import android.content.Context;

public interface ManagerFactory {

    PluginManager buildManager(
            Context context
    );

}
