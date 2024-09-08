package me.kbai.zhenxunui.model

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import me.kbai.zhenxunui.R

enum class BlockType(@StringRes val nameResId: Int) {
    @SerializedName("ALL")
    ALL(R.string.block_type_all),

    @SerializedName("PRIVATE")
    PRIVATE(R.string.block_type_private),

    @SerializedName("GROUP")
    GROUP(R.string.block_type_group)
}