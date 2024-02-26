package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

/**
 * @author Sean on 2023/6/2
 */
open class PluginInfo(
    val module: String,
    @SerializedName("plugin_name")
    val name: String,
    @SerializedName("default_status")
    val defaultStatus: Boolean,
    @SerializedName("limit_superuser")
    val limitSuperuser: Boolean,
    @SerializedName("cost_gold")
    val costGold: Int,
    @SerializedName("menu_type")
    val menuType: String,
    val version: String,
    val level: Int,
    val status: Boolean,
    val author: String?,
    @SerializedName("block_type")
    val blockType: BlockType?
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PluginInfo) return false

        if (module != other.module) return false
        if (name != other.name) return false
        if (defaultStatus != other.defaultStatus) return false
        if (limitSuperuser != other.limitSuperuser) return false
        if (costGold != other.costGold) return false
        if (menuType != other.menuType) return false
        if (version != other.version) return false
        if (level != other.level) return false
        if (status != other.status) return false
        if (author != other.author) return false
        if (blockType != other.blockType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = module.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + defaultStatus.hashCode()
        result = 31 * result + limitSuperuser.hashCode()
        result = 31 * result + costGold
        result = 31 * result + menuType.hashCode()
        result = 31 * result + version.hashCode()
        result = 31 * result + level
        result = 31 * result + status.hashCode()
        result = 31 * result + (author?.hashCode() ?: 0)
        result = 31 * result + (blockType?.hashCode() ?: 0)
        return result
    }
}