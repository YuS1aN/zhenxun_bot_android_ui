package me.kbai.zhenxunui.ui.plugin

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.View
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseDialog
import me.kbai.zhenxunui.databinding.DialogEditConfigBinding
import me.kbai.zhenxunui.ext.displaySize
import me.kbai.zhenxunui.ext.dp
import me.kbai.zhenxunui.model.PluginData
import me.kbai.zhenxunui.model.UpdateConfig
import me.kbai.zhenxunui.widget.CommonDecoration

/**
 * @author Sean on 2023/6/5
 */
class EditConfigDialog(
    val position: Int,
    private val configs: Map<String, PluginData.Config>,
    context: Context,
    private val onConfirmListener: (dialog: Dialog, button: View, position: Int, updateConfigs: List<UpdateConfig>) -> Unit
) : BaseDialog(context) {

    private val mBinding = DialogEditConfigBinding.inflate(layoutInflater)
    private val mAdapter = ConfigAdapter()

    init {
        minMarginHorizontal = 60.dp.toInt()
        maxWidth = 320.dp.toInt()
        setContentView(mBinding.root)
        setCancelable(false)
        initView()
        populateData()
    }

    private fun initView() = mBinding.run {
        rvConfig.adapter = mAdapter
        rvConfig.addItemDecoration(CommonDecoration(root.resources.getDimensionPixelSize(R.dimen.list_item_margin)))

        btnCancel.setOnClickListener { dismiss() }
        btnConfirm.setOnClickListener click@{
            onConfirmListener.invoke(
                this@EditConfigDialog,
                it,
                position,
                mAdapter.updateConfigs.values.toList()
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun populateData() = mBinding.run {
        mAdapter.data = configs.values.toList()
    }

    override fun onStart() {
        super.onStart()
        val size = context.displaySize()

        mBinding.run {
            root.post {
                val maxHeight = (size.y - 125.dp - btnCancel.height).toInt()

                rvConfig.run {
                    if (height > maxHeight) layoutParams =
                        layoutParams.also { it.height = maxHeight }

                    addOnLayoutChangeListener { v, _, top, _, bottom, _, _, _, _ ->
                        val h = bottom - top
                        if (h > maxHeight) v.layoutParams =
                            v.layoutParams.also { it.height = maxHeight }
                    }
                }
            }
        }
    }
}