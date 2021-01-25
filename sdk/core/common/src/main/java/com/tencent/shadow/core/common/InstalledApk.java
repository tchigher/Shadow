package com.tencent.shadow.core.common;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 安装完成的apk
 */
public class InstalledApk
        implements Parcelable {

    public final String mApkFilePath;

    public final String odexPath;

    public final String libraryPath;

    public final byte[] parcelExtras;

    public InstalledApk(
            String apkFilePath,
            String odexPath,
            String libraryPath
    ) {
        this(apkFilePath, odexPath, libraryPath, null);
    }

    public InstalledApk(
            String apkFilePath,
            String odexPath,
            String libraryPath,
            byte[] parcelExtras
    ) {
        this.mApkFilePath = apkFilePath;
        this.odexPath = odexPath;
        this.libraryPath = libraryPath;
        this.parcelExtras = parcelExtras;
    }

    protected InstalledApk(
            Parcel in
    ) {
        mApkFilePath = in.readString();
        odexPath = in.readString();
        libraryPath = in.readString();
        int parcelExtrasLength = in.readInt();
        if (parcelExtrasLength > 0) {
            parcelExtras = new byte[parcelExtrasLength];
        } else {
            parcelExtras = null;
        }
        if (parcelExtras != null) {
            in.readByteArray(parcelExtras);
        }
    }

    @Override
    public void writeToParcel(
            Parcel dest,
            int flags
    ) {
        dest.writeString(mApkFilePath);
        dest.writeString(odexPath);
        dest.writeString(libraryPath);
        dest.writeInt(parcelExtras == null ? 0 : parcelExtras.length);
        if (parcelExtras != null) {
            dest.writeByteArray(parcelExtras);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InstalledApk> CREATOR = new Creator<InstalledApk>() {

        @Override
        public InstalledApk createFromParcel(
                Parcel in
        ) {
            return new InstalledApk(in);
        }

        @Override
        public InstalledApk[] newArray(
                int size
        ) {
            return new InstalledApk[size];
        }

    };

}
