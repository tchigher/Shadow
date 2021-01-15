package com.tencent.shadow.dynamic.host;

import android.os.IBinder;
import android.os.Parcel;

import static android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE;

class PpsBinder extends android.os.Binder {
    static final String DESCRIPTOR = PpsBinder.class.getName();

    static final int TRANSACTION_CODE_NO_EXCEPTION = 0;
    static final int TRANSACTION_CODE_FAILED_EXCEPTION = 1;

    static final int TRANSACTION_loadRuntime = (FIRST_CALL_TRANSACTION);
    static final int TRANSACTION_loadPluginLoader = (FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_setUuidManager = (FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_exit = (FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_getPpsStatus = (FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_getPluginLoader = (FIRST_CALL_TRANSACTION + 5);

    private final PluginProcessService mPps;

    PpsBinder(PluginProcessService pps) {
        mPps = pps;
    }

    @Override
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
        switch (code) {
            case INTERFACE_TRANSACTION: {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            case TRANSACTION_loadRuntime: {
                data.enforceInterface(DESCRIPTOR);
                String _arg0;
                _arg0 = data.readString();
                try {
                    mPps.loadRuntime(_arg0);
                    reply.writeInt(TRANSACTION_CODE_NO_EXCEPTION);
                } catch (FailedException e) {
                    reply.writeInt(TRANSACTION_CODE_FAILED_EXCEPTION);
                    e.writeToParcel(reply, 0);
                }
                return true;
            }
            case TRANSACTION_loadPluginLoader: {
                data.enforceInterface(DESCRIPTOR);
                String _arg0;
                _arg0 = data.readString();
                try {
                    mPps.loadPluginLoader(_arg0);
                    reply.writeInt(TRANSACTION_CODE_NO_EXCEPTION);
                } catch (FailedException e) {
                    reply.writeInt(TRANSACTION_CODE_FAILED_EXCEPTION);
                    e.writeToParcel(reply, 0);
                }
                return true;
            }
            case TRANSACTION_setUuidManager: {
                data.enforceInterface(DESCRIPTOR);
                IBinder iBinder = data.readStrongBinder();
                UUIDManager uuidManager = iBinder != null ? new BinderUUIDManager(iBinder) : null;
                mPps.setUuidManager(uuidManager);
                reply.writeNoException();
                return true;
            }
            case TRANSACTION_exit: {
                data.enforceInterface(DESCRIPTOR);
                mPps.exit();
                reply.writeNoException();
                return true;
            }
            case TRANSACTION_getPpsStatus: {
                data.enforceInterface(DESCRIPTOR);
                PpsStatus ppsStatus = mPps.getPpsStatus();
                reply.writeNoException();
                ppsStatus.writeToParcel(reply, PARCELABLE_WRITE_RETURN_VALUE);
                return true;
            }
            case TRANSACTION_getPluginLoader: {
                data.enforceInterface(DESCRIPTOR);
                IBinder pluginLoader = mPps.getPluginLoader();
                reply.writeNoException();
                reply.writeStrongBinder(pluginLoader);
                return true;
            }
            default:
                return false;
        }
    }
}
