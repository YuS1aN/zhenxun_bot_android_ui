package me.kbai.zhenxunui.model

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import me.kbai.zhenxunui.R

enum class BlockType(val paramName: String, @StringRes val nameResId: Int) {
    @SerializedName("all")
    ALL("all", R.string.block_type_all),

    @SerializedName("private")
    PRIVATE("private", R.string.block_type_private),

    @SerializedName("group")
    GROUP("group", R.string.block_type_group)
}