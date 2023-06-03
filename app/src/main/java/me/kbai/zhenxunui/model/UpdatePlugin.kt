package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

/**
 * @author Sean on 2023/6/4
 */
class UpdatePlugin(
    val module: String,
    @SerializedName("default_status")
    val defaultStatus: Boolean,
    @SerializedName("limit_superuser")
    val superuser: Boolean,
    @SerializedName("cost_gold")
    val costGold: Int,
    val cmd: List<String>,
    @SerializedName("menu_type")
    val menuType: String,
    @SerializedName("group_level")
    val groupLevel: Int,
    @SerializedName("block_type")
    val blockType: String
)