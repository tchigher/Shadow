package com.tencent.shadow.dynamic.host;

import android.support.annotation.NonNull;
import android.view.View;

public interface PluginAppEnterCallback {

    void onShowLoadingView(
            @NonNull View view
    );

    void onCloseLoadingView();

    void onComplete();

}
