package me.kbai.zhenxunui.ui.plugin

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.databinding.ItemPluginListBinding
import me.kbai.zhenxunui.extends.getThemeColor
import me.kbai.zhenxunui.extends.setOnDebounceClickListener
import me.kbai.zhenxunui.model.PluginInfo

/**
 * TODO 在切换 PluginTypeFragment 后 Switch 切换没有动画, 主要是由于 View.isLaidOut() == false, 可以尝试 requestLayout 修复
 * @author Sean on 2023/6/2
 */
class PluginViewHolder(val binding: ItemPluginListBinding) : RecyclerView.ViewHolder(binding.root) {
    var module: String = ""

    fun setEnabled(enabled: Boolean): Unit = binding.run {
        swEnabled.isEnabled = enabled
        llInfo0.isEnabled = enabled
        llDivider1.isEnabled = enabled
        llInfo1.isEnabled = enabled
    }
}

class PluginAdapter : RecyclerView.Adapter<PluginViewHolder>() {
    var data: List<PluginInfo> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onItemEditClickListener: ((position: Int) -> Unit)? = null
    var onItemSwitchClickListener: ((position: Int, button: SwitchCompat) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PluginViewHolder(
            ItemPluginListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: PluginViewHolder, position: Int) {
        val item = data[position]

        holder.module = item.module

        holder.binding.run {
            swEnabled.setOnCheckedChangeListener { _, isChecked ->
                statusLine.run {
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

            tvName.text = item.name
            tvModule.text = item.module
            tvAuthor.text = tvAuthor.context.getString(R.string.plugin_author_format, item.author)
            swEnabled.isChecked = item.status
            tvVersion.text = item.version
            tvDefaultState.setStateDrawableTop(item.defaultStatus)
            tvSuperuserOnly.setStateDrawableTop(item.limitSuperuser)
            tvCost.text = item.costGold.toString()
            tvGroupLevel.text = item.level.toString()
            tvMenuType.text = item.menuType

            llInfo0.setOnDebounceClickListener { onItemEditClickListener?.invoke(position) }
            llInfo1.setOnDebounceClickListener { onItemEditClickListener?.invoke(position) }
            llDivider1.setOnDebounceClickListener { onItemEditClickListener?.invoke(position) }

            swEnabled.setOnDebounceClickListener {
                onItemSwitchClickListener?.invoke(position, swEnabled)
            }
        }
    }

    private fun TextView.setStateDrawableTop(state: Boolean) {
        val bounds = compoundDrawablesRelative[1].bounds
        val drawable = ResourcesCompat.getDrawable(
            resources,
            if (state) R.drawable.ic_plugin_enabled else R.drawable.ic_plugin_disabled,
            null
        )?.apply { setBounds(bounds) }
        setCompoundDrawablesRelative(null, drawable, null, null)
    }
}