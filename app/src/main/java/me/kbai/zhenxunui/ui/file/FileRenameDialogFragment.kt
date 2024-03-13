package me.kbai.zhenxunui.ui.file

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseDialogFragment
import me.kbai.zhenxunui.databinding.DialogFileRenameBinding

class FileRenameDialogFragment(
    private val oldName: String,
    private val confirmClickListener: (button: View, dialog: DialogFragment, newName: String) -> Unit
) : BaseDialogFragment() {

    private lateinit var mBinding: DialogFileRenameBinding

    init {
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = DialogFileRenameBinding.inflate(inflater, container, false).also { mBinding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.etName.setText(oldName)
        mBinding.btnCancel.setOnClickListener { dismiss() }
        mBinding.btnConfirm.setOnClickListener {
            val text = mBinding.etName.text
            if (text.isNullOrBlank()) {
                mBinding.etName.error = getString(R.string.error_input_file_name)
                return@setOnClickListener
            }
            confirmClickListener.invoke(it, this, text.toString())
        }
    }
}