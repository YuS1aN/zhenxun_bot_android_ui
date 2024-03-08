package me.kbai.zhenxunui.ui.db

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.kbai.zhenxunui.databinding.ItemSqlResultRowBinding

class SqlResultRowAdapter(
    val data: List<Map<String, *>>
) : RecyclerView.Adapter<SqlResultRowAdapter.ItemViewHolder>() {

    class ItemViewHolder(val binding: ItemSqlResultRowBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemSqlResultRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.rvValues.adapter = SqlResultValueAdapter(data[position])
    }
}