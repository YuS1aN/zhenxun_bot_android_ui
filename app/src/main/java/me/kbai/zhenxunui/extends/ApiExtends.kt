package me.kbai.zhenxunui.extends

import android.app.Application
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.repository.Resource

/**
 * @author Sean on 2023/6/1
 */
suspend fun <T : Resource<*>> Flow<T>.apiCollect(button: View? = null): T {
    button?.isEnabled = false
    var result: T? = null
    apiCollect { result = it }
    button?.isEnabled = true
    return result!!
}

suspend fun <T : Resource<*>> Flow<T>.apiCollect(
    button: View? = null,
    action: suspend (value: T) -> Unit
): Unit = apiCollect(application(), button, action)

suspend fun <T : Resource<*>> Flow<T>.apiCollect(
    application: Application,
    button: View? = null,
    action: suspend (value: T) -> Unit
): Unit = collect { value ->
    button?.isEnabled = value.status != Resource.Status.LOADING

    if (value.status == Resource.Status.FAIL && value.httpStatusCode in 400..401) {
        application.logout()
        return@collect
    }
    action.invoke(value)
}

/**
 * 要记得在 active state 恢复 button 状态
 */
fun <T : Resource<*>> Flow<T>.launchAndApiCollectIn(
    owner: LifecycleOwner,
    button: View? = null,
    activeState: Lifecycle.State = Lifecycle.State.STARTED,
    action: suspend (value: T) -> Unit
) = owner.lifecycleScope.launch {
    owner.repeatOnLifecycle(activeState) {
        apiCollect(button = button, action = action)
    }
}

//fun <T : Resource<*>> Flow<T>.checkToken(
//    application: Application
//): Flow<T> = filter {
//    if (it.code == Constants.CODE_EXPIRED_TOKEN) {
//        application.logout()
//        return@filter false
//    }
//    return@filter true
//}
//
//fun <T : Resource<*>> LiveData<T>.networkObserve(fragment: Fragment, observer: Observer<T>) =
//    networkObserve(fragment.requireActivity(), observer)
//
//fun <T : Resource<*>> LiveData<T>.networkObserve(
//    activity: ComponentActivity,
//    observer: Observer<T>
//) = observe(activity) {
//    if (value?.code == Constants.CODE_EXPIRED_TOKEN) {
//        MainScope().launch {
//            activity.application.logout()
//        }
//        return@observe
//    }
//    observer.onChanged(it)
//}