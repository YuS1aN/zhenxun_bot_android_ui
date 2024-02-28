package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

class PluginDetail(
    module: String,
    name: String,
    defaultStatus: Boolean,
    limitSuperuser: Boolean,
    costGold: Int,
    menuType: String,
    version: String,
    level: Int,
    status: Boolean,
    author: String?,
    blockType: BlockType?,
    @SerializedName("config_list")
    val configList: List<Config>?
) : PluginInfo(
    module,
    name,
    defaultStatus,
    limitSuperuser,
    costGold,
    menuType,
    version,
    level,
    status,
    author,
    blockType
) {
    data class Config(
        val module: String,
        val key: String,
        val value: Any?,
        val help: String?,
        @SerializedName("default_value")
        val defaultValue: Any?,
        val type: ConfigValueType?,
        @SerializedName("type_inner")
        val typeInner: List<ConfigValueType>?
    )

    constructor(pluginInfo: PluginInfo, configList: List<Config>?) : this(
        pluginInfo.module,
        pluginInfo.name,
        pluginInfo.defaultStatus,
        pluginInfo.limitSuperuser,
        pluginInfo.costGold,
        pluginInfo.menuType,
        pluginInfo.version,
        pluginInfo.level,
        pluginInfo.status,
        pluginInfo.author,
        pluginInfo.blockType,
        configList
    )

    fun createUpdateData(): UpdatePlugin = UpdatePlugin(
        module,
        defaultStatus,
        limitSuperuser,
        costGold,
        menuType,
        level,
        blockType,
        configListToMutableMap()
    )

    private fun configListToMutableMap() = HashMap<String, Any?>().apply {
        configList?.forEach {
            put(it.key, it.value)
        }
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + configList.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PluginDetail) return false
        if (!super.equals(other)) return false

        return configList == other.configList
    }
}