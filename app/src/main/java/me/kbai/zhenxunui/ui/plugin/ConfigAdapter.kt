package me.kbai.zhenxunui.ui.plugin

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.databinding.ItemEditConfigBinding
import me.kbai.zhenxunui.model.PluginData
import me.kbai.zhenxunui.model.UpdateConfig

/**
 * @author Sean on 2023/6/6
 */
class ConfigAdapter : RecyclerView.Adapter<ConfigAdapter.ViewHolder>() {
    var data: List<PluginData.Config> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    val updateConfigs: HashMap<String, UpdateConfig> = HashMap()
    private val mTextWatchers: HashMap<ViewHolder, ConfigTextWatcher> = HashMap()

    class ViewHolder(val binding: ItemEditConfigBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemEditConfigBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        if (updateConfigs[item.key] == null) {
            updateConfigs[item.key] = UpdateConfig(item.module, item.key, item.value)
        }
        val update = updateConfigs[item.key]!!

        holder.binding.run {
            tvKey.text = update.key
            etValue.setText(update.value?.toString() ?: "")
            tvDescription.text =
                tvDescription.context.getString(R.string.description_label, item.help)

            val textWatcher = mTextWatchers[holder]
                ?: ConfigTextWatcher(item.key).also {
                    mTextWatchers[holder] = it
                    etValue.addTextChangedListener(it)
                }
            textWatcher.key = item.key
        }
    }

    inner class ConfigTextWatcher(var key: String) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            updateConfigs[key]!!.run {
                val newValue = try {
                    when (value) {
                        is Boolean -> s.toString().toBoolean()
                        is Int -> s.toString().toInt()
                        is Long -> s.toString().toLong()
                        else -> s?.toString()
                    }
                } catch (e: Exception) {
                    null
                }
                if (newValue != null) value = newValue
            }
        }
    }
}