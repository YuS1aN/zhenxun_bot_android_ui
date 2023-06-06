package me.kbai.zhenxunui.ui.plugin

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.android.material.tabs.TabLayout
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseDialog
import me.kbai.zhenxunui.databinding.DialogEditPluginBinding
import me.kbai.zhenxunui.model.PluginData
import me.kbai.zhenxunui.model.UpdatePlugin

/**
 * @author Sean on 2023/6/5
 */
class EditPluginDialog(
    val position: Int,
    private val plugin: PluginData,
    context: Context,
    private val onConfirmListener: (dialog: Dialog, button: View, position: Int, update: UpdatePlugin) -> Unit
) : BaseDialog(context) {

    private val mBinding = DialogEditPluginBinding.inflate(layoutInflater)
    private var mBlockTypeTabs: Array<TabLayout.Tab> = emptyArray()

    init {
        setContentView(mBinding.root)
        setCancelable(false)
        initView()
        populateData()
    }

    private fun initView() = mBinding.run {
        mBlockTypeTabs = arrayOf(
            tlBlockType.newTab().setText(R.string.tab_all),
            tlBlockType.newTab().setText(R.string.tab_group),
            tlBlockType.newTab().setText(R.string.tab_private)
        )
        mBlockTypeTabs.forEach { tlBlockType.addTab(it) }

        swEnabled.setOnCheckedChangeListener { _, isChecked ->
            setBlockTypeVisibility(!isChecked)
        }

        sbGroupLevel.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                etGroupLevel.setText(progress.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        etCost.addTextChangedListener {
            if (it.toString().toIntOrNull() != null) tilCost.isErrorEnabled = false
        }
        etType.addTextChangedListener {
            if (!it.isNullOrBlank()) tilType.isErrorEnabled = false
        }
        etCommand.addTextChangedListener {
            if (!it.isNullOrBlank()) tilType.isErrorEnabled = false
        }

        btnCancel.setOnClickListener { dismiss() }
        btnConfirm.setOnClickListener click@{
            val cost = try {
                etCost.text.toString().toInt()
            } catch (e: NumberFormatException) {
                tilCost.error = it.context.getString(R.string.error_valid_number)
                return@click
            }
            val commands = etCommand.text
                .toString()
                .replace("，", ",")
                .split(",")
                .toList()
            if (commands.isEmpty()) {
                tilCommand.error = it.context.getString(R.string.error_string_is_empty)
                return@click
            }
            val type = etType.text.toString()
            if (type.isBlank()) {
                tilType.error = it.context.getString(R.string.error_string_is_empty)
                return@click
            }

            onConfirmListener.invoke(
                this@EditPluginDialog, it, position,
                UpdatePlugin(
                    plugin.module,
                    swDefaultStatus.isChecked,
                    swSuperuser.isChecked,
                    cost,
                    commands,
                    type,
                    sbGroupLevel.progress,
                    if (swEnabled.isChecked) "" else when (tlBlockType.selectedTabPosition) {
                        1 -> "群组"
                        2 -> "私聊"
                        else -> "全部"
                    }
                )
            )
        }


    }

    private fun setBlockTypeVisibility(isVisible: Boolean) = mBinding.run {
        tlBlockType.isVisible = isVisible
        tvBlockType.isVisible = isVisible
        ConstraintSet()
            .apply {
                clone(clBody)
                connect(
                    swDefaultStatus.id, ConstraintSet.TOP,
                    if (isVisible) tvBlockType.id else swEnabled.id, ConstraintSet.BOTTOM
                )
                applyTo(clBody)
            }
    }

    @SuppressLint("SetTextI18n")
    private fun populateData() = mBinding.run {
        tvModule.text = plugin.module

        plugin.manager.let { manager ->
            swEnabled.isChecked = manager.status
            setBlockTypeVisibility(!manager.status)
            tlBlockType.selectTab(
                when (manager.blockType) {
                    "群组" -> mBlockTypeTabs[1]
                    "私聊" -> mBlockTypeTabs[2]
                    else -> mBlockTypeTabs[0]
                }
            )
            tvTitle.text = manager.name
            tvAuthor.text = "@${manager.author}"
        }

        plugin.setting?.let { setting ->
            swDefaultStatus.isChecked = setting.defaultStatus
            swSuperuser.isChecked = setting.superuser
            etCost.setText(setting.costGold.toString())
            etType.setText(setting.pluginType[0])
            val cmd = StringBuilder()
            setting.cmd.forEach { cmd.append(it).append(',') }
            cmd.deleteCharAt(cmd.lastIndex)
            etCommand.setText(cmd)
            sbGroupLevel.progress = setting.level
        }
    }
}