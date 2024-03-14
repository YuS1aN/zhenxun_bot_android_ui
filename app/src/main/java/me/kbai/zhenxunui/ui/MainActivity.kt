package me.kbai.zhenxunui.ui

import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseActivity
import me.kbai.zhenxunui.databinding.ActivityMainBinding
import me.kbai.zhenxunui.databinding.LayoutMainNavHeaderBinding
import me.kbai.zhenxunui.extends.findNavControllerByManager
import me.kbai.zhenxunui.extends.setOnDebounceClickListener
import me.kbai.zhenxunui.model.BotBaseInfo
import me.kbai.zhenxunui.tool.glide.GlideApp
import me.kbai.zhenxunui.viewmodel.MainViewModel

/**
 * @author Sean on 2023/5/30
 */
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var mAppBarConfiguration: AppBarConfiguration

    private val mViewModel by viewModels<MainViewModel>()

    private val mNavHeaderBinding by lazy {
        LayoutMainNavHeaderBinding.bind(viewBinding.navView.getHeaderView(0))
    }

    override fun initView() {
        setSupportActionBar(viewBinding.icBar.toolbar)

        val navController = findNavControllerByManager(R.id.fragment_container)

        mAppBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_console,
                R.id.nav_plugin,
                R.id.nav_friend_list,
                R.id.nav_request,
                R.id.nav_db_manage,
                R.id.nav_file_explorer
            ), viewBinding.drawerLayout
        )
        setupActionBarWithNavController(navController, mAppBarConfiguration)
        viewBinding.navView.setupWithNavController(navController)
    }

    override fun initData() {
        mViewModel.currentBot.observe(this) {
            mNavHeaderBinding.bindNavHeaderData(it)
        }
        SelectAccountDialogFragment.selectedAccount.observe(this) { consumable ->
            consumable.get()?.let { mViewModel.selectBot(it) }
        }

        mViewModel.requestBotList()
    }

    private fun LayoutMainNavHeaderBinding.bindNavHeaderData(info: BotBaseInfo) {
        GlideApp.with(ivAvatar)
            .load(info.avatarUrl)
            .into(ivAvatar)
        tvName.text = info.nickname
        tvId.text = info.selfId
        tvChange.setOnDebounceClickListener {
            SelectAccountDialogFragment().show(supportFragmentManager)
        }
    }

    override fun onSupportNavigateUp() =
        findNavControllerByManager(R.id.fragment_container).navigateUp(mAppBarConfiguration) || super.onSupportNavigateUp()

    override fun getViewBinding(inflater: LayoutInflater) =
        ActivityMainBinding.inflate(layoutInflater)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }
}