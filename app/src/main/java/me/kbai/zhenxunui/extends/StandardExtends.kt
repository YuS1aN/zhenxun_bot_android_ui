package me.kbai.zhenxunui.extends

inline fun <T> T.runWithNoReturn(block: T.() -> Unit): Unit = run(block)
