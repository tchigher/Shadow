package com.tencent.shadow.core.loader.infos

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by tracyluo on 2018/6/7.
 */
class PluginServiceInfo(className: String?) : Parcelable, PluginComponentInfo(className) {
    constructor(parcel: Parcel) : this(
            parcel.readString())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(className)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PluginServiceInfo> {
        override fun createFromParcel(parcel: Parcel): PluginServiceInfo {
            return PluginServiceInfo(parcel)
        }

        override fun newArray(size: Int): Array<PluginServiceInfo?> {
            return arrayOfNulls(size)
        }
    }
}