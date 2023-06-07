package me.kbai.zhenxunui.ui.common

import android.content.Context
import android.content.DialogInterface
import androidx.annotation.StringRes
import me.kbai.zhenxunui.base.BaseDialog
import me.kbai.zhenxunui.databinding.DialogPromptBinding

/**
 * @author Sean on 2023/6/8
 */
class PromptDialog(context: Context) : BaseDialog(context) {

    private val mBinding = DialogPromptBinding.inflate(layoutInflater)

    init {
        setContentView(mBinding.root)
        setCancelable(false)

        mBinding.btnCancel.setOnClickListener { dismiss() }
    }

    fun setTitle(text: String): PromptDialog {
        mBinding.tvTitle.text = text
        return this
    }

    fun setText(text: String): PromptDialog {
        mBinding.tvText.text = text
        return this
    }

    fun setText(@StringRes resId: Int): PromptDialog {
        mBinding.tvText.setText(resId)
        return this
    }

    fun setOnCancelClickListener(onCancel: (dialog: DialogInterface) -> Unit): PromptDialog {
        mBinding.btnCancel.setOnClickListener { onCancel.invoke(this) }
        return this
    }

    fun setOnConfirmClickListener(onConfirm: (dialog: DialogInterface) -> Unit): PromptDialog {
        mBinding.btnConfirm.setOnClickListener { onConfirm.invoke(this) }
        return this
    }
}