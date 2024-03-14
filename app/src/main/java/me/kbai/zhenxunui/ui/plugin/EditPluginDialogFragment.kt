package me.kbai.zhenxunui.ui.plugin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseDialogFragment
import me.kbai.zhenxunui.databinding.DialogEditPluginBinding
import me.kbai.zhenxunui.extends.apiCollect
import me.kbai.zhenxunui.extends.viewLifecycleScope
import me.kbai.zhenxunui.model.Consumable
import me.kbai.zhenxunui.model.PluginInfo
import me.kbai.zhenxunui.model.UpdatePlugin
import me.kbai.zhenxunui.repository.Resource
import me.kbai.zhenxunui.tool.GlobalToast
import me.kbai.zhenxunui.viewmodel.EditPluginViewModel

/**
 * @author Sean on 2023/6/5
 */
class EditPluginDialogFragment : BaseDialogFragment() {
    private lateinit var mBinding: DialogEditPluginBinding
    private val mViewModel by viewModels<EditPluginViewModel>()

    companion object {
        var sPluginInfo: PluginInfo? = null

        private var mUpdatePlugin: MutableLiveData<Consumable<UpdatePlugin>> = MutableLiveData()
        var updatePlugin: LiveData<Consumable<UpdatePlugin>> = mUpdatePlugin
    }

    init {
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DialogEditPluginBinding.inflate(inflater).also { mBinding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        mViewModel.requestPluginDetail(sPluginInfo!!)
    }

    private fun initView() = mBinding.run {
        mViewModel.pluginDetail.observe(this@EditPluginDialogFragment) {
            if (it.status == Resource.Status.FAIL) {
                GlobalToast.showToast(it.message)
                dismiss()
                return@observe
            }

            val data = it.data!!

            tvTitle.text = data.name
            tvAuthor.text = getString(R.string.plugin_author_format, data.author)
            tvModule.text = data.module
            rvConfig.adapter = PluginConfigAdapter(data)

            mViewModel.requestMenuTypes()
        }

        mViewModel.menuTypes.observe(this@EditPluginDialogFragment) {
            (rvConfig.adapter as PluginConfigAdapter?)?.pluginMenuTypes = it
        }

        btnCancel.setOnClickListener { dismiss() }
        btnConfirm.setOnClickListener {
            val data = (rvConfig.adapter as PluginConfigAdapter?)?.checkAndGetUpdateData()
                ?: return@setOnClickListener

            viewLifecycleScope.launch {
                mViewModel.updatePlugin(data)
                    .apiCollect(it) {
                        GlobalToast.showToast(it.message)
                        if (it.success()) {
                            mUpdatePlugin.value = Consumable(data)
                            dismiss()
                        }
                    }
            }
        }
    }
}