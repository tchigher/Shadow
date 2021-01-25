package com.tencent.shadow.dynamic.host;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE;

class PPSBinder
        extends Binder {

    static final String DESCRIPTOR = PPSBinder.class.getName();

    static final int TRANSACTION_CODE_NO_EXCEPTION = 0;
    static final int TRANSACTION_CODE_FAILED_EXCEPTION = 1;

    static final int TRANSACTION_loadRuntime = (FIRST_CALL_TRANSACTION);
    static final int TRANSACTION_loadPluginLoader = (FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_setUUIDManager = (FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_exit = (FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_getPpsStatus = (FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_getPluginLoader = (FIRST_CALL_TRANSACTION + 5);

    private final PluginProcessService mPPS;

    PPSBinder(
            PluginProcessService pps
    ) {
        mPPS = pps;
    }

    @Override
    public boolean onTransact(
            int code,
            @NonNull Parcel data,
            @Nullable Parcel reply,
            int flags
    ) {
        switch (code) {
            case INTERFACE_TRANSACTION: {
                if (reply != null) {
                    reply.writeString(DESCRIPTOR);
                }
                return true;
            }
            case TRANSACTION_loadRuntime: {
                data.enforceInterface(DESCRIPTOR);
                String _arg0;
                _arg0 = data.readString();
                try {
                    mPPS.loadRuntime(_arg0);
                    if (reply != null) {
                        reply.writeInt(TRANSACTION_CODE_NO_EXCEPTION);
                    }
                } catch (FailedException e) {
                    if (reply != null) {
                        reply.writeInt(TRANSACTION_CODE_FAILED_EXCEPTION);
                    }
                    e.writeToParcel(reply, 0);
                }
                return true;
            }

            case TRANSACTION_loadPluginLoader: {
                data.enforceInterface(DESCRIPTOR);

                String uuid = data.readString();
                try {
                    mPPS.loadPluginLoader(uuid);
                    if (reply != null) {
                        reply.writeInt(TRANSACTION_CODE_NO_EXCEPTION);
                    }
                } catch (FailedException e) {
                    if (reply != null) {
                        reply.writeInt(TRANSACTION_CODE_FAILED_EXCEPTION);
                    }
                    e.writeToParcel(reply, 0);
                }

                return true;
            }

            case TRANSACTION_setUUIDManager: {
                data.enforceInterface(DESCRIPTOR);
                IBinder iBinder = data.readStrongBinder();
                UUIDManager uuidManager = iBinder != null ? new BinderUUIDManager(iBinder) : null;
                mPPS.setUUIDManager(uuidManager);
                if (reply != null) {
                    reply.writeNoException();
                }
                return true;
            }
            case TRANSACTION_exit: {
                data.enforceInterface(DESCRIPTOR);
                mPPS.exit();
                if (reply != null) {
                    reply.writeNoException();
                }
                return true;
            }
            case TRANSACTION_getPpsStatus: {
                data.enforceInterface(DESCRIPTOR);
                PPSStatus ppsStatus = mPPS.getPpsStatus();
                if (reply != null) {
                    reply.writeNoException();
                }
                ppsStatus.writeToParcel(reply, PARCELABLE_WRITE_RETURN_VALUE);
                return true;
            }
            case TRANSACTION_getPluginLoader: {
                data.enforceInterface(DESCRIPTOR);
                IBinder pluginLoader = mPPS.getPluginLoader();
                if (reply != null) {
                    reply.writeNoException();
                }
                reply.writeStrongBinder(pluginLoader);
                return true;
            }
            default:
                return false;
        }
    }

}
