package me.kbai.zhenxunui.ui.db

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import me.kbai.zhenxunui.base.BaseDialogFragment
import me.kbai.zhenxunui.databinding.DialogSqlResultBinding
import me.kbai.zhenxunui.viewmodel.DbManageViewModel

class SqlResultDialogFragment : BaseDialogFragment() {

    private lateinit var mBinding: DialogSqlResultBinding

    private val mViewModel by viewModels<DbManageViewModel>({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DialogSqlResultBinding.inflate(inflater, container, false).also { mBinding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mViewModel.executeSqlResult.observe(viewLifecycleOwner) {
            mBinding.rvRows.adapter = SqlResultRowAdapter(it)
        }
    }
}