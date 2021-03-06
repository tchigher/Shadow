package com.tencent.shadow.core.load_parameters;

import android.os.Parcel;
import android.os.Parcelable;

/*
 * Loader加载插件的输入参数结构体
 * <p>
 * 这个类不能用Kotlin写是因为这个类可能会由非Kotlin写的代码new出来，
 * 而Loader打包的kotlin运行时可能连同Loader一起在一个独立的ClassLoader中。
 * 如果这个类用Kotlin写，就要求构造这个类对象的代码具有Kotlin运行时。
 */
public class LoadParameters
        implements Parcelable {

    public final String businessName;
    public final String partKey;
    public final String[] dependsOn;
    public final String[] hostWhiteList;

    public LoadParameters(
            String businessName,
            String partKey,
            String[] dependsOn,
            String[] hostWhiteList
    ) {
        this.businessName = businessName;
        this.partKey = partKey;
        this.dependsOn = dependsOn;
        this.hostWhiteList = hostWhiteList;
    }

    public LoadParameters(
            Parcel in
    ) {
        businessName = in.readString();
        partKey = in.readString();
        dependsOn = in.createStringArray();
        hostWhiteList = in.createStringArray();
    }

    @Override
    public void writeToParcel(
            Parcel dest,
            int flags
    ) {
        dest.writeString(businessName);
        dest.writeString(partKey);
        dest.writeStringArray(dependsOn);
        dest.writeStringArray(hostWhiteList);
    }

    @Override
    public int describeContents(
    ) {
        return 0;
    }

    public static final Creator<LoadParameters> CREATOR = new Creator<LoadParameters>() {

        @Override
        public LoadParameters createFromParcel(
                Parcel in
        ) {
            return new LoadParameters(in);
        }

        @Override
        public LoadParameters[] newArray(
                int size
        ) {
            return new LoadParameters[size];
        }

    };

}
