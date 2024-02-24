package me.kbai.zhenxunui.tool

import java.util.concurrent.atomic.AtomicInteger

class AlwaysDifferent<T>(val obj: T) {
    companion object {
        private val hashCode = AtomicInteger(0)
    }

    override fun equals(other: Any?): Boolean = false

    override fun hashCode(): Int = hashCode.incrementAndGet()
}