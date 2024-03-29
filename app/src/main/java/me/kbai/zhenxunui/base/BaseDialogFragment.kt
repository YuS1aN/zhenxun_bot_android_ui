package me.kbai.zhenxunui.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import me.kbai.zhenxunui.extends.displaySize
import me.kbai.zhenxunui.extends.dp
import me.kbai.zhenxunui.extends.logE
import me.kbai.zhenxunui.extends.logI
import java.util.function.Consumer
import kotlin.math.min

/**
 * @author Sean on 2023/6/1
 */
open class BaseDialogFragment : DialogFragment() {
    protected var minMarginHorizontal = 80.dp.toInt()
    protected var maxWidth = 280.dp.toInt()

    val who: String by lazy {
        logI("Trying to get who.")
        var mWho = ""
        try {
            mWho = javaClass.superclass.superclass.getDeclaredField("mWho").let {
                it.isAccessible = true
                it.get(this)
            } as String
        } catch (e: Exception) {
            logE("Exception getting who: $e")
        }
        mWho
    }

    protected open fun Window.setDialogSize() {
        val size = displaySize()
        setLayout(
            min(size.width - minMarginHorizontal, maxWidth),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.run {
            setDialogSize()

            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

                val blurListener = Consumer<Boolean> { blurEnable ->
                    attributes = attributes.apply {
                        setDimAmount(if (blurEnable) 0.2f else 0.4f)
                        blurBehindRadius = 25
                    }
                }
                decorView.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
                    override fun onViewAttachedToWindow(v: View) {
                        windowManager.addCrossWindowBlurEnabledListener(blurListener)
                    }

                    override fun onViewDetachedFromWindow(v: View) {
                        windowManager.removeCrossWindowBlurEnabledListener(blurListener)
                    }
                })
            }
        }
    }

    fun show(fragmentManager: FragmentManager) {
        if (!fragmentManager.isStateSaved) {
            show(fragmentManager, this::class.simpleName ?: BaseDialogFragment::class.simpleName)
        }
    }
}