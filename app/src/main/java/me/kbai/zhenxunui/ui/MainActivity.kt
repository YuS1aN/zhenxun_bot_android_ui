package me.kbai.zhenxunui.ui

import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseActivity
import me.kbai.zhenxunui.databinding.ActivityMainBinding
import me.kbai.zhenxunui.ext.findNavControllerByManager

/**
 * @author Sean on 2023/5/30
 */
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var mAppBarConfiguration: AppBarConfiguration

    override fun initView() {
        setSupportActionBar(viewBinding.icBar.toolbar)

        val navController = findNavControllerByManager(R.id.fragment_container)

        mAppBarConfiguration = AppBarConfiguration(
            setOf(
//                R.id.nav_home,
                R.id.nav_console,
                R.id.nav_plugin,
                R.id.nav_group,
                R.id.nav_request,
//                R.id.nav_info
            ), viewBinding.drawerLayout
        )
        setupActionBarWithNavController(navController, mAppBarConfiguration)
        viewBinding.navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp() =
        findNavControllerByManager(R.id.fragment_container).navigateUp(mAppBarConfiguration) || super.onSupportNavigateUp()

    override fun getViewBinding(inflater: LayoutInflater) =
        ActivityMainBinding.inflate(layoutInflater)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }
}