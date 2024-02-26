package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

enum class ConfigValueType {
    @SerializedName("int")
    INT,

    @SerializedName("float")
    FLOAT,

    @SerializedName("str")
    STRING,

    @SerializedName("bool")
    BOOL,

    @SerializedName("list")
    LIST,

    @SerializedName("tuple")
    TUPLE
}