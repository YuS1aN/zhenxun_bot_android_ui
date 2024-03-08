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
fun <T> Flow<T>.launchAndCollectIn(
    owner: LifecycleOwner,
    activeState: Lifecycle.State = Lifecycle.State.STARTED,
    collector: FlowCollector<T>
) = owner.lifecycleScope.launch {
    owner.repeatOnLifecycle(activeState) {
        collect(collector)
    }
}