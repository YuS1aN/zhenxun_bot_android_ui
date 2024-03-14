package me.kbai.zhenxunui.ui.file

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.databinding.ItemFileExplorerLineBinding
import me.kbai.zhenxunui.extends.setOnDebounceClickListener
import me.kbai.zhenxunui.model.RemoteFile

class FileListLineAdapter(
    private val showBack: Boolean,
    private val data: List<RemoteFile>
) : RecyclerView.Adapter<FileListLineAdapter.ItemViewHolder>() {
    var onItemClickListener: ((RemoteFile) -> Unit)? = null
    var onBackPressClickListener: (() -> Unit)? = null
    var onItemDeleteListener: ((RemoteFile) -> Unit)? = null
    var onItemRenameListener: ((RemoteFile) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemFileExplorerLineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = data.size + if (showBack) 1 else 0

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (showBack && position == 0) {
            holder.binding.run {
                val bounds = tvFile.compoundDrawables[0].bounds
                tvFile.setCompoundDrawables(
                    ResourcesCompat.getDrawable(tvFile.resources, R.drawable.ic_back_32, null)
                        ?.apply {
                            setBounds(bounds)
                        }, null, null, null
                )
                tvFile.setText(R.string.back)

                root.setOnDebounceClickListener {
                    onBackPressClickListener?.invoke()
                }
            }
            return
        }

        val item = data[position + if (showBack) -1 else 0]

        holder.binding.run {
            val bounds = tvFile.compoundDrawables[0].bounds
            tvFile.setCompoundDrawables(
                ResourcesCompat.getDrawable(
                    tvFile.resources,
                    if (item.isFile) R.drawable.ic_file_32 else R.drawable.ic_folder_32,
                    null
                )?.apply {
                    setBounds(bounds)
                }, null, null, null
            )
            tvFile.text = item.name

            tvFile.setOnDebounceClickListener {
                onItemClickListener?.invoke(item)
            }

            tvDelete.setOnDebounceClickListener {
                onItemDeleteListener?.invoke(item)
            }

            tvRename.setOnDebounceClickListener {
                onItemRenameListener?.invoke(item)
            }
        }
    }

    class ItemViewHolder(val binding: ItemFileExplorerLineBinding) :
        RecyclerView.ViewHolder(binding.root)
}