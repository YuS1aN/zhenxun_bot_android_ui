package me.kbai.zhenxunui.ui.plugin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentPluginBinding
import me.kbai.zhenxunui.model.PluginType

/**
 * @author Sean on 2023/5/30
 */
class PluginFragment : BaseFragment<FragmentPluginBinding>() {
    private val mFragments = listOf(
        PluginTypeFragment.newInstance(PluginType.NORMAL),
        PluginTypeFragment.newInstance(PluginType.ADMIN),
        PluginTypeFragment.newInstance(PluginType.SUPERUSER),
        PluginTypeFragment.newInstance(PluginType.HIDDEN)
    )

    private val tabTextRes = arrayOf(
        R.string.tab_normal,
        R.string.tab_admin,
        R.string.tab_superuser,
        R.string.tab_hidden
    )

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentPluginBinding = FragmentPluginBinding.inflate(inflater)

    override fun initView() = viewBinding.run {
        vpType.adapter = object : FragmentStateAdapter(this@PluginFragment) {
            override fun getItemCount(): Int = mFragments.size

            override fun createFragment(position: Int): Fragment = mFragments[position]
        }
        TabLayoutMediator(tlType, vpType, false, true) { tab, position ->
            tab.setText(tabTextRes[position])
        }.attach()
    }
}