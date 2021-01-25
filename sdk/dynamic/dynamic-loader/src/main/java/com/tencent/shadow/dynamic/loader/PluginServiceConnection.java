package com.tencent.shadow.dynamic.loader;

import android.content.ComponentName;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface PluginServiceConnection {

    String DESCRIPTOR = PluginServiceConnection.class.getName();

    int TRANSACTION_onServiceConnected = IBinder.FIRST_CALL_TRANSACTION;
    int TRANSACTION_onServiceDisconnected = IBinder.FIRST_CALL_TRANSACTION + 1;

    void onServiceConnected(
            @Nullable ComponentName name,
            @NonNull IBinder service
    );

    void onServiceDisconnected(
            @Nullable ComponentName name
    );

}
