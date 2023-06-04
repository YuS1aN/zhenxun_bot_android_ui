package me.kbai.zhenxunui.ui.plugin

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.widget.addTextChangedListener
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

    init {
        setContentView(mBinding.root)
        setCancelable(false)
        initView()
        populateData()
    }

    private fun initView() = mBinding.run {
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
                    if (swEnabled.isChecked) "" else "全部"
                )
            )
        }


    }

    @SuppressLint("SetTextI18n")
    private fun populateData() = mBinding.run {
        tvModule.text = plugin.module

        plugin.manager.let { manager ->
            swEnabled.isChecked = manager.status
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