package me.kbai.zhenxunui.extends

inline fun <T> T.runWithoutReturn(block: T.() -> Unit): Unit = run(block)
