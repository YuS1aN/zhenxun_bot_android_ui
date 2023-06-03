package me.kbai.zhenxunui.ui.plugin

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.databinding.ItemPluginListBinding
import me.kbai.zhenxunui.ext.getThemeColor
import me.kbai.zhenxunui.ext.setOnDebounceClickListener
import me.kbai.zhenxunui.model.PluginData

/**
 * TODO 在切换 PluginTypeFragment 后 Switch 切换没有动画, 主要是由于 View.isLaidOut() == false, 可以尝试 requestLayout 修复
 * @author Sean on 2023/6/2
 */
class PluginViewHolder(val binding: ItemPluginListBinding) : RecyclerView.ViewHolder(binding.root)

class PluginAdapter() : RecyclerView.Adapter<PluginViewHolder>() {
    var data: List<PluginData> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onItemClickListener: ((position: Int) -> Unit)? = null
    var onItemSwitchClickListener: ((position: Int, switch: CompoundButton, isChecked: Boolean) -> Unit)? =
        null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PluginViewHolder(ItemPluginListBinding.inflate(LayoutInflater.from(parent.context)))

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: PluginViewHolder, position: Int) {
        val item = data[position]
        holder.binding.run {
            swEnabled.setOnCheckedChangeListener { _, isChecked ->
                dividingLine.run {
                    setImageDrawable(
                        ColorDrawable(
                            if (isChecked) {
                                context.getThemeColor(android.R.attr.colorPrimaryDark)
                            } else {
                                context.getColor(R.color.switch_unchecked)
                            }
                        )
                    )
                }
            }

            tvModel.text = item.module

            item.manager.let { manager ->
                tvName.text = manager.name
                swEnabled.isChecked = manager.status
            }
            swEnabled.isVisible = item.setting != null

            root.setOnDebounceClickListener { onItemClickListener?.invoke(position) }
            swEnabled.setOnDebounceClickListener {
                onItemSwitchClickListener?.invoke(position, swEnabled, swEnabled.isChecked)
            }
        }
    }
}