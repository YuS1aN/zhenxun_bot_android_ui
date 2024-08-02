package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

/**
 * @author Sean on 2023/6/2
 */
enum class PluginType(val string: String) {
    @SerializedName("NORMAL")
    NORMAL("NORMAL"),

    @SerializedName("ADMIN")
    ADMIN("ADMIN"),

    @SerializedName("HIDDEN")
    HIDDEN("HIDDEN"),

    @SerializedName("SUPERUSER")
    SUPERUSER("SUPERUSER"),

    @SerializedName("ADMIN_SUPER")
    ADMIN_SUPER("ADMIN_SUPER")
}