package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

/**
 * @author Sean on 2023/6/2
 */
enum class PluginType(val string: String) {
    @SerializedName("normal")
    NORMAL("normal"),

    @SerializedName("admin")
    ADMIN("admin"),

    @SerializedName("hidden")
    HIDDEN("hidden"),

    @SerializedName("superuser")
    SUPERUSER("superuser")
}