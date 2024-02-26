package me.kbai.zhenxunui.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import me.kbai.zhenxunui.Constants
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseDialogFragment
import me.kbai.zhenxunui.databinding.DialogChangeApiBinding
import me.kbai.zhenxunui.extends.dp
import me.kbai.zhenxunui.extends.hideSoftInputWhenInputDone

/**
 * @author Sean on 2023/6/1
 */
class ChangeApiDialogFragment : BaseDialogFragment() {
    private lateinit var mBinding: DialogChangeApiBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DialogChangeApiBinding.inflate(layoutInflater).also { mBinding = it }.root

    init {
        isCancelable = false
        maxWidth = 300.dp.toInt()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                    tilApi.error = view.context.getString(R.string.error_input_api)
                    return@setOnClickListener
                }
                if (!Constants.updateApiUrl(text.toString())) {
                    tilApi.error = view.context.getString(R.string.error_wrong_api_address)
                    return@setOnClickListener
                }
                dismiss()
            }
        }
    }
}