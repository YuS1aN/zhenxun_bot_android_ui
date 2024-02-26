package me.kbai.zhenxunui.ui

import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.DecodeFormat
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.Constants
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseActivity
import me.kbai.zhenxunui.databinding.ActivityLoginBinding
import me.kbai.zhenxunui.extends.apiCollect
import me.kbai.zhenxunui.extends.hideSoftInputWhenInputDone
import me.kbai.zhenxunui.extends.setOnDebounceClickListener
import me.kbai.zhenxunui.tool.GlobalToast
import me.kbai.zhenxunui.tool.glide.GlideApp
import me.kbai.zhenxunui.viewmodel.LoginViewModel

/**
 * @author Sean on 2023/5/31
 */
class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    private val mViewModel by viewModels<LoginViewModel>()

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

        btnApi.setOnDebounceClickListener {
            ChangeApiDialogFragment().show(supportFragmentManager)
        }

        etUsername.addTextChangedListener {
            if (!it.isNullOrBlank()) tilUsername.isErrorEnabled = false
        }
        etPassword.addTextChangedListener {
            if (!it.isNullOrBlank()) {
                tilPassword.isErrorEnabled = false
                cbSavePassword.isVisible = true
            }
        }
        etPassword.hideSoftInputWhenInputDone(btnLogin)

        btnLogin.setOnDebounceClickListener click@{ button ->
            val username = etUsername.text
            if (username.isNullOrBlank()) {
                tilUsername.error = getString(R.string.error_input_username)
                return@click
            }
            val password = etPassword.text
            if (password.isNullOrBlank()) {
                tilPassword.error = getString(R.string.error_input_password)
                cbSavePassword.isVisible = false
                return@click
            }
            if (Constants.apiBaseUrl.value.isNullOrBlank()) {
                GlobalToast.showToast(R.string.error_not_set_api)
                ChangeApiDialogFragment().show(supportFragmentManager)
                return@click
            }

            lifecycleScope.launch {
                val usernameStr = username.toString()
                val passwordStr = password.toString()

                mViewModel.login(usernameStr, passwordStr)
                    .apiCollect(button) {
                        GlobalToast.showToast(it.message)
                        if (it.success()) {
                            savePasswordIfChecked(usernameStr, passwordStr)
                            startActivity(
                                Intent(this@LoginActivity, MainActivity::class.java)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            )
                        }
                    }
            }
        }
    }

    override fun initData() {
        readSavedAccount()
    }

    private fun savePasswordIfChecked(username: String, password: String) {
        val editor = getSharedPreferences(Constants.SP_NAME_CONFIG, MODE_PRIVATE)
            .edit()
            .putString(Constants.SP_KEY_USERNAME, username)

        if (viewBinding.cbSavePassword.isChecked) {
            editor.putString(Constants.SP_KEY_PASSWORD, password)
        } else {
            editor.remove(Constants.SP_KEY_PASSWORD)
        }
        editor.apply()
    }

    private fun readSavedAccount() = viewBinding.run {
        val sp = getSharedPreferences(Constants.SP_NAME_CONFIG, MODE_PRIVATE)
        etUsername.setText(sp.getString(Constants.SP_KEY_USERNAME, ""))
        val password = sp.getString(Constants.SP_KEY_PASSWORD, "")
        etPassword.setText(password)
        cbSavePassword.isChecked = !password.isNullOrBlank()
    }
}