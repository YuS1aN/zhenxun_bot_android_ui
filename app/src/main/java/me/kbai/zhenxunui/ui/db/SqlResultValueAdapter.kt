package me.kbai.zhenxunui.ui.db

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.kbai.zhenxunui.databinding.ItemSqlResultValueBinding

class SqlResultValueAdapter(
    map: Map<String, *>
) : RecyclerView.Adapter<SqlResultValueAdapter.ItemViewHolder>() {
    val data: List<Map.Entry<String, *>> = ArrayList<Map.Entry<String, *>>(map.entries)

    class ItemViewHolder(val binding: ItemSqlResultValueBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemSqlResultValueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = data[position]

        holder.binding.run {
            tvField.text = item.key
            tvValue.text = when (item.value) {
                is String -> "\"${item.value}\""
                else -> item.value.toString()
            }
        }
    }
}