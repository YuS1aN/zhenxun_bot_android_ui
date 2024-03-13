package me.kbai.zhenxunui.ui.file

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseFragment
import me.kbai.zhenxunui.databinding.FragmentFileExplorerBinding
import me.kbai.zhenxunui.databinding.ItemFileExplorerLineBinding
import me.kbai.zhenxunui.databinding.LayoutErrorPageBinding
import me.kbai.zhenxunui.extends.dp
import me.kbai.zhenxunui.extends.launchAndApiCollectIn
import me.kbai.zhenxunui.extends.setOnDebounceClickListener
import me.kbai.zhenxunui.model.RemoteFile
import me.kbai.zhenxunui.repository.Resource
import me.kbai.zhenxunui.tool.GlobalToast
import me.kbai.zhenxunui.ui.common.PromptDialogFragment
import me.kbai.zhenxunui.viewmodel.FileExplorerViewModel
import java.util.ArrayDeque
import java.util.Deque
import kotlin.math.abs
import kotlin.math.max

/**
 * @author Sean on 2023/5/30
 */
class FileExplorerFragment : BaseFragment<FragmentFileExplorerBinding>() {

    companion object {
        const val ARGS_FILE = "FILE"
    }

    private val mViewModel by viewModels<FileExplorerViewModel>()

    private val mSwipedViews: Deque<ItemFileExplorerLineBinding> = ArrayDeque()
    private val mSwipeAnimators: Deque<Animator> = ArrayDeque()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFileExplorerBinding = FragmentFileExplorerBinding.inflate(inflater, container, false)

    override fun initView(): Unit = viewBinding.run {
        icError.root.setBackgroundResource(R.color.dn_white)
        root.setOnRefreshListener {
            mViewModel.readDir()
        }
        rvDirs.setItemSwipe()
    }

    override fun initData() {
        mViewModel.file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARGS_FILE, RemoteFile::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARGS_FILE)
        }

        mViewModel.dir.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.LOADING -> viewBinding.icError.loading()
                Resource.Status.FAIL -> {
                    viewBinding.root.isRefreshing = false
                    viewBinding.icError.error(it.message)
                }

                Resource.Status.SUCCESS -> {
                    viewBinding.root.isRefreshing = false
                    viewBinding.rvDirs.adapter = initFileListAdapter(it.data.orEmpty())
                }
            }
        }

        if (mViewModel.dir.value == null) mViewModel.readDir()
    }

    private fun initFileListAdapter(data: List<RemoteFile>) = FileListLineAdapter(
        mViewModel.file != null, data
    ).apply {
        onItemClickListener = { file: RemoteFile ->
            if (file.isFile) {
                //TODO open file.
            } else {
                findNavController().navigate(
                    R.id.action_fileExplorerFragment_self,
                    Bundle().apply { putParcelable(ARGS_FILE, file) }
                )
            }
        }

        onBackPressClickListener = { findNavController().popBackStack() }

        onItemRenameListener = { file ->
            FileRenameDialogFragment(file.name) { button, dialog, newName ->
                mViewModel.renameFile(file, newName)
                    .launchAndApiCollectIn(viewLifecycleOwner, button) {
                        if (it.status == Resource.Status.LOADING) return@launchAndApiCollectIn
                        GlobalToast.showToast(it.message)
                        dialog.dismiss()
                        viewBinding.root.isRefreshing = true
                        mViewModel.readDir()
                    }
            }.show(childFragmentManager)
        }

        onItemDeleteListener = { file ->
            PromptDialogFragment()
                .setText(R.string.prompt_delete_file)
                .setOnConfirmClickListener { button, dialog ->
                    mViewModel.deleteFile(file).launchAndApiCollectIn(viewLifecycleOwner, button) {
                        if (it.status == Resource.Status.LOADING) return@launchAndApiCollectIn
                        GlobalToast.showToast(it.message)
                        dialog.dismiss()
                        viewBinding.root.isRefreshing = true
                        mViewModel.readDir()
                    }
                }
                .show(childFragmentManager)
        }
    }

    private fun RecyclerView.setItemSwipe() = post {
        setOnScrollChangeListener { _, _, _, _, _ ->
            while (mSwipeAnimators.isNotEmpty()) {
                mSwipeAnimators.poll()?.end()
            }
            while (mSwipedViews.isNotEmpty()) {
                mSwipedViews.poll()?.resetTranslationX()
            }
        }

        addOnItemTouchListener(object : OnItemTouchListener {
            private var disallowIntercept = false
            private var downPoint = PointF()
            private var dx = 0f
            private var dy = 0f
            private var mSelectedHolder: RecyclerView.ViewHolder? = null

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (disallowIntercept || e.actionIndex != 0) return false
                when (e.actionMasked) {
                    MotionEvent.ACTION_DOWN -> downPoint.set(e.x, e.y)
                    MotionEvent.ACTION_MOVE -> {
                        updateDistance(e)

                        if (abs(dx) > abs(dy) && dx < -(5.dp)) {
                            mSelectedHolder = rv.findChildViewUnder(e.x, e.y)
                                ?.let { rv.getChildViewHolder(it) }
                                ?.also {
                                    mSwipedViews.offerLast((it as FileListLineAdapter.ItemViewHolder).binding)
                                    viewBinding.root.isEnabled = false
                                }
                            return true
                        }
                    }

                    MotionEvent.ACTION_UP -> clear()
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                val finalHolder = mSelectedHolder ?: return
                val binding = (finalHolder as FileListLineAdapter.ItemViewHolder).binding

                //返回按钮不能swipe
                if (mViewModel.file != null && finalHolder.adapterPosition == 0) {
                    return
                }

                when (e.actionMasked) {
                    MotionEvent.ACTION_MOVE -> {
                        updateDistance(e)

                        val transX =
                            max(dx, -(binding.tvDelete.width + binding.tvRename.width).toFloat())

                        binding.tvFile.translationX = transX
                        binding.tvRename.translationX = binding.tvRename.width + transX
                    }

                    MotionEvent.ACTION_UP -> {
                        val buttonWidth =
                            (binding.tvDelete.width + binding.tvRename.width).toFloat()

                        val applyAnimatorParams: ValueAnimator.() -> Unit = {
                            duration = 200
                            mSwipeAnimators.offerLast(this)
                            addUpdateListener {
                                binding.tvFile.translationX = it.animatedValue as Float
                                binding.tvRename.translationX =
                                    binding.tvRename.width + it.animatedValue as Float
                            }
                        }
                        if (buttonWidth / 2 + dx > 1e-5) {
                            ValueAnimator.ofFloat(binding.tvFile.translationX, 0f)
                                .apply { applyAnimatorParams(this) }
                                .start()
                        } else {
                            ValueAnimator.ofFloat(binding.tvFile.translationX, -buttonWidth)
                                .apply { applyAnimatorParams(this) }
                                .start()
                        }
                        clear()
                    }
                }
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                this.disallowIntercept = disallowIntercept
            }

            private fun updateDistance(e: MotionEvent) {
                dx = e.x - downPoint.x
                dy = e.y - downPoint.y
            }

            private fun clear() {
                dx = 0f
                dy = 0f
                mSelectedHolder = null
                viewBinding.root.isEnabled = true
            }
        })
    }

    private fun LayoutErrorPageBinding.loading() {
        root.isVisible = false
//        btnRetry.isVisible = false
//        tvText.setText(R.string.loading)
//        ivImage.setImageResource(R.drawable.ic_loading)
//        root.setBackgroundResource(R.color.dn_white)
    }

    private fun LayoutErrorPageBinding.error(msg: String) {
        root.isVisible = true
        btnRetry.isVisible = true
        btnRetry.setOnDebounceClickListener {
            mViewModel.readDir()
            loading()
        }
        tvText.text = msg
        ivImage.setImageResource(R.drawable.ic_error)
    }

    private fun ItemFileExplorerLineBinding.resetTranslationX() {
        tvFile.translationX = 0f
        tvRename.translationX = 0f
    }
}