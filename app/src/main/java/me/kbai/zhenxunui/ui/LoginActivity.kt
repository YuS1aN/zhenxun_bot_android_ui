package me.kbai.zhenxunui.ui

import android.view.LayoutInflater
import com.bumptech.glide.load.DecodeFormat
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseActivity
import me.kbai.zhenxunui.databinding.ActivityLoginBinding
import me.kbai.zhenxunui.tool.glide.GlideApp

/**
 * @author Sean on 2023/5/31
 */
class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    override fun getViewBinding(inflater: LayoutInflater) = ActivityLoginBinding.inflate(inflater)

    override fun initView(): Unit = viewBinding.run {
        GlideApp.with(this@LoginActivity)
            .load(R.drawable.bg_login)
            .format(DecodeFormat.PREFER_RGB_565)
            .into(ivBackground)

        GlideApp.with(this@LoginActivity)
            .load(R.drawable.bg_login)
            .blur(25)
            .into(caBackground)

        btnApi.setOnClickListener {
            ChangeApiDialog(this@LoginActivity)
                .show()
        }

        btnLogin.setOnClickListener {

        }
    }
}