package me.kbai.zhenxunui.extends

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.repository.Resource

/**
 * @author sean on 2022/4/15
 */

fun LifecycleOwner.launchRepeatOnLifeCycle(
    state: Lifecycle.State,
    block: suspend CoroutineScope.() -> Unit
) = lifecycleScope.launch {
    repeatOnLifecycle(state, block)
}

val Fragment.viewLifecycleScope: CoroutineScope
    get() = viewLifecycleOwner.lifecycleScope

inline fun <reified T> Flow<T>.launchAndCollectIn(
    owner: LifecycleOwner,
    collector: FlowCollector<T>
) {
    val scope = if (owner is Fragment) {
        owner.viewLifecycleScope
    } else {
        owner.lifecycleScope
    }
    scope.launch {
        logI(T::class.java.name, "launchAndCollectIn")
        assert(T::class.java != Resource::class.java) { "Resource 类型要用 apiCollect 实现 token 过期重新登录!" }

        collect(collector)
    }
}