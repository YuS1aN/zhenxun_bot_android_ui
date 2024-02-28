package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

/**
 * @author Sean on 2023/6/4
 */
data class UpdatePlugin(
    val module: String,
    @SerializedName("default_status")
    var defaultStatus: Boolean,
    @SerializedName("limit_superuser")
    var limitSuperuser: Boolean,
    @SerializedName("cost_gold")
    var costGold: Int?,
    @SerializedName("menu_type")
    var menuType: String,
    var level: Int,
    @SerializedName("block_type")
    var blockType: BlockType?,
    val configs: MutableMap<String, Any?>
) {
    fun applyToPluginInfo(pluginInfo: PluginInfo) = PluginInfo(
        module,
        pluginInfo.name,
        defaultStatus,
        limitSuperuser,
        costGold ?: 0,
        menuType,
        pluginInfo.version,
        level,
        blockType != BlockType.ALL,
        pluginInfo.author,
        blockType
    )
}