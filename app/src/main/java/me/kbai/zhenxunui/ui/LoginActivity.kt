package me.kbai.zhenxunui.ui

import android.os.Build
import android.view.LayoutInflater
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.set
import com.bumptech.glide.load.DecodeFormat
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseActivity
import me.kbai.zhenxunui.databinding.ActivityLoginBinding
import me.kbai.zhenxunui.tool.RenderEffectTool
import me.kbai.zhenxunui.tool.glide.GlideApp

/**
 * @author Sean on 2023/5/31
 */
class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    override fun getViewBinding(inflater: LayoutInflater) = ActivityLoginBinding.inflate(inflater)

    override fun initView() {
        GlideApp.with(this)
            .load(R.drawable.bg_login)
            .format(DecodeFormat.PREFER_RGB_565)
            .into(viewBinding.ivBackground)

        GlideApp.with(this)
            .load(R.drawable.bg_login)
            .blur(25)
            .into(viewBinding.caBackground)
    }
}