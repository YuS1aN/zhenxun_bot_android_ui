package me.kbai.zhenxunui.ui.group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.kbai.zhenxunui.databinding.ItemPluginStatusSwitchBinding
import me.kbai.zhenxunui.model.GroupInfo
import me.kbai.zhenxunui.model.PluginInfo


class PluginStatusAdapter(
    val data: List<PluginInfo>,
    group: GroupInfo
) : RecyclerView.Adapter<PluginStatusAdapter.ViewHolder>() {
    private val mNewState = HashMap<String, Boolean>().apply {
        data.forEach {
            put(it.module, true)
        }
        group.closedPlugins.forEach {
            put(it, false)
        }
    }

    fun getClosedPlugins(): List<String> = mNewState.filter { !it.value }.map { it.key }

    class ViewHolder(val binding: ItemPluginStatusSwitchBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemPluginStatusSwitchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int): Unit =
        holder.binding.root.run {
            val item = data[position]

            text = item.name
            setOnCheckedChangeListener { _, isChecked ->
                mNewState[item.module] = isChecked
            }
            isChecked = mNewState[item.module]!!
        }
}