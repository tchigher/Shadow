package com.tencent.shadow.dynamic.host;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import static com.tencent.shadow.dynamic.host.PPSBinder.TRANSACTION_CODE_FAILED_EXCEPTION;
import static com.tencent.shadow.dynamic.host.PPSBinder.TRANSACTION_CODE_NO_EXCEPTION;

public class PPSController {

    final private IBinder mRemoteBinder;

    PPSController(
            @NonNull IBinder remoteBinder
    ) {
        mRemoteBinder = remoteBinder;
    }

    public void loadRuntime(
            @NonNull String uuid
    ) throws RemoteException, FailedException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();

        try {
            _data.writeInterfaceToken(PPSBinder.DESCRIPTOR);
            _data.writeString(uuid);

            mRemoteBinder.transact(PPSBinder.TRANSACTION_loadRuntime, _data, _reply, 0);

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

    public void loadPluginLoader(
            @NonNull String uuid
    ) throws RemoteException, FailedException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();

        try {
            _data.writeInterfaceToken(PPSBinder.DESCRIPTOR);
            _data.writeString(uuid);

            mRemoteBinder.transact(PPSBinder.TRANSACTION_loadPluginLoader, _data, _reply, 0);

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

    public void setUUIDManager(
            @NonNull IBinder uuidManagerBinder
    ) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();

        try {
            _data.writeInterfaceToken(PPSBinder.DESCRIPTOR);
            _data.writeStrongBinder(uuidManagerBinder);

            mRemoteBinder.transact(PPSBinder.TRANSACTION_setUUIDManager, _data, _reply, 0);

            _reply.readException();
        } finally {
            _reply.recycle();
            _data.recycle();
        }
    }

    public void exit(
    ) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();

        try {
            _data.writeInterfaceToken(PPSBinder.DESCRIPTOR);

            mRemoteBinder.transact(PPSBinder.TRANSACTION_exit, _data, _reply, 0);

            _reply.readException();
        } finally {
            _reply.recycle();
            _data.recycle();
        }
    }

    public PPSStatus getPPSStatus(
    ) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        PPSStatus _result;

        try {
            _data.writeInterfaceToken(PPSBinder.DESCRIPTOR);

            mRemoteBinder.transact(PPSBinder.TRANSACTION_getPpsStatus, _data, _reply, 0);

            _reply.readException();
            _result = new PPSStatus(_reply);
        } finally {
            _reply.recycle();
            _data.recycle();
        }

        return _result;
    }

    public IBinder getPluginLoader(
    ) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        IBinder _result;

        try {
            _data.writeInterfaceToken(PPSBinder.DESCRIPTOR);

            mRemoteBinder.transact(PPSBinder.TRANSACTION_getPluginLoader, _data, _reply, 0);

            _reply.readException();
            _result = _reply.readStrongBinder();
        } finally {
            _reply.recycle();
            _data.recycle();
        }

        return _result;
    }

}
