package me.kbai.zhenxunui.ui.plugin

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.databinding.ItemPluginListBinding
import me.kbai.zhenxunui.extends.getThemeColor
import me.kbai.zhenxunui.extends.setOnDebounceClickListener
import me.kbai.zhenxunui.model.PluginData

/**
 * TODO 在切换 PluginTypeFragment 后 Switch 切换没有动画, 主要是由于 View.isLaidOut() == false, 可以尝试 requestLayout 修复
 * @author Sean on 2023/6/2
 */
class PluginViewHolder(val binding: ItemPluginListBinding) : RecyclerView.ViewHolder(binding.root) {
    var module: String = ""

    fun setEnabled(enabled: Boolean): Unit = binding.run {
        swEnabled.isEnabled = enabled
        btnEdit.isEnabled = enabled
        btnConfig.isEnabled = enabled
    }
}

class PluginAdapter : RecyclerView.Adapter<PluginViewHolder>() {
    var data: List<PluginData> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            holderMap.clear()
            nonEditableSet.clear()
            notifyDataSetChanged()
        }
    val holderMap: HashMap<String, PluginViewHolder> = HashMap()
    val nonEditableSet: HashSet<String> = HashSet()

    var onItemEditClickListener: ((position: Int) -> Unit)? = null
    var onItemConfigClickListener: ((position: Int) -> Unit)? = null
    var onItemSwitchClickListener: ((position: Int, isChecked: Boolean) -> Unit)? =
        null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PluginViewHolder(
            ItemPluginListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: PluginViewHolder, position: Int) {
        val item = data[position]

        holderMap[item.module] = holder
        holder.module = item.module

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

            tvModule.text = item.module

            item.manager.let { manager ->
                tvName.text = manager.name
                swEnabled.isChecked = manager.status
            }

            swEnabled.isVisible = item.setting != null
            btnEdit.isVisible = item.setting != null
            btnConfig.isVisible = item.config != null

            btnEdit.setOnDebounceClickListener { onItemEditClickListener?.invoke(position) }
            btnConfig.setOnDebounceClickListener { onItemConfigClickListener?.invoke(position) }
            swEnabled.setOnDebounceClickListener {
                onItemSwitchClickListener?.invoke(position, swEnabled.isChecked)
            }

            holder.setEnabled(!nonEditableSet.contains(item.module))
        }
    }
}