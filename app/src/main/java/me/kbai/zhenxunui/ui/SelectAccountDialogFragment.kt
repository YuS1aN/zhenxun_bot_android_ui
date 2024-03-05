package me.kbai.zhenxunui.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import me.kbai.zhenxunui.base.BaseDialogFragment
import me.kbai.zhenxunui.databinding.DialogSelectAccountBinding
import me.kbai.zhenxunui.databinding.ItemSelectAccountBinding
import me.kbai.zhenxunui.extends.dp
import me.kbai.zhenxunui.extends.setOnDebounceClickListener
import me.kbai.zhenxunui.tool.glide.GlideApp
import me.kbai.zhenxunui.viewmodel.MainViewModel

class SelectAccountDialogFragment(
    val callback: (id: String) -> Unit
) : BaseDialogFragment() {
    private lateinit var mBinding: DialogSelectAccountBinding

    private val mMainViewModel by viewModels<MainViewModel>({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = DialogSelectAccountBinding.inflate(inflater, container, false).also { mBinding = it }.root

    override fun onStart() {
        super.onStart()
        val list = mMainViewModel.botList.value ?: return
        if (list.size >= 5) {
            dialog?.window?.apply {
                setLayout(attributes.width, 285.dp.toInt())
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val list = mMainViewModel.botList.value ?: return

        mBinding.root.adapter = object : RecyclerView.Adapter<ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
                ItemSelectAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            override fun getItemCount() = list.size

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val item = list[position]
                holder.binding.run {
                    GlideApp.with(ivAvatar)
                        .load(item.avatarUrl)
                        .into(ivAvatar)
                    tvName.text = item.nickname
                    tvId.text = item.selfId
                    root.setOnDebounceClickListener {
                        dismiss()
                        callback.invoke(item.selfId)
                    }
                }
            }
        }
    }

    private class ViewHolder(val binding: ItemSelectAccountBinding) :
        RecyclerView.ViewHolder(binding.root)
}