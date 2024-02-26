package me.kbai.zhenxunui.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import me.kbai.zhenxunui.base.BaseDialogFragment
import me.kbai.zhenxunui.databinding.DialogPromptBinding

/**
 * @author Sean on 2023/6/8
 */
class PromptDialogFragment : BaseDialogFragment() {

    private lateinit var mBinding: DialogPromptBinding

    init {
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DialogPromptBinding.inflate(inflater).also { mBinding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding.btnCancel.setOnClickListener { dismiss() }
    }

    fun setTitle(text: String): PromptDialogFragment {
        mBinding.tvTitle.text = text
        return this
    }

    fun setText(text: String): PromptDialogFragment {
        mBinding.tvText.text = text
        return this
    }

    fun setText(@StringRes resId: Int): PromptDialogFragment {
        mBinding.tvText.setText(resId)
        return this
    }

    fun setOnCancelClickListener(onCancel: (dialog: DialogFragment) -> Unit): PromptDialogFragment {
        mBinding.btnCancel.setOnClickListener { onCancel.invoke(this) }
        return this
    }

    fun setOnConfirmClickListener(onConfirm: (dialog: DialogFragment) -> Unit): PromptDialogFragment {
        mBinding.btnConfirm.setOnClickListener { onConfirm.invoke(this) }
        return this
    }
}