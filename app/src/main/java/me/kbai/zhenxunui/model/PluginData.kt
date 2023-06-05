package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

/**
 * @author Sean on 2023/6/2
 */
data class PluginData(
    @SerializedName("model")
    val module: String,
    @SerializedName("plugin_settings")
    val setting: Setting?,
    @SerializedName("plugin_manager")
    val manager: Manager,
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

    fun makeUpdatePlugin(
        module: String = this.module,
        defaultStatus: Boolean = setting!!.defaultStatus,
        superuser: Boolean = setting!!.superuser,
        costGold: Int = setting!!.costGold,
        cmd: List<String> = setting!!.cmd,
        menuType: String = setting!!.pluginType[0],
        groupLevel: Int = setting!!.level,
        blockType: String = manager.blockType.orEmpty()
    ) = UpdatePlugin(
        module,
        defaultStatus,
        superuser,
        costGold,
        cmd,
        menuType,
        groupLevel,
        blockType
    )

    fun applyUpdatePlugin(update: UpdatePlugin): PluginData {
        if (update.module != module) throw IllegalArgumentException("Not the same module.")
        return copy(
            setting = setting?.copy(
                defaultStatus = update.defaultStatus,
                superuser = update.superuser,
                costGold = update.costGold,
                cmd = update.cmd,
                pluginType = setting.pluginType.toMutableList().apply { set(0, update.menuType) },
                level = update.groupLevel
            ),
            manager = manager.copy(
                status = update.blockType == "",
                blockType = update.blockType
            )
        )
    }

    fun applyUpdateConfig(configs: List<UpdateConfig>): PluginData {
        if (configs[0].module != module) throw IllegalArgumentException("Not the same module.")
        val modified = config?.toMutableMap()?.also {
            for (update in configs) {
                val config = it[update.key] ?: continue
                it[update.key] = config.copy(value = update.value)
            }
        }
        return copy(config = modified)
    }
}