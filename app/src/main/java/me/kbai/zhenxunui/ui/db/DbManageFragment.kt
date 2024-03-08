package me.kbai.zhenxunui.ui.db

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentDbManageBinding
import me.kbai.zhenxunui.extends.launchAndApiCollectIn
import me.kbai.zhenxunui.extends.setOnDebounceClickListener
import me.kbai.zhenxunui.extends.viewLifecycleScope
import me.kbai.zhenxunui.repository.Resource
import me.kbai.zhenxunui.tool.GlobalToast
import me.kbai.zhenxunui.viewmodel.DbManageViewModel

/**
 * @author Sean on 2023/5/30
 */
class DbManageFragment : BaseFragment<FragmentDbManageBinding>() {

    private val mViewModel by viewModels<DbManageViewModel>()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDbManageBinding = FragmentDbManageBinding.inflate(inflater, container, false)

    override fun initView() = viewBinding.run {
        srlRefresh.setOnRefreshListener {
            mViewModel.requestTableList()
        }

        btnSql.setOnDebounceClickListener { btn ->
            mViewModel.executeSql(etSql.text?.toString().orEmpty())
                .launchAndApiCollectIn(viewLifecycleOwner, btn) {
                    if (it.status == Resource.Status.LOADING) return@launchAndApiCollectIn
                    GlobalToast.showToast(it.message)

                    if (it.data != null) {
                        SqlResultDialogFragment().show(childFragmentManager)
                    }
                }
        }
    }

    override fun onStart() {
        super.onStart()
        viewBinding.btnSql.isEnabled = true
    }

    override fun initData() {
        mViewModel.tables.observe(viewLifecycleOwner) {
            viewBinding.srlRefresh.isRefreshing = false
            viewBinding.rvTables.adapter = DbTableAdapter(it, mViewModel, viewLifecycleScope)
        }

        if (mViewModel.tables.value == null) mViewModel.requestTableList()
    }
}