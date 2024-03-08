package me.kbai.zhenxunui.ui.db

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.kbai.zhenxunui.databinding.ItemDbManageTableBinding
import me.kbai.zhenxunui.extends.setOnDebounceClickListener
import me.kbai.zhenxunui.model.TableListItem
import me.kbai.zhenxunui.viewmodel.DbManageViewModel

class DbTableAdapter(
    private val data: List<TableListItem>,
    private val viewModel: DbManageViewModel,
    private val coroutineScope: CoroutineScope
) : RecyclerView.Adapter<DbTableAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemDbManageTableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = data[position]

        holder.binding.run {
            tvTableName.text = item.name
            tvDescription.text = item.desc

            root.setOnDebounceClickListener {
                elFields.toggleExpand()

                if (rvFields.adapter != null) {
                    return@setOnDebounceClickListener
                }
                coroutineScope.launch {
                    viewModel.getColumns(item.name)
                        .collect {
                            if (!holder.recycled) {
                                delay(elFields.remainPlayTime)
                                pbWaiting.isVisible = false
                                rvFields.adapter = TableFieldAdapter(it)
                                rvFields.post { elFields.expand() }
                            }
                        }
                }
            }

            elFields.collapse(false)
            pbWaiting.isVisible = true
            holder.recycled = false
        }
    }

    override fun onViewRecycled(holder: ItemViewHolder) = holder.binding.run {
        holder.recycled = true
        rvFields.adapter = null
    }

    class ItemViewHolder(val binding: ItemDbManageTableBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var recycled: Boolean = false
    }
}