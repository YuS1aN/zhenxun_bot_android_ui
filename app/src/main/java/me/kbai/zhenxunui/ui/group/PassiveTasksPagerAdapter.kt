package me.kbai.zhenxunui.ui.group

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.viewpager.widget.PagerAdapter
import me.kbai.zhenxunui.databinding.ItemPassiveTasksPageBinding
import me.kbai.zhenxunui.model.GroupInfo

private class ItemHolder(
    val binding: ItemPassiveTasksPageBinding
) {
    val children = arrayOf(
        binding.item0.root,
        binding.item1.root,
        binding.item2.root,
        binding.item3.root
    )
}

class PassiveTasksPagerAdapter(
    val data: List<GroupInfo.Task>
) : PagerAdapter() {
    private val mNewState = HashMap<String, Boolean>().also { map ->
        data.forEach {
            map[it.name] = it.status
        }
    }

    private val mViews = SparseArray<ItemHolder>()

    fun getEnabledTasks(): List<String> {
        val list = ArrayList<String>()
        mNewState.forEach {
            if (it.value) list.add(it.key)
        }
        return list
    }

    override fun getCount(): Int = (data.size + 3) shr 2

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val holder = mViews[position] ?: createPage(container, position)
        var index = position * 4

        holder.children.forEach {
            if (index < data.size) {
                val item = data[index]
                it.text = item.zhName
                it.isChecked = mNewState[item.name]!!
                it.setOnCheckedChangeListener { _, isChecked ->
                    mNewState[item.name] = isChecked
                }
                it.isVisible = true
                index++
            } else {
                it.isVisible = false
            }
        }
        return holder.binding.root.also { container.addView(it) }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(mViews[position].binding.root)
        mViews.remove(position)
    }

    private fun createPage(container: ViewGroup, position: Int) =
        ItemHolder(
            ItemPassiveTasksPageBinding.inflate(
                LayoutInflater.from(container.context),
                container,
                false
            )
        ).also { mViews[position] = it }
}