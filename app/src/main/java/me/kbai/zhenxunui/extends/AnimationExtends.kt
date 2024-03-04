package me.kbai.zhenxunui.extends

import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener


fun Animation.setAnimationListener(
    onStart: (Animation) -> Unit = {},
    onEnd: (Animation) -> Unit = {},
    onRepeat: (Animation) -> Unit = {},
) = setAnimationListener(object : AnimationListener {
    override fun onAnimationStart(animation: Animation) {
        onStart(animation)
    }

    override fun onAnimationEnd(animation: Animation) {
        onEnd(animation)
    }

    override fun onAnimationRepeat(animation: Animation) {
        onRepeat(animation)
    }
})