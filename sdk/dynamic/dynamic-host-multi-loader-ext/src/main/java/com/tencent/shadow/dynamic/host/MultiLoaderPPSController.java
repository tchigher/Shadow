package com.tencent.shadow.dynamic.host;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import static com.tencent.shadow.dynamic.host.MultiLoaderPPSBinder.TRANSACTION_CODE_FAILED_EXCEPTION;
import static com.tencent.shadow.dynamic.host.MultiLoaderPPSBinder.TRANSACTION_CODE_NO_EXCEPTION;

public class MultiLoaderPPSController {

    final private IBinder mRemoteBinder;

    MultiLoaderPPSController(
            @NonNull IBinder remoteBinder
    ) {
        mRemoteBinder = remoteBinder;
    }

    public void loadRuntimeForPlugin(
            @NonNull String pluginKey,
            @NonNull String uuid
    ) throws RemoteException, FailedException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();

        try {
            _data.writeInterfaceToken(MultiLoaderPPSBinder.DESCRIPTOR);
            _data.writeString(pluginKey);
            _data.writeString(uuid);

            mRemoteBinder.transact(MultiLoaderPPSBinder.TRANSACTION_loadRuntimeForPlugin, _data, _reply, 0);

            int i = _reply.readInt();
            if (i == TRANSACTION_CODE_FAILED_EXCEPTION) {
                throw new FailedException(_reply);
            } else if (i != TRANSACTION_CODE_NO_EXCEPTION) {
                throw new RuntimeException("不认识的 code == " + i);
            }
        } finally {
            _reply.recycle();
            _data.recycle();
        }
    }

    public void loadPluginLoaderForPlugin(
            @NonNull String pluginKey,
            @NonNull String uuid
    ) throws RemoteException, FailedException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();

        try {
            _data.writeInterfaceToken(MultiLoaderPPSBinder.DESCRIPTOR);
            _data.writeString(pluginKey);
            _data.writeString(uuid);

            mRemoteBinder.transact(MultiLoaderPPSBinder.TRANSACTION_loadPluginLoaderForPlugin, _data, _reply, 0);

            int i = _reply.readInt();
            if (i == TRANSACTION_CODE_FAILED_EXCEPTION) {
                throw new FailedException(_reply);
            } else if (i != TRANSACTION_CODE_NO_EXCEPTION) {
                throw new RuntimeException("不认识的 code == " + i);
            }
        } finally {
            _reply.recycle();
            _data.recycle();
        }
    }

    public void setUUIDManagerForPlugin(
            @NonNull String pluginKey,
            @NonNull IBinder uuidManagerBinder) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        try {
            _data.writeInterfaceToken(MultiLoaderPPSBinder.DESCRIPTOR);
            _data.writeString(pluginKey);
            _data.writeStrongBinder(uuidManagerBinder);
            mRemoteBinder.transact(MultiLoaderPPSBinder.TRANSACTION_setUUIDManagerForPlugin, _data, _reply, 0);
            _reply.readException();
        } finally {
            _reply.recycle();
            _data.recycle();
        }
    }

    public PPSStatus getPPSStatusForPlugin(
            @NonNull String pluginKey
    ) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        PPSStatus _result;

        try {
            _data.writeInterfaceToken(MultiLoaderPPSBinder.DESCRIPTOR);
            _data.writeString(pluginKey);

            mRemoteBinder.transact(MultiLoaderPPSBinder.TRANSACTION_getPpsStatusForPlugin, _data, _reply, 0);

            _reply.readException();
            _result = new PPSStatus(_reply);
        } finally {
            _reply.recycle();
            _data.recycle();
        }

        return _result;
    }

    public IBinder getPluginLoaderForPlugin(
            @NonNull String pluginKey
    ) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        IBinder _result;

        try {
            _data.writeInterfaceToken(MultiLoaderPPSBinder.DESCRIPTOR);
            _data.writeString(pluginKey);

            mRemoteBinder.transact(MultiLoaderPPSBinder.TRANSACTION_getPluginLoaderForPlugin, _data, _reply, 0);

            _reply.readException();
            _result = _reply.readStrongBinder();
        } finally {
            _reply.recycle();
            _data.recycle();
        }

        return _result;
    }

    public void exit() throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();

        try {
            _data.writeInterfaceToken(MultiLoaderPPSBinder.DESCRIPTOR);

            mRemoteBinder.transact(MultiLoaderPPSBinder.TRANSACTION_exit, _data, _reply, 0);

            _reply.readException();
        } finally {
            _reply.recycle();
            _data.recycle();
        }
    }

}
