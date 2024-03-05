package me.kbai.zhenxunui.ui.request

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentRequestBinding
import me.kbai.zhenxunui.extends.viewLifecycleScope

/**
 * @author Sean on 2023/5/30
 */
class RequestFragment : BaseFragment<FragmentRequestBinding>() {
    private val mFragments = listOf(
        RequestTypeFragment.newInstance("group"),
        RequestTypeFragment.newInstance("friend")
    )

    private val mTabTextRes = arrayOf(
        R.string.tab_request_group,
        R.string.tab_request_private
    )

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRequestBinding = FragmentRequestBinding.inflate(inflater, container, false)

    override fun initView() = viewBinding.run {
        vpType.adapter = object : FragmentStateAdapter(this@RequestFragment) {
            override fun getItemCount(): Int = mFragments.size

            override fun createFragment(position: Int): Fragment = mFragments[position]
        }
        TabLayoutMediator(tlType, vpType, false, true) { tab, position ->
            tab.setText(mTabTextRes[position])
        }.attach()

        btnClear.setOnClickListener {
            viewLifecycleScope.launch {
                it.isEnabled = false
                mFragments[vpType.currentItem].clearRequest()
                it.isEnabled = true
            }
        }
    }
}