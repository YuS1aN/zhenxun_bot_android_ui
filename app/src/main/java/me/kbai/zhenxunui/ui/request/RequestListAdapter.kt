package me.kbai.zhenxunui.ui.request

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.databinding.ItemRequestListBinding
import me.kbai.zhenxunui.model.BotRequest

/**
 * @author Sean on 2023/6/8
 */
class RequestListAdapter : RecyclerView.Adapter<RequestListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemRequestListBinding) : RecyclerView.ViewHolder(binding.root) {
        internal var oid: String? = null

        internal fun setEnabled(enabled: Boolean): Unit = binding.run {
            tvApprove.isEnabled = enabled
            tvRefuse.isEnabled = enabled
            tvDelete.isEnabled = enabled
        }
    }

    var data: List<BotRequest> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            mNonEditableSet.clear()
            mHolderMap.clear()
            notifyDataSetChanged()
        }

    private val mNonEditableSet: HashSet<String> = HashSet()
    private val mHolderMap: HashMap<String, ViewHolder> = HashMap()

    var onItemClickListener: ((handle: String, request: BotRequest) -> Unit)? =
        null

    private lateinit var mColorSpan: ForegroundColorSpan
    private val mStyleSpan = StyleSpan(Typeface.BOLD)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemRequestListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        val context = holder.binding.root.context

        mHolderMap[item.botId] = holder
        holder.oid = item.botId

        holder.binding.run {
            if (!this@RequestListAdapter::mColorSpan.isInitialized) {
                mColorSpan = ForegroundColorSpan(
                    ResourcesCompat.getColor(root.resources, R.color.dn_black, null)
                )
            }

            tvNickname.text = context.getSpannableString(R.string.nickname_format, item.nickname)
                .applyLabelStyle()
            tvQq.text = context.getSpannableString(R.string.qq_format, item.id).applyLabelStyle()
            tvSex.text = context.getSpannableString(R.string.sex_format, item.sex).applyLabelStyle()
            tvAge.text = context.getSpannableString(R.string.age_format, item.age).applyLabelStyle()

            val inviteGroup = item.inviteGroup != null
            llGroup.isVisible = inviteGroup
            if (inviteGroup) {
                tvInviteGroup.text =
                    context.getSpannableString(
                        R.string.invite_group_format,
                        item.groupName.orEmpty()
                    )
                tvGroupId.text =
                    context.getSpannableString(R.string.group_id_format, item.inviteGroup)
            }
            tvGreet.text = context.getSpannableString(R.string.greet_format, item.comment.orEmpty())

            tvApprove.setOnClickListener {
                onItemClickListener?.invoke("approve", item)
            }
            tvRefuse.setOnClickListener {
                onItemClickListener?.invoke("refuse", item)
            }
            tvDelete.setOnClickListener {
                onItemClickListener?.invoke("delete", item)
            }
            holder.setEnabled(!mNonEditableSet.contains(item.botId))
        }
    }

    fun setItemEnabled(oid: String, enabled: Boolean) {
        mHolderMap[oid]?.let {
            if (it.oid == oid) it.setEnabled(enabled)
        }
        if (enabled) {
            mNonEditableSet.remove(oid)
        } else {
            mNonEditableSet.add(oid)
        }
    }

    private fun Context.getSpannableString(@StringRes resId: Int, vararg formatArgs: Any?) =
        SpannableString(
            getString(resId, *formatArgs)
        )

    private fun SpannableString.applyLabelStyle() = apply {
        val end = indexOf(':') + 1
        setSpan(mColorSpan, 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        setSpan(mStyleSpan, 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}