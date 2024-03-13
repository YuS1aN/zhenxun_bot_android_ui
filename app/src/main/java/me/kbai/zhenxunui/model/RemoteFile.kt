package me.kbai.zhenxunui.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class RemoteFile(
    @SerializedName("is_file")
    val isFile: Boolean,
    val name: String,
    val parent: String?
) : Parcelable {

    fun getPath() = (if (parent.isNullOrEmpty()) "" else "${parent}/") + name

    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readString().orEmpty(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (isFile) 1 else 0)
        parcel.writeString(name)
        parcel.writeString(parent)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RemoteFile> {
        override fun createFromParcel(parcel: Parcel): RemoteFile {
            return RemoteFile(parcel)
        }

        override fun newArray(size: Int): Array<RemoteFile?> {
            return arrayOfNulls(size)
        }
    }
}
