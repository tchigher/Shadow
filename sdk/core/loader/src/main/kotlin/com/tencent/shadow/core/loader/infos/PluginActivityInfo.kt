package com.tencent.shadow.core.loader.infos

import android.content.pm.ActivityInfo
import android.os.Parcel
import android.os.Parcelable

class PluginActivityInfo(
        className: String?,
        val themeResource: Int,
        val activityInfo: ActivityInfo?
) : Parcelable,
        PluginComponentInfo(className) {

    constructor(
            parcel: Parcel
    ) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readParcelable(ActivityInfo::class.java.classLoader)) {
    }

    override fun writeToParcel(
            parcel: Parcel,
            flags: Int
    ) {
        parcel.writeString(className)
        parcel.writeInt(themeResource)
        parcel.writeParcelable(activityInfo, flags)
    }

    override fun describeContents(
    ): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PluginActivityInfo> {

        override fun createFromParcel(
                parcel: Parcel
        ): PluginActivityInfo {
            return PluginActivityInfo(parcel)
        }

        override fun newArray(
                size: Int
        ): Array<PluginActivityInfo?> {
            return arrayOfNulls(size)
        }

    }

}