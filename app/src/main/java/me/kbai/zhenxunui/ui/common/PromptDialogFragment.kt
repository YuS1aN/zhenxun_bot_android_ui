package me.kbai.zhenxunui.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import me.kbai.zhenxunui.base.BaseDialogFragment
import me.kbai.zhenxunui.databinding.DialogPromptBinding

/**
 * @author Sean on 2023/6/8
 */
class PromptDialogFragment : BaseDialogFragment() {

    private lateinit var mBinding: DialogPromptBinding

    private val mParams = Params()

    init {
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DialogPromptBinding.inflate(inflater).also { mBinding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding.run {
            mParams.title?.let { tvTitle.text = it }
            mParams.text?.let { tvText.text = it }
            mParams.textRes?.let { tvText.setText(it) }
            
            btnCancel.setOnClickListener {
                val listener = mParams.cancelClickListener
                if (listener == null) {
                    dismiss()
                } else {
                    listener.invoke(this@PromptDialogFragment)
                }
            }
            btnConfirm.setOnClickListener {
                val listener = mParams.confirmClickListener
                if (listener == null) {
                    dismiss()
                } else {
                    listener.invoke(btnConfirm, this@PromptDialogFragment)
                }
            }
        }
    }

    fun setTitle(text: String): PromptDialogFragment {
        mParams.title = text
        return this
    }

    fun setText(text: String): PromptDialogFragment {
        mParams.text = text
        return this
    }

    fun setText(@StringRes resId: Int): PromptDialogFragment {
        mParams.textRes = resId
        return this
    }

    fun setOnCancelClickListener(onCancel: (dialog: DialogFragment) -> Unit): PromptDialogFragment {
        mParams.cancelClickListener = onCancel
        return this
    }

    fun setOnConfirmClickListener(onConfirm: (dialog: DialogFragment) -> Unit): PromptDialogFragment {
        mParams.confirmClickListener = { _, dialog -> onConfirm.invoke(dialog) }
        return this
    }

    fun setOnConfirmClickListener(onConfirm: (button: View, dialog: DialogFragment) -> Unit): PromptDialogFragment {
//        mBinding.btnConfirm.setOnClickListener { onConfirm.invoke(it, this) }
        mParams.confirmClickListener = onConfirm
        return this
    }

    private class Params {
        var title: String? = null

        var text: String? = null

        @StringRes
        var textRes: Int? = null

        var cancelClickListener: ((dialog: DialogFragment) -> Unit)? = null

        var confirmClickListener: ((button: View, dialog: DialogFragment) -> Unit)? = null
    }
}