package me.kbai.zhenxunui.ui.group

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.databinding.ItemGroupListBinding
import me.kbai.zhenxunui.ext.setOnDebounceClickListener
import me.kbai.zhenxunui.model.GroupInfo
import java.time.format.TextStyle

/**
 * @author Sean on 2023/6/7
 */
class GroupListAdapter : RecyclerView.Adapter<GroupListAdapter.ViewHolder>() {
    var data: List<GroupInfo> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onItemClickListener: ((position: Int) -> Unit)? = null

    private lateinit var mColorSpan: ForegroundColorSpan
    private val mStyleSpan = StyleSpan(Typeface.BOLD)

    class ViewHolder(val binding: ItemGroupListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemGroupListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        val context = holder.binding.root.context

        holder.binding.run {

            if (!this@GroupListAdapter::mColorSpan.isInitialized) {
                mColorSpan = ForegroundColorSpan(
                    ResourcesCompat.getColor(root.resources, R.color.dn_black, null)
                )
            }

            tvName.text = SpannableString(item.group.groupName).applyLabelStyle()

            tvId.text = SpannableString(
                context.getString(
                    R.string.group_id_format,
                    item.group.groupId
                )
            ).applyLabelStyle()

            tvMemberNum.text = SpannableString(
                context.getString(
                    R.string.member_num_format,
                    item.group.memberCount,
                    item.group.maxMemberCount
                )
            ).applyLabelStyle()

            tvGroupLevel.text = SpannableString(
                context.getString(
                    R.string.group_level_format,
                    item.level
                )
            ).applyLabelStyle()

            val tasks = StringBuilder()
            item.task.filter { it.status }
                .map { it.nameZh }
                .forEach {
                    tasks.append(it).append(", ")
                }
            tvEnabledTask.text = SpannableString(
                context.getString(
                    R.string.enabled_task_format,
                    tasks.delete(tasks.length - 2, tasks.length)
                )
            ).applyLabelStyle()

            tvStatus.isSelected = item.status
            tvStatus.setText(if (item.status) R.string.enable else R.string.disable)

            root.setOnDebounceClickListener {
                onItemClickListener?.invoke(position)
            }
        }
    }

    private fun SpannableString.applyLabelStyle() = apply {
        val end = indexOf(':') + 1
        setSpan(mColorSpan, 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        setSpan(mStyleSpan, 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}