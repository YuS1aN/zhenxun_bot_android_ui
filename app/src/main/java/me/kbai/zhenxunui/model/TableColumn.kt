package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

data class TableColumn(
    @SerializedName("column_name")
    val name: String,
    @SerializedName("data_type")
    val type: String,
    @SerializedName("max_length")
    val maxLength: Int?,
    @SerializedName("is_nullable")
    val nullable: String
)