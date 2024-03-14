package me.kbai.zhenxunui.ui.file

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
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
import me.kbai.zhenxunui.ui.common.EditTextDialogFragment
import me.kbai.zhenxunui.ui.common.PromptDialog
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

        private var mRenamingFile: RemoteFile? = null
        private var mEditingFile: RemoteFile? = null
    }

    private val mViewModel by viewModels<FileExplorerViewModel>()

    private val mSwipedViews: Deque<ItemFileExplorerLineBinding> = ArrayDeque()
    private val mSwipeAnimators: Deque<Animator> = ArrayDeque()

    private var mMenuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.file_explorer_menu_items, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
            R.id.menu_new_file -> {
                EditTextDialogFragment.newInstance(
                    R.string.new_file, "", EditTextDialogFragment.NEW_FILE
                ).show(childFragmentManager)
                true
            }

            R.id.menu_new_folder -> {
                EditTextDialogFragment.newInstance(
                    R.string.new_folder, "", EditTextDialogFragment.NEW_FOLDER
                ).show(childFragmentManager)
                true
            }

            else -> false
        }
    }

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
                    if (it.data != null) {
                        viewBinding.icError.success()
                        viewBinding.rvDirs.adapter = initFileListAdapter(it.data)
                    }
                }
            }
        }

        EditTextDialogFragment.result.observe(viewLifecycleOwner) {
            if (it.isConsumed) return@observe
            val text = it.get() ?: return@observe
            when (it.id) {
                EditTextDialogFragment.NEW_FILE -> newFile(text)
                EditTextDialogFragment.NEW_FOLDER -> newFolder(text)
                EditTextDialogFragment.RENAME -> rename(text)
                EditTextDialogFragment.EDIT_FILE -> editFile(text)
            }
        }

        if (mViewModel.dir.value == null) {
            mViewModel.readDir()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().addMenuProvider(mMenuProvider)
    }

    private fun newFile(name: String) {
        val file = mViewModel.file ?: RemoteFile(false, "", null)
        val dialog = EditTextDialogFragment.dialogMap[EditTextDialogFragment.NEW_FILE]?.get()

        mViewModel.createNewFile(name, file.getPath())
            .launchAndApiCollectIn(viewLifecycleOwner, dialog?.binding?.btnConfirm) {
                if (it.status == Resource.Status.LOADING) return@launchAndApiCollectIn
                GlobalToast.showToast(it.data ?: it.message)
                dialog?.dismiss()
                viewBinding.root.isRefreshing = true
                mViewModel.readDir()
            }
    }

    private fun newFolder(name: String) {
        val file = mViewModel.file ?: RemoteFile(false, "", null)
        val dialog = EditTextDialogFragment.dialogMap[EditTextDialogFragment.NEW_FOLDER]?.get()

        mViewModel.createNewFolder(name, file.getPath())
            .launchAndApiCollectIn(viewLifecycleOwner, dialog?.binding?.btnConfirm) {
                if (it.status == Resource.Status.LOADING) return@launchAndApiCollectIn
                GlobalToast.showToast(it.data ?: it.message)
                dialog?.dismiss()
                viewBinding.root.isRefreshing = true
                mViewModel.readDir()
            }
    }

    private fun rename(name: String) {
        val file = mRenamingFile ?: return
        val dialog = EditTextDialogFragment.dialogMap[EditTextDialogFragment.RENAME]?.get()

        mViewModel.renameFile(file, name)
            .launchAndApiCollectIn(viewLifecycleOwner, dialog?.binding?.btnConfirm) {
                if (it.status == Resource.Status.LOADING) return@launchAndApiCollectIn
                GlobalToast.showToast(it.data ?: it.message)
                dialog?.dismiss()
                viewBinding.root.isRefreshing = true
                mViewModel.readDir()
            }
    }

    private fun editFile(text: String) {
        val file = mEditingFile ?: return
        val dialog = EditTextDialogFragment.dialogMap[EditTextDialogFragment.EDIT_FILE]?.get()

        mViewModel.editFile(file, text)
            .launchAndApiCollectIn(viewLifecycleOwner, dialog?.binding?.btnConfirm) {
                if (it.status == Resource.Status.LOADING) return@launchAndApiCollectIn
                GlobalToast.showToast(it.data ?: it.message)
                dialog?.dismiss()
            }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFileListAdapter(data: List<RemoteFile>) = FileListLineAdapter(
        mViewModel.file != null, data
    ).apply {
        onItemClickListener = { file: RemoteFile ->
            if (file.isFile) {
                viewBinding.root.setOnTouchListener { _, _ -> true }
                viewBinding.icLoading.root.isVisible = true

                mViewModel.readFile(file).launchAndApiCollectIn(viewLifecycleOwner) {
                    if (it.status == Resource.Status.LOADING) return@launchAndApiCollectIn

                    viewBinding.root.setOnTouchListener(null)
                    viewBinding.icLoading.root.isVisible = false

                    //这里服务端返回数据有问题, 非文本内容读取失败后状态仍为success但数据为空
                    if (it.success() && it.data != null) {
                        //EditText内容过多会导致IME崩溃(Binder传输限制), 限制到约500K以内
                        if (it.data.length > 250_000) {
                            GlobalToast.showToast(R.string.text_too_long)
                            PromptDialog(requireContext())
                                .setText(it.data)
                                .show()
                            return@launchAndApiCollectIn
                        }
                        mEditingFile = file
                        EditTextDialogFragment.newInstance(
                            R.string.edit_file,
                            it.data,
                            EditTextDialogFragment.EDIT_FILE,
                            Int.MAX_VALUE
                        ).show(childFragmentManager)
                    } else {
                        GlobalToast.showToast(it.message)
                    }
                }
            } else {
                findNavController().navigate(
                    R.id.action_fileExplorerFragment_self,
                    Bundle().apply { putParcelable(ARGS_FILE, file) }
                )
            }
        }

        onBackPressClickListener = { findNavController().popBackStack() }

        onItemRenameListener = { file ->
            mRenamingFile = file
            EditTextDialogFragment
                .newInstance(R.string.rename, file.name, EditTextDialogFragment.RENAME)
                .show(childFragmentManager)
        }

        onItemDeleteListener = { file ->
            PromptDialog(requireContext())
                .setText(R.string.prompt_delete_file)
                .setOnConfirmClickListener { button, dialog ->
                    mViewModel.deleteFile(file).launchAndApiCollectIn(viewLifecycleOwner, button) {
                        if (it.status == Resource.Status.LOADING) return@launchAndApiCollectIn
                        GlobalToast.showToast(it.data ?: it.message)
                        dialog.dismiss()
                        viewBinding.root.isRefreshing = true
                        mViewModel.readDir()
                    }
                }
                .show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        mSwipedViews.clear()
        mSwipeAnimators.clear()
        requireActivity().removeMenuProvider(mMenuProvider)
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

    private fun LayoutErrorPageBinding.success() {
        root.isVisible = false
    }

    private fun ItemFileExplorerLineBinding.resetTranslationX() {
        tvFile.translationX = 0f
        tvRename.translationX = 0f
    }
}