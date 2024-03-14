package me.kbai.zhenxunui.ui.common

import android.content.Context
import android.content.DialogInterface
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.annotation.StringRes
import me.kbai.zhenxunui.base.BaseDialog
import me.kbai.zhenxunui.databinding.DialogPromptBinding

/**
 * @author Sean on 2023/6/8
 */
class PromptDialog(context: Context) : BaseDialog(context) {

    private var mBinding: DialogPromptBinding

    private val mParams = Params()

    init {
        setCancelable(false)

        setContentView(DialogPromptBinding.inflate(layoutInflater).also { mBinding = it }.root)
    }

    override fun show() {
        mBinding.run {
            tvText.movementMethod = ScrollingMovementMethod.getInstance()

            mParams.title?.let { tvTitle.text = it }
            mParams.text?.let { tvText.text = it }
            mParams.textRes?.let { tvText.setText(it) }

            btnCancel.setOnClickListener {
                val listener = mParams.cancelClickListener
                if (listener == null) {
                    dismiss()
                } else {
                    listener.invoke(this@PromptDialog)
                }
            }
            btnConfirm.setOnClickListener {
                val listener = mParams.confirmClickListener
                if (listener == null) {
                    dismiss()
                } else {
                    listener.invoke(btnConfirm, this@PromptDialog)
                }
            }
        }
        super.show()
    }

    fun setTitle(text: String): PromptDialog {
        mParams.title = text
        return this
    }

    fun setText(text: String): PromptDialog {
        mParams.text = text
        return this
    }

    fun setText(@StringRes resId: Int): PromptDialog {
        mParams.textRes = resId
        return this
    }

    fun setOnCancelClickListener(onCancel: (dialog: DialogInterface) -> Unit): PromptDialog {
        mParams.cancelClickListener = onCancel
        return this
    }

    fun setOnConfirmClickListener(onConfirm: (dialog: DialogInterface) -> Unit): PromptDialog {
        mParams.confirmClickListener = { _, dialog -> onConfirm.invoke(dialog) }
        return this
    }

    fun setOnConfirmClickListener(onConfirm: (button: View, dialog: DialogInterface) -> Unit): PromptDialog {
//        mBinding.btnConfirm.setOnClickListener { onConfirm.invoke(it, this) }
        mParams.confirmClickListener = onConfirm
        return this
    }

    private class Params {
        var title: String? = null

        var text: String? = null

        @StringRes
        var textRes: Int? = null

        var cancelClickListener: ((dialog: DialogInterface) -> Unit)? = null

        var confirmClickListener: ((button: View, dialog: DialogInterface) -> Unit)? = null
    }
}