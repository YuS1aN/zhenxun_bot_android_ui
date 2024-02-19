package me.kbai.zhenxunui.ui.console

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentConsoleBinding
import me.kbai.zhenxunui.ext.viewLifecycleScope
import me.kbai.zhenxunui.tool.glide.GlideApp
import me.kbai.zhenxunui.viewmodel.ConsoleViewModel

/**
 * @author Sean on 2023/5/30
 */
class ConsoleFragment : BaseFragment<FragmentConsoleBinding>() {

    private val mViewModel by viewModels<ConsoleViewModel>()

    private var mCountdownJob: Job? = null

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentConsoleBinding = FragmentConsoleBinding.inflate(inflater)

    override fun initView() = viewBinding.run {
        mViewModel.systemStatus.observe(this@ConsoleFragment) {
            tvCpuUsage.text = getString(R.string.percent_format, it.cpu)
            spvCpuUsage.setProgressSmooth(it.cpu / 100)
            tvMemoryUsage.text = getString(R.string.percent_format, it.memory)
            spvMemoryUsage.setProgressSmooth(it.memory / 100)
            tvDiskUsage.text = getString(R.string.percent_format, it.disk)
            spvDiskUsage.setProgressSmooth(it.disk / 100)
        }

        mViewModel.botList.observe(this@ConsoleFragment) { list ->
            list.find { it.isSelect }?.let { info ->
                GlideApp.with(ivAvatar)
                    .load(info.avatarUrl)
                    .into(ivAvatar)
                tvNickname.text = info.nickname
                tvId.text = info.selfId
                tvFriendCount.text = info.friendCount.toString()
                tvGroupCount.text = info.groupCount.toString()
                startUpdateConnectedDuration(info.connectTime.toLong())
            }
        }
    }

    override fun initData() {
        viewLifecycleScope.launch {
            mViewModel.requestBotList()
        }
    }

    override fun onStart() {
        super.onStart()
        mViewModel.openWebSocket()
    }

    override fun onStop() {
        super.onStop()
        mViewModel.closeWebSocket()
    }

    private fun startUpdateConnectedDuration(connectTime: Long) {
        mCountdownJob?.cancel()
        mCountdownJob = viewLifecycleScope.launch {
            while (isActive) {
                var diff = System.currentTimeMillis() / 1000 - connectTime
                val days = diff / 86400
                diff %= 86400
                val hours = diff / 3600
                diff %= 3600
                val minutes = diff / 60
                diff %= 60
                viewBinding.tvConnectionDuration.text =
                    getString(R.string.connected_duration_format, days, hours, minutes, diff)
                delay(1000)
            }
        }
    }
}