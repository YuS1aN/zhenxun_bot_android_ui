package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

/**
 * @author Sean on 2023/6/2
 */
enum class PluginType {
    /**
     * 管理员
     */
    @SerializedName("ADMIN")
    ADMIN,

    /**
     * 超级用户
     */
    @SerializedName("SUPERUSER")
    SUPERUSER,

    /**
     * 管理员以及超级用户
     */
    @SerializedName("ADMIN_SUPER")
    ADMIN_SUPER,

    /**
     * 普通插件
     */
    @SerializedName("NORMAL")
    NORMAL,

    /**
     * 隐藏插件，一般为没有主动触发命令的插件，不受权限控制，如消息统计
     */
    @SerializedName("HIDDEN")
    HIDDEN,

    /**
     * 依赖插件，一般为没有主动触发命令的插件，受权限控制
     */
    @SerializedName("DEPENDANT")
    DEPENDANT,

    /**
     * 父插件，仅仅标记
     */
    @SerializedName("PARENT")
    PARENT
}