package com.tencent.shadow.dynamic.manager;

import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tencent.shadow.dynamic.loader.PluginLoader;
import com.tencent.shadow.dynamic.loader.PluginServiceConnection;

import java.util.Map;

class BinderPluginLoader implements PluginLoader {

    final private IBinder mRemoteBinder;

    BinderPluginLoader(
            @NonNull IBinder remoteBinder
    ) {
        mRemoteBinder = remoteBinder;
    }

    @Override
    public void loadPlugin(
            @NonNull String partKey
    ) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();

        try {
            _data.writeInterfaceToken(DESCRIPTOR);
            _data.writeString(partKey);

            mRemoteBinder.transact(TRANSACTION_loadPlugin, _data, _reply, 0);

            _reply.readException();
        } finally {
            _reply.recycle();
            _data.recycle();
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map getLoadedPlugins(
    ) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        Map _result;

        try {
            _data.writeInterfaceToken(DESCRIPTOR);

            mRemoteBinder.transact(TRANSACTION_getLoadedPlugin, _data, _reply, 0);

            _reply.readException();
            ClassLoader classLoader = this.getClass().getClassLoader();
            _result = _reply.readHashMap(classLoader);
        } finally {
            _reply.recycle();
            _data.recycle();
        }

        return _result;
    }

    @Override
    public void callApplicationOnCreate(
            @NonNull String partKey
    ) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();

        try {
            _data.writeInterfaceToken(DESCRIPTOR);
            _data.writeString(partKey);

            mRemoteBinder.transact(TRANSACTION_callApplicationOnCreate, _data, _reply, 0);

            _reply.readException();
        } finally {
            _reply.recycle();
            _data.recycle();
        }
    }

    @Override
    public Intent convertActivityIntent(
            @NonNull Intent pluginActivityIntent
    ) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        Intent _result;

        try {
            _data.writeInterfaceToken(DESCRIPTOR);
            _data.writeInt(1);
            pluginActivityIntent.writeToParcel(_data, 0);

            mRemoteBinder.transact(TRANSACTION_convertActivityIntent, _data, _reply, 0);

            _reply.readException();
            if ((0 != _reply.readInt())) {
                _result = Intent.CREATOR.createFromParcel(_reply);
            } else {
                _result = null;
            }
        } finally {
            _reply.recycle();
            _data.recycle();
        }

        return _result;
    }

    @Override
    public ComponentName startPluginService(
            @NonNull Intent pluginServiceIntent
    ) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        ComponentName _result;

        try {
            _data.writeInterfaceToken(DESCRIPTOR);
            _data.writeInt(1);
            pluginServiceIntent.writeToParcel(_data, 0);

            mRemoteBinder.transact(TRANSACTION_startPluginService, _data, _reply, 0);

            _reply.readException();
            if ((0 != _reply.readInt())) {
                _result = ComponentName.CREATOR.createFromParcel(_reply);
            } else {
                _result = null;
            }
        } finally {
            _reply.recycle();
            _data.recycle();
        }

        return _result;
    }

    @Override
    public boolean stopPluginService(
            @NonNull Intent pluginServiceIntent
    ) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        boolean _result;

        try {
            _data.writeInterfaceToken(DESCRIPTOR);
            _data.writeInt(1);
            pluginServiceIntent.writeToParcel(_data, 0);

            mRemoteBinder.transact(TRANSACTION_stopPluginService, _data, _reply, 0);

            _reply.readException();
            _result = (0 != _reply.readInt());
        } finally {
            _reply.recycle();
            _data.recycle();
        }

        return _result;
    }

    @Override
    public boolean bindPluginService(
            @NonNull Intent pluginServiceIntent,
            @Nullable PluginServiceConnection pluginServiceConnection,
            @NonNull Integer flags
    ) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        boolean _result;
        try {
            _data.writeInterfaceToken(DESCRIPTOR);
            _data.writeInt(1);
            pluginServiceIntent.writeToParcel(_data, 0);
            _data.writeStrongBinder((((pluginServiceConnection != null))
                    ? (new PluginServiceConnectionBinder(pluginServiceConnection))
                    : (null)));
            _data.writeInt(flags);

            mRemoteBinder.transact(TRANSACTION_bindPluginService, _data, _reply, 0);

            _reply.readException();
            _result = (0 != _reply.readInt());
        } finally {
            _reply.recycle();
            _data.recycle();
        }

        return _result;
    }

    @Override
    public void unbindPluginService(
            @Nullable PluginServiceConnection pluginServiceConnection
    ) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();

        try {
            _data.writeInterfaceToken(DESCRIPTOR);
            _data.writeStrongBinder((((pluginServiceConnection != null))
                    ? (new PluginServiceConnectionBinder(pluginServiceConnection))
                    : (null)));

            mRemoteBinder.transact(TRANSACTION_unbindService, _data, _reply, 0);

            _reply.readException();
        } finally {
            _reply.recycle();
            _data.recycle();
        }
    }

    @Override
    public void startActivityInPluginProcess(
            @NonNull Intent intent
    ) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();

        try {
            _data.writeInterfaceToken(DESCRIPTOR);
            intent.writeToParcel(_data, 0);

            mRemoteBinder.transact(TRANSACTION_startActivityInPluginProcess, _data, _reply, 0);

            _reply.readException();
        } finally {
            _reply.recycle();
            _data.recycle();
        }
    }

}
