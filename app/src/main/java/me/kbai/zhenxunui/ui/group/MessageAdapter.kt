package me.kbai.zhenxunui.ui.group

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import me.kbai.zhenxunui.Constants
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.databinding.ItemMessageReceiveBinding
import me.kbai.zhenxunui.databinding.ItemMessageTextBinding
import me.kbai.zhenxunui.model.ChatMessage
import me.kbai.zhenxunui.model.MessageType
import me.kbai.zhenxunui.tool.glide.GlideApp

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding: ItemMessageReceiveBinding
        get() = ItemMessageReceiveBinding.bind(itemView)
}

const val SENDER = 1
const val RECEIVER = 0

class MessageAdapter : RecyclerView.Adapter<ViewHolder>() {
    private val mData: MutableList<ChatMessage> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        if (viewType == SENDER) {
            return ViewHolder(inflater.inflate(R.layout.item_message_send, parent, false))
        }
        return ViewHolder(inflater.inflate(R.layout.item_message_receive, parent, false))
    }

    override fun getItemCount() = mData.size

    override fun getItemViewType(position: Int): Int =
        if (mData[position].userId == Constants.currentBot?.selfId) {
            SENDER
        } else {
            RECEIVER
        }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData[position]

        holder.binding.run {
            GlideApp.with(ivAvatar)
                .load(item.avatarUrl)
                .into(ivAvatar)
            tvNickname.text = item.name
            llMessage.apply {
                removeAllViews()
                var needMargin = false

                for (msg in item.message) {
                    when (msg.type) {
                        MessageType.TEXT -> ItemMessageTextBinding.inflate(
                            LayoutInflater.from(context), this, true
                        ).apply {
                            root.text = msg.msg
                            if (needMargin) {
                                root.updateLayoutParams<MarginLayoutParams> {
                                    topMargin =
                                        resources.getDimensionPixelSize(R.dimen.margin_normal)
                                }
                            }
                        }

                    }
                    needMargin = true
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<ChatMessage>) {
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }

    fun addData(item: ChatMessage) {
        if (mData.isEmpty() || mData.last() != item) {
            mData.add(item)
            notifyItemInserted(mData.size - 1)
        }
    }
}