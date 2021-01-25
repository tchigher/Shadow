package com.tencent.shadow.dynamic.manager;

import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tencent.shadow.dynamic.loader.PluginServiceConnection;

import static com.tencent.shadow.dynamic.loader.PluginServiceConnection.DESCRIPTOR;
import static com.tencent.shadow.dynamic.loader.PluginServiceConnection.TRANSACTION_onServiceConnected;
import static com.tencent.shadow.dynamic.loader.PluginServiceConnection.TRANSACTION_onServiceDisconnected;

class PluginServiceConnectionBinder extends Binder {

    private final PluginServiceConnection mPluginServiceConnection;

    PluginServiceConnectionBinder(
            PluginServiceConnection pluginServiceConnection
    ) {
        mPluginServiceConnection = pluginServiceConnection;
    }

    @Override
    public boolean onTransact(
            int code,
            @NonNull Parcel data,
            @Nullable Parcel reply,
            int flags
    ) throws RemoteException {
        switch (code) {
            case INTERFACE_TRANSACTION: {
                if (reply != null) {
                    reply.writeString(DESCRIPTOR);
                }
                return true;
            }

            case TRANSACTION_onServiceConnected: {
                data.enforceInterface(DESCRIPTOR);

                final ComponentName componentName;
                if (0 != data.readInt()) {
                    componentName = ComponentName.CREATOR.createFromParcel(data);
                } else {
                    componentName = null;
                }

                IBinder service = data.readStrongBinder();
                mPluginServiceConnection.onServiceConnected(componentName, service);
                service.linkToDeath(() -> mPluginServiceConnection.onServiceDisconnected(componentName), 0);

                if (reply != null) {
                    reply.writeNoException();
                }

                return true;
            }

            case TRANSACTION_onServiceDisconnected: {
                data.enforceInterface(DESCRIPTOR);

                ComponentName componentName;
                if (0 != data.readInt()) {
                    componentName = ComponentName.CREATOR.createFromParcel(data);
                } else {
                    componentName = null;
                }

                mPluginServiceConnection.onServiceDisconnected(componentName);

                if (reply != null) {
                    reply.writeNoException();
                }

                return true;
            }
        }

        return super.onTransact(code, data, reply, flags);
    }

}
