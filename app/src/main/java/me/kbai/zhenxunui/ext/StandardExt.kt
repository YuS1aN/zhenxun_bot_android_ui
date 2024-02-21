package me.kbai.zhenxunui.ext

inline fun <T> T.runWithNoReturn(block: T.() -> Unit): Unit = run(block)
