package me.kbai.zhenxunui.ui.db

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.databinding.ItemDbManageFieldBinding
import me.kbai.zhenxunui.model.TableColumn

class TableFieldAdapter(
    private val data: List<TableColumn>
) : RecyclerView.Adapter<TableFieldAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemDbManageFieldBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.binding.run {
        val item = data[position]

        tvName.text = item.name
        tvType.text = item.type
        tvMaxLength.text =
            tvMaxLength.context.getString(
                R.string.max_length_format,
                item.maxLength?.toString().orEmpty()
            )
        tvNullable.text = tvNullable.context.getString(R.string.nullable_format, item.nullable)
    }

    class ItemViewHolder(val binding: ItemDbManageFieldBinding) :
        RecyclerView.ViewHolder(binding.root)
}