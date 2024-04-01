package me.kbai.zhenxunui.ui.db

import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.databinding.PopupSqlLogBinding
import me.kbai.zhenxunui.model.SqlLog
import me.kbai.zhenxunui.viewmodel.SqlLogViewModel


class SqlLogPopupWindow(host: Fragment) : PopupWindow(
    View.inflate(host.requireContext(), R.layout.popup_sql_log, null)
) {
    private val mBinding = PopupSqlLogBinding.bind(contentView)
    private val mViewModel by host.viewModels<SqlLogViewModel>()

    private val mObserver: Observer<List<SqlLog>>

    private val mWindowManager = host.requireActivity().windowManager

    private var mOnItemSelectedListener: ((SqlLogPopupWindow, SqlLog) -> Unit)? = null

    init {
        isFocusable = true
        isOutsideTouchable = true
        animationStyle = R.style.PopupAnim

        mObserver = Observer { list ->
            mBinding.lvSql.apply {
                adapter = ArrayAdapter(
                    mBinding.root.context, R.layout.item_sql_log, 0, list.map { it.sql }
                )
                setOnItemClickListener { _, _, position, _ ->
                    mOnItemSelectedListener?.invoke(this@SqlLogPopupWindow, list[position])
                }
            }
            mBinding.pbWaiting.isVisible = false
        }
        mViewModel.logs.observeForever(mObserver)
    }

    fun setOnItemSelectedListener(listener: (popup: SqlLogPopupWindow, sqlLog: SqlLog) -> Unit): SqlLogPopupWindow {
        mOnItemSelectedListener = listener
        return this
    }

    fun show(anchor: View) {
        anchor.context.getSystemService(InputMethodManager::class.java)
            .hideSoftInputFromWindow(anchor.windowToken, 0)

        width = anchor.width
        height = LayoutParams.WRAP_CONTENT
        showAsDropDown(anchor, 0, 0, Gravity.BOTTOM)

        val container: View = contentView.parent.let {
            if (it is View) it else contentView
        }
        val params = (container.layoutParams as WindowManager.LayoutParams).apply {
            flags = flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
            dimAmount = 0.1f
        }
        mWindowManager.updateViewLayout(container, params)
    }

    override fun dismiss() {
        mViewModel.logs.removeObserver(mObserver)

        val container: View = contentView.parent.let {
            if (it is View) it else contentView
        }
        val params = (container.layoutParams as WindowManager.LayoutParams).apply {
            flags = flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
            dimAmount = 0f
        }
        mWindowManager.updateViewLayout(container, params)

        super.dismiss()
    }
}