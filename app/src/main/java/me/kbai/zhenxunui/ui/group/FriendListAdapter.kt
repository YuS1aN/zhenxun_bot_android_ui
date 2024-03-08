package me.kbai.zhenxunui.ui.group

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.databinding.ItemFriendListBinding
import me.kbai.zhenxunui.databinding.ItemFriendListClassifyBinding
import me.kbai.zhenxunui.extends.setOnDebounceClickListener
import me.kbai.zhenxunui.model.FriendListItem
import me.kbai.zhenxunui.model.GroupListItem
import me.kbai.zhenxunui.tool.glide.GlideApp

/**
 * @author Sean on 2023/6/7
 */
class FriendListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_GROUP_CLASSIFY = 0
        private const val TYPE_FRIEND_CLASSIFY = 1
        private const val TYPE_GROUP = 2
        private const val TYPE_FRIEND = 3
    }

    var groupData: List<GroupListItem> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var friendData: List<FriendListItem> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onGroupClickListener: ((GroupListItem) -> Unit)? = null
    var onFriendClickListener: ((FriendListItem) -> Unit)? = null

    var expandGroup = true
    var expandFriend = true

    private var mRecyclerView: RecyclerView? = null

    class ItemViewHolder(val binding: ItemFriendListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setVisible(visible: Boolean) {
            itemView.layoutParams = (itemView.layoutParams ?: RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )).apply {
                if (visible) {
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                } else {
                    width = 0
                    height = 0
                }
            }
        }
    }

    class ClassifyViewHolder(val binding: ItemFriendListClassifyBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        mRecyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        mRecyclerView = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_GROUP_CLASSIFY, TYPE_FRIEND_CLASSIFY -> ClassifyViewHolder(
            ItemFriendListClassifyBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

        else -> ItemViewHolder(
            ItemFriendListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount() = 2 + groupData.size + friendData.size

    override fun getItemViewType(position: Int) = when (position) {
        0 -> TYPE_GROUP_CLASSIFY
        in 1..groupData.size -> TYPE_GROUP
        groupData.size + 1 -> TYPE_FRIEND_CLASSIFY
        else -> TYPE_FRIEND
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_GROUP_CLASSIFY -> (holder as ClassifyViewHolder).onBindGroup()
            TYPE_FRIEND_CLASSIFY -> (holder as ClassifyViewHolder).onBindFriend()
            TYPE_GROUP -> (holder as ItemViewHolder).onBindGroup(groupData[position - 1])
            TYPE_FRIEND -> (holder as ItemViewHolder).onBindFriend(friendData[position - 2 - groupData.size])
        }
    }

    private fun ClassifyViewHolder.onBindGroup() = binding.run {
        tvClassify.setText(R.string.groups)
        root.setOnClickListener {
            expandGroup = !expandGroup

            if (expandGroup) {
                ValueAnimator.ofFloat(-90f, 0f)
            } else {
                ValueAnimator.ofFloat(0f, -90f)
            }.apply {
                duration = 250

                addUpdateListener {
                    ivArrow.rotation = it.animatedValue as Float
                }
            }.start()

            mRecyclerView?.run {
                for (i in 1..groupData.size) {
                    val holder = findViewHolderForAdapterPosition(i) ?: break

                    (holder as ItemViewHolder).setVisible(expandGroup)
                }
            }
        }
        ivArrow.rotation = if (expandGroup) 0f else -90f
    }

    private fun ClassifyViewHolder.onBindFriend() = binding.run {
        tvClassify.setText(R.string.friends)
        root.setOnClickListener {
            expandFriend = !expandFriend

            if (expandFriend) {
                ValueAnimator.ofFloat(-90f, 0f)
            } else {
                ValueAnimator.ofFloat(0f, -90f)
            }.apply {
                duration = 250

                addUpdateListener {
                    ivArrow.rotation = it.animatedValue as Float
                }
            }.start()

            mRecyclerView?.run {
                val begin = groupData.size + 2
                val end = begin + friendData.size

                for (i in begin..<end) {
                    val holder = findViewHolderForAdapterPosition(i) ?: break

                    (holder as ItemViewHolder).setVisible(expandFriend)
                }
            }
        }
        ivArrow.rotation = if (expandFriend) 0f else -90f
    }

    private fun ItemViewHolder.onBindGroup(item: GroupListItem) = binding.run {
        GlideApp.with(ivAvatar)
            .load(item.avatarUrl)
            .into(ivAvatar)
        tvNickname.text = item.groupName
        tvId.text = item.groupId
        root.setOnDebounceClickListener { onGroupClickListener?.invoke(item) }

        setVisible(expandGroup)
    }

    private fun ItemViewHolder.onBindFriend(item: FriendListItem) = binding.run {
        GlideApp.with(ivAvatar)
            .load(item.avatarUrl)
            .into(ivAvatar)
        tvNickname.text = item.nickname
        tvId.text = item.userId
        root.setOnDebounceClickListener { onFriendClickListener?.invoke(item) }

        setVisible(expandFriend)
    }
}