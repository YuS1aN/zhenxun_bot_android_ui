package me.kbai.zhenxunui.ui.db

import android.annotation.SuppressLint
import android.util.LayoutDirection
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentDbManageBinding
import me.kbai.zhenxunui.extends.launchAndApiCollectIn
import me.kbai.zhenxunui.extends.setOnDebounceClickListener
import me.kbai.zhenxunui.extends.viewLifecycleScope
import me.kbai.zhenxunui.repository.Resource
import me.kbai.zhenxunui.tool.GlobalToast
import me.kbai.zhenxunui.viewmodel.DbManageViewModel
import me.kbai.zhenxunui.viewmodel.SqlLogViewModel

/**
 * @author Sean on 2023/5/30
 */
class DbManageFragment : BaseFragment<FragmentDbManageBinding>() {

    private val mViewModel by viewModels<DbManageViewModel>()
    private val mSqlLogViewModel by viewModels<SqlLogViewModel>()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDbManageBinding = FragmentDbManageBinding.inflate(inflater, container, false)


    @SuppressLint("ClickableViewAccessibility")
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

                    mSqlLogViewModel.requestSqlLog()
                }
        }

        etSql.setOnTouchListener touch@{ v, event ->
            if (event.action != MotionEvent.ACTION_UP) return@touch false
            val drawableEnd = (v as TextView).compoundDrawables[2] ?: return@touch false
            val drawableLeft: Int
            val drawableRight: Int

            if (v.layoutDirection == LayoutDirection.LTR) {
                drawableLeft = v.right - v.paddingEnd - drawableEnd.bounds.width()
                drawableRight = v.right
            } else {
                drawableLeft = 0
                drawableRight = v.paddingEnd + drawableEnd.bounds.width()
            }

            if (event.x >= drawableLeft && event.x <= drawableRight) {
                SqlLogPopupWindow(this@DbManageFragment)
                    .setOnItemSelectedListener { popup, sqlLog ->
                        etSql.setText(sqlLog.sql)
                        etSql.setSelection(sqlLog.sql.length)
                        popup.dismiss()
                    }
                    .show(v)
                return@touch true
            }
            return@touch false
        }
    }

    override fun initData() {
        mViewModel.tables.observe(viewLifecycleOwner) {
            viewBinding.srlRefresh.isRefreshing = false
            viewBinding.rvTables.adapter = DbTableAdapter(it, mViewModel, viewLifecycleScope)
        }

        if (mViewModel.tables.value == null) mViewModel.requestTableList()
    }
}