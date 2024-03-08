package me.kbai.zhenxunui.ui.plugin

import android.content.Context
import android.graphics.Typeface
import android.text.InputType
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.databinding.BtnEditConfigValueListAddBinding
import me.kbai.zhenxunui.databinding.ItemEditConfigBinding
import me.kbai.zhenxunui.databinding.ItemEditConfigValueBoolBinding
import me.kbai.zhenxunui.databinding.ItemEditConfigValueListBoolBinding
import me.kbai.zhenxunui.databinding.ItemEditConfigValueListTextBinding
import me.kbai.zhenxunui.databinding.ItemEditConfigValueTextBinding
import me.kbai.zhenxunui.databinding.LayoutPluginConfigsBinding
import me.kbai.zhenxunui.extends.addOnTabSelectedListener
import me.kbai.zhenxunui.extends.setOnProgressChangedListener
import me.kbai.zhenxunui.model.BlockType
import me.kbai.zhenxunui.model.ConfigValueType
import me.kbai.zhenxunui.model.PluginDetail
import me.kbai.zhenxunui.model.UpdatePlugin
import me.kbai.zhenxunui.tool.GlobalToast
import kotlin.math.max

/**
 * @author Sean on 2023/6/6
 */
class PluginConfigAdapter(
    val data: PluginDetail
) : RecyclerView.Adapter<PluginConfigAdapter.TextWatcherHolder>() {
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

    fun checkAndGetUpdateData(): UpdatePlugin? {
        if (mEditableData.costGold == null) {
            GlobalToast.showToast(R.string.error_cost_gold)
            return null
        }
        if (mEditableData.menuType.isBlank()) {
            GlobalToast.showToast(R.string.error_menu_type)
            return null
        }
        return mEditableData
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

    override fun onBindViewHolder(holder: TextWatcherHolder, position: Int) {
        if (position == 0) {
            bindHead(holder as HeadViewHolder)
            return
        }

        val item = data.configList!![position - 1]

        (holder as ItemViewHolder).binding.run {
            tvKey.text = item.key
            tvDescription.text =
                tvDescription.context.getString(R.string.description_label, item.help)

            when (item.type) {
                ConfigValueType.BOOL -> setBoolValueConfig(llValue, item)
                ConfigValueType.LIST, ConfigValueType.TUPLE -> setListValueConfig(holder, item)
                else -> setTextValueConfig(holder, item)
            }
        }
    }

    override fun onViewRecycled(holder: TextWatcherHolder) = holder.run {
        editTexts.forEach {
            it.removeTextChangedListener(it.tag as TextWatcher)
        }
        editTexts.clear()
    }

    private fun bindHead(holder: HeadViewHolder) = holder.binding.run binding@{
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

            clearOnTabSelectedListeners()
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
            val textWatcher = addTextChangedListener {
                val cost = it.toString().toIntOrNull()
                mEditableData.costGold = cost
            }
            tag = textWatcher
            holder.editTexts.add(this)
            setText(mEditableData.costGold?.toString())
        }

        pluginMenuTypes?.also { initHeadMenuType(it) }

        sbGroupLevel.apply {
            setOnProgressChangedListener { progress, _ ->
                mEditableData.level = progress
                etGroupLevel.setText(progress.toString())
            }

            progress = mEditableData.level
        }

        tvExtraConfigs.isVisible = (data.configList?.size ?: 0) > 0
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

    private fun setBoolValueConfig(layout: LinearLayout, config: PluginDetail.Config) {
        layout.removeAllViews()
        val binding = ItemEditConfigValueBoolBinding.inflate(
            LayoutInflater.from(layout.context),
            layout,
            true
        )
        binding.tvDefaultValue.text = defaultValueSpan(layout.context, config)
        binding.swValue.setOnCheckedChangeListener { _, isChecked ->
            mEditableData.configs[config.key] = isChecked
        }
        binding.swValue.isChecked = mEditableData.configs[config.key] as Boolean? ?: false
    }

    private fun setTextValueConfig(holder: ItemViewHolder, config: PluginDetail.Config) =
        holder.binding.run {
            llValue.removeAllViews()
            val binding = ItemEditConfigValueTextBinding.inflate(
                LayoutInflater.from(llValue.context),
                llValue,
                true
            )
            binding.tvDefaultValue.text = defaultValueSpan(llValue.context, config)
            binding.etValue.inputType = getConfigInputType(config.type)
            binding.etValue.apply {
                val textWatcher = addTextChangedListener {
                    val strVal = it?.toString()
                    try {
                        mEditableData.configs[config.key] = when (config.type) {
                            ConfigValueType.INT -> strVal?.toLong()
                            ConfigValueType.FLOAT -> strVal?.toDouble()
                            ConfigValueType.BOOL -> strVal?.lowercase()?.toBoolean()
                            else -> strVal
                        }
                    } catch (e: NumberFormatException) {
                        //
                    }
                }
                tag = textWatcher
                holder.editTexts.add(binding.etValue)
            }
            binding.etValue.setText(mEditableData.configs[config.key]?.toString())
        }

    private fun defaultValueSpan(context: Context, config: PluginDetail.Config) = SpannableString(
        context.getString(R.string.default_value_format, config.defaultValue ?: "")
    ).apply {
        setSpan(StyleSpan(Typeface.BOLD), 0, indexOf(':') + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun setListValueConfig(holder: ItemViewHolder, config: PluginDetail.Config) =
        holder.binding.run {
            llValue.removeAllViews()
            val end = max(
                config.typeInner?.size ?: 0,
                (mEditableData.configs[config.key] as List<*>?)?.size ?: 0
            )

            for (index in 0..<end) {
                addListValue(index, holder, config)
            }
            val btnAdd = BtnEditConfigValueListAddBinding.inflate(
                LayoutInflater.from(llValue.context),
                llValue,
                true
            )
            btnAdd.root.setOnClickListener {
                addListValue(llValue.size - 1, holder, config)
            }
        }

    private fun addListValue(
        index: Int,
        holder: ItemViewHolder,
        config: PluginDetail.Config
    ) = holder.binding.run {
        val inflater = LayoutInflater.from(llValue.context)
        val typeInnerSize = config.typeInner?.size ?: 0

        val type = if (index < typeInnerSize) {
            config.typeInner!![index]
        } else if (typeInnerSize > 0) {
            config.typeInner!![0]
        } else {
            ConfigValueType.STRING
        }

        val child = when (type) {
            ConfigValueType.BOOL -> ItemEditConfigValueListBoolBinding.inflate(
                inflater,
                llValue,
                false
            ).apply { initListBoolValue(llValue, index, config) }

            else -> ItemEditConfigValueListTextBinding.inflate(inflater, llValue, false)
                .apply { initListTextValue(llValue, holder, index, type, config) }
        }
        llValue.addView(child.root, index)
    }

    private fun ItemEditConfigValueListBoolBinding.initListBoolValue(
        parent: ViewGroup,
        index: Int,
        config: PluginDetail.Config
    ) {
        @Suppress("UNCHECKED_CAST")
        val list = mEditableData.configs[config.key] as MutableList<Any?>? ?: ArrayList()
        for (i in list.size..index) {
            list.add(null)
        }
        if (index > (config.typeInner?.size ?: 1)) ivReduce.isVisible = true
        swValue.isChecked = list[index] as Boolean? ?: false
        mEditableData.configs[config.key] = list

        ivReduce.setOnClickListener {
            list.removeAt(parent.indexOfChild(root))
            parent.removeView(root)
        }
        swValue.setOnCheckedChangeListener { _, isChecked ->
            list[parent.indexOfChild(root)] = isChecked
        }
    }

    private fun ItemEditConfigValueListTextBinding.initListTextValue(
        parent: ViewGroup,
        holder: ItemViewHolder,
        index: Int,
        innerType: ConfigValueType,
        config: PluginDetail.Config
    ) {
        @Suppress("UNCHECKED_CAST")
        val list = mEditableData.configs[config.key] as MutableList<Any?>? ?: ArrayList()
        for (i in list.size..index) {
            list.add(null)
        }
        if (index >= (config.typeInner?.size ?: 1)) ivReduce.isVisible = true
        ivReduce.setOnClickListener {
            list.removeAt(parent.indexOfChild(root))
            parent.removeView(root)
        }
        etValue.inputType = getConfigInputType(innerType)
        etValue.setText(list[index]?.toString())
        mEditableData.configs[config.key] = list

        val textWatcher = etValue.addTextChangedListener {
            list[parent.indexOfChild(root)] = it?.toString() ?: ""
        }
        etValue.tag = textWatcher
        holder.editTexts.add(etValue)
    }

    private fun getConfigInputType(config: ConfigValueType?) = when (config) {
        ConfigValueType.INT -> InputType.TYPE_CLASS_NUMBER
        ConfigValueType.FLOAT -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        else -> InputType.TYPE_CLASS_TEXT
    }

    open class TextWatcherHolder(view: View) : RecyclerView.ViewHolder(view) {
        val editTexts: ArrayList<EditText> = ArrayList()
    }

    private class HeadViewHolder(val binding: LayoutPluginConfigsBinding) :
        TextWatcherHolder(binding.root)

    private class ItemViewHolder(val binding: ItemEditConfigBinding) :
        TextWatcherHolder(binding.root)
}