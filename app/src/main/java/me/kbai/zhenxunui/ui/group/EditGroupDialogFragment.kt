package me.kbai.zhenxunui.ui.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseDialogFragment
import me.kbai.zhenxunui.databinding.DialogEditGroupBinding
import me.kbai.zhenxunui.model.GroupInfo
import me.kbai.zhenxunui.model.UpdateGroup

/**
 * @author Sean on 2023/6/7
 */
class EditGroupDialogFragment(
    private val group: GroupInfo,
    private val onConfirmListener: (dialog: DialogFragment, button: View, update: UpdateGroup) -> Unit
) : BaseDialogFragment() {
    private lateinit var mBinding: DialogEditGroupBinding
    private val mUpdateGroup = group.makeUpdateGroup()

    init {
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DialogEditGroupBinding.inflate(inflater).also { mBinding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                tilGroupLevel.error = tilGroupLevel.context.getString(R.string.error_valid_number)
                return@setOnClickListener
            }
            mUpdateGroup.level = level

            onConfirmListener.invoke(this@EditGroupDialogFragment, it, mUpdateGroup)
        }
    }
}