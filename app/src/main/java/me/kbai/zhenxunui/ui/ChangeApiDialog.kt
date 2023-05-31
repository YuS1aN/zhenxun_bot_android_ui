package me.kbai.zhenxunui.ui

import android.content.Context
import androidx.core.widget.addTextChangedListener
import me.kbai.zhenxunui.Constants
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseDialog
import me.kbai.zhenxunui.databinding.DialogChangeApiBinding

/**
 * @author Sean on 2023/6/1
 */
class ChangeApiDialog(context: Context) : BaseDialog(context) {
    private val mBinding = DialogChangeApiBinding.inflate(layoutInflater)

    init {
        setContentView(mBinding.root)
        setCancelable(false)

        mBinding.etApi.setText(Constants.apiBaseUrl.value)
        mBinding.etApi.addTextChangedListener {
            if (!it.isNullOrBlank()) {
                mBinding.tilApi.isErrorEnabled = false
            }
        }
        mBinding.btnBack.setOnClickListener {
            dismiss()
        }

        mBinding.btnConfirm.setOnClickListener {
            val text = mBinding.etApi.text
            if (text.isNullOrBlank()) {
                mBinding.tilApi.error = context.getString(R.string.error_input_api)
                return@setOnClickListener
            }
            Constants.updateApiUrl(text.toString())
            dismiss()
        }
    }
}