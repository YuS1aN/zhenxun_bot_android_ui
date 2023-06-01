package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

/**
 * @author Sean on 2023/6/2
 */
data class PluginData(
    val model: String,
    @SerializedName("plugin_settings")
    val setting: Setting?,
    @SerializedName("plugin_manager")
    val manager: Manager?,
    @SerializedName("plugin_config")
    val config: Map<String, Config>?,
    @SerializedName("cd_limit")
    val cdLimit: CdLimit?,
    @SerializedName("block_limit")
    val blockLimit: BlockLimit?,
    @SerializedName("count_limit")
    val countLimit: CountLimit?
) {
    data class Setting(
        val cmd: List<String>,
        @SerializedName("default_status")
        val defaultStatus: Boolean,
        val level: Int,
        @SerializedName("limit_superuser")
        val superuser: Boolean,
        @SerializedName("plugin_type")
        val pluginType: List<String>,
        @SerializedName("cost_gold")
        val costGold: Int
    )

    data class Manager(
        @SerializedName("plugin_name")
        val name: String,
        val status: Boolean,
        val error: Boolean,
        @SerializedName("block_type")
        val blockType: String?,
        val author: String?,
        val version: String?
    )

    data class Config(
        val module: String,
        val key: String,
        val value: Any?,
        val help: String,
        @SerializedName("default_value")
        val defaultValue: Any?,
        @SerializedName("has_type")
        val hasType: Boolean
    )

    data class CdLimit(
        val cd: Int,
        val status: Boolean,
        @SerializedName("check_type")
        val checkType: String,
        @SerializedName("limit_type")
        val limitType: String,
        val rst: String
    )

    data class BlockLimit(
        val status: Boolean,
        @SerializedName("check_type")
        val checkType: String,
        @SerializedName("limit_type")
        val limitType: String,
        val rst: String
    )

    data class CountLimit(
        @SerializedName("max_count")
        val maxCount: Int,
        val status: Boolean,
        @SerializedName("limit_type")
        val limitType: String,
        val rst: String
    )
}