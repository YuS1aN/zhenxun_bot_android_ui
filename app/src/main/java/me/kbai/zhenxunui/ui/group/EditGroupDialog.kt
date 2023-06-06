package me.kbai.zhenxunui.ui.group

import android.content.Context
import android.content.DialogInterface
import android.view.View
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseDialog
import me.kbai.zhenxunui.databinding.DialogEditGroupBinding
import me.kbai.zhenxunui.model.GroupInfo
import me.kbai.zhenxunui.model.UpdateGroup

/**
 * @author Sean on 2023/6/7
 */
class EditGroupDialog(
    context: Context,
    private val group: GroupInfo,
    private val onConfirmListener: (dialog: DialogInterface, button: View, update: UpdateGroup) -> Unit
) : BaseDialog(context) {
    private val mBinding = DialogEditGroupBinding.inflate(layoutInflater)
    private val mUpdateGroup = group.makeUpdateGroup()

    init {
        setContentView(mBinding.root)
        setCancelable(false)
        initView()
    }

    private fun initView() = mBinding.run {
        swEnabled.isChecked = group.status
        etGroupLevel.setText(group.level.toString())

        swEnabled.setOnCheckedChangeListener { _, isChecked ->
            mUpdateGroup.status = isChecked
        }

        btnCancel.setOnClickListener { dismiss() }

        btnConfirm.setOnClickListener {
            val level = try {
                etGroupLevel.text.toString().toInt()
            } catch (e: NumberFormatException) {
                tilGroupLevel.error = context.getString(R.string.error_valid_number)
                return@setOnClickListener
            }
            mUpdateGroup.level = level

            onConfirmListener.invoke(this@EditGroupDialog, it, mUpdateGroup)
        }
    }
}