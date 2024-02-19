package me.kbai.zhenxunui.model

import com.google.gson.annotations.SerializedName

data class BotBaseInfo(
    val bot: String?,
    @SerializedName("self_id")
    val selfId: String,
    val nickname: String,
    @SerializedName("ava_url")
    val avatarUrl: String,
    @SerializedName("friend_count")
    val friendCount: Int,
    @SerializedName("group_count")
    val groupCount: Int,
    @SerializedName("received_messages")
    val receivedMessageCount: Int,
    @SerializedName("connect_time")
    val connectTime: Double,
    @SerializedName("connect_date")
    val connectDate: String,
    @SerializedName("plugin_count")
    val pluginCount: Int,
    @SerializedName("success_plugin_count")
    val successPluginCount: Int,
    @SerializedName("fail_plugin_count")
    val failPluginCount: Int,
    @SerializedName("is_select")
    val isSelect: Boolean,
    val config: Config
) {
    data class Config(
        val driver: String,
        val host: String,
        val port: Int,
        @SerializedName("log_level")
        val logLevel: String,
        @SerializedName("api_timeout")
        val apiTimeout: Float,
        val superusers: List<String>,
        val nickname: List<String>,
        @SerializedName("command_start")
        val commandStart: List<String>,
        @SerializedName("command_sep")
        val commandSep: List<String>,
        @SerializedName("session_expire_timeout")
        val sessionExpireTime: Float,
        @SerializedName("session_running_expression")
        val sessionRunningExpression: String,
        val debug: Boolean,
        val environment: String
    )
}
