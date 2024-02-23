package me.kbai.zhenxunui.ui

import android.content.Context
import androidx.core.widget.addTextChangedListener
import me.kbai.zhenxunui.Constants
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseDialog
import me.kbai.zhenxunui.databinding.DialogChangeApiBinding
import me.kbai.zhenxunui.extends.dp
import me.kbai.zhenxunui.extends.hideSoftInputWhenInputDone

/**
 * @author Sean on 2023/6/1
 */
class ChangeApiDialog(context: Context) : BaseDialog(context) {
    private val mBinding = DialogChangeApiBinding.inflate(layoutInflater)

    init {
        setContentView(mBinding.root)
        setCancelable(false)
        maxWidth = 300.dp.toInt()

        mBinding.run {
            etApi.run {
                setText(Constants.apiBaseUrl.value)
                hideSoftInputWhenInputDone(btnConfirm)
                addTextChangedListener {
                    if (!it.isNullOrBlank()) {
                        tilApi.isErrorEnabled = false
                    }
                }
            }

            btnBack.setOnClickListener {
                dismiss()
            }

            btnConfirm.setOnClickListener {
                val text = etApi.text
                if (text.isNullOrBlank()) {
                    tilApi.error = context.getString(R.string.error_input_api)
                    return@setOnClickListener
                }
                if (!Constants.updateApiUrl(text.toString())) {
                    tilApi.error = context.getString(R.string.error_wrong_api_address)
                    return@setOnClickListener
                }
                dismiss()
            }
        }
    }
}