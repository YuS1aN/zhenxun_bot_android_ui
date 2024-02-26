package me.kbai.zhenxunui.ui.plugin

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.databinding.ItemEditConfigBinding
import me.kbai.zhenxunui.databinding.LayoutPluginConfigsBinding
import me.kbai.zhenxunui.extends.addOnTabSelectedListener
import me.kbai.zhenxunui.extends.setOnProgressChangedListener
import me.kbai.zhenxunui.model.BlockType
import me.kbai.zhenxunui.model.PluginDetail
import me.kbai.zhenxunui.model.UpdatePlugin

/**
 * @author Sean on 2023/6/6
 */
class PluginConfigAdapter(
    val data: PluginDetail
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_HEAD = 0
        private const val TYPE_ITEM = 1
    }

    private var mHeadBinding: LayoutPluginConfigsBinding? = null

    var pluginMenuTypes: List<String>? = null
        set(value) {
            field = value
            if (value != null) mHeadBinding?.initHeadMenuType(value)
        }

    private val mEditableData: UpdatePlugin = data.createUpdateData()

    private val mTextWatchers: HashMap<ItemViewHolder, ConfigTextWatcher> = HashMap()

    fun checkAndGetUpdateData(): UpdatePlugin? {
        return null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_HEAD -> HeadViewHolder(
            LayoutPluginConfigsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                .also { mHeadBinding = it }
        )

        else -> ItemViewHolder(
            ItemEditConfigBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = 1 + (data.configList?.size ?: 0)

    override fun getItemViewType(position: Int) = if (position == 0) TYPE_HEAD else TYPE_ITEM

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) {
            bindHead((holder as HeadViewHolder).binding)
            return
        }

        val item = data.configList!![position - 1]

        (holder as ItemViewHolder).binding.run {
            tvKey.text = item.key
            tvDescription.text =
                tvDescription.context.getString(R.string.description_label, item.help)

            //TODO 插件参数
//            etValue.setText(mEditableData.configs[item.key]?.toString() ?: "")
//            val textWatcher = mTextWatchers[holder] ?: ConfigTextWatcher(item.key).also {
//                mTextWatchers[holder] = it
//            }
//            etValue.addTextChangedListener(textWatcher)
//            textWatcher.key = item.key
        }
    }

    private fun bindHead(binding: LayoutPluginConfigsBinding) = binding.run binding@{
        swEnabled.apply {
            setOnCheckedChangeListener { _, isChecked ->
                setBlockTypeVisibility(this@binding, !isChecked)
                mEditableData.blockType = if (isChecked) null
                else tlBlockType.getTabAt(tlBlockType.selectedTabPosition)?.tag as BlockType?
            }

            isChecked = mEditableData.blockType == null
        }

        tlBlockType.apply {
            removeAllTabs()
            val tabs = BlockType.entries.map {
                newTab().setText(it.nameResId).setTag(it)
            }
            tabs.forEach { addTab(it) }
            addOnTabSelectedListener {
                mEditableData.blockType = if (swEnabled.isChecked) null else it.tag as BlockType
            }

            if (mEditableData.blockType != null) {
                selectTab(tabs.find { tag == mEditableData.blockType })
            }
        }

        swDefaultStatus.apply {
            setOnCheckedChangeListener { _, isChecked ->
                mEditableData.defaultStatus = isChecked
            }

            isChecked = mEditableData.defaultStatus
        }

        swSuperuser.apply {
            setOnCheckedChangeListener { _, isChecked ->
                mEditableData.limitSuperuser = isChecked
            }

            isChecked = mEditableData.limitSuperuser
        }

        etCost.apply {
            addTextChangedListener {
                val cost = it.toString().toIntOrNull()
                if (cost != null) {
                    tilCost.isErrorEnabled = false
                }
                mEditableData.costGold = cost
            }

            setText(mEditableData.costGold?.toString() ?: "")
        }

        pluginMenuTypes?.also { initHeadMenuType(it) }

        sbGroupLevel.apply {
            setOnProgressChangedListener { progress, _ ->
                mEditableData.level = progress
                etGroupLevel.setText(progress.toString())
            }

            progress = mEditableData.level
        }
    }

    private fun LayoutPluginConfigsBinding.initHeadMenuType(menuTypes: List<String>) =
        spType.apply {
            if (adapter != null) return@apply

            adapter = ArrayAdapter(
                context, R.layout.item_config_spinner, 0, menuTypes
            )
            onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long
                ) {
                    mEditableData.menuType = menuTypes[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    mEditableData.menuType = data.menuType
                }
            }

            setSelection(menuTypes.indexOf(mEditableData.menuType))
        }

    private fun setBlockTypeVisibility(binding: LayoutPluginConfigsBinding, isVisible: Boolean) =
        binding.run {
            tlBlockType.isVisible = isVisible
            tvBlockType.isVisible = isVisible

            TransitionManager.beginDelayedTransition(binding.root)
            ConstraintSet()
                .apply {
                    clone(root)
                    connect(
                        swDefaultStatus.id, ConstraintSet.TOP,
                        if (isVisible) tvBlockType.id else swEnabled.id, ConstraintSet.BOTTOM
                    )
                    applyTo(root)
                }
        }

    inner class ConfigTextWatcher(var key: String) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val config = data.configList!!.find { it.key == key }
            config!!.run {
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
                if (newValue != null) mEditableData.configs[key] = newValue
            }
        }
    }

    private class HeadViewHolder(val binding: LayoutPluginConfigsBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ItemViewHolder(val binding: ItemEditConfigBinding) :
        RecyclerView.ViewHolder(binding.root)
}