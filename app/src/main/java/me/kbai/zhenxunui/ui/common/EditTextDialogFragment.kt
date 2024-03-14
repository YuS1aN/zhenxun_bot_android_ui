package me.kbai.zhenxunui.ui.common

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.annotation.StringRes
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.base.BaseDialogFragment
import me.kbai.zhenxunui.databinding.DialogEditTextBinding
import me.kbai.zhenxunui.extends.displaySize
import me.kbai.zhenxunui.model.Consumable
import java.lang.ref.WeakReference

class EditTextDialogFragment : BaseDialogFragment() {

    companion object {
        const val NEW_FILE = 1
        const val NEW_FOLDER = 2
        const val RENAME = 3
        const val EDIT_FILE = 4

        private const val ARGS_TITLE_RES = "1"
        private const val ARGS_TEXT = "2"
        private const val ARGS_ID = "3"
        private const val ARGS_MAX_LINE = "4"

        private val _result: MutableLiveData<Consumable<String>> = MutableLiveData()
        val result: LiveData<Consumable<String>> = _result

        private val _dialogMap: MutableMap<Int, WeakReference<EditTextDialogFragment>> = HashMap()
        val dialogMap: Map<Int, WeakReference<EditTextDialogFragment>> = _dialogMap

        fun newInstance(
            @StringRes titleRes: Int,
            defaultText: String,
            id: Int,
            maxLine: Int = 1
        ): EditTextDialogFragment {
            val args = Bundle()
            args.putInt(ARGS_TITLE_RES, titleRes)
            args.putString(ARGS_TEXT, defaultText)
            args.putInt(ARGS_ID, id)
            args.putInt(ARGS_MAX_LINE, maxLine)
            val fragment = EditTextDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var binding: DialogEditTextBinding

    init {
        maxWidth = Int.MAX_VALUE
        isCancelable = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _dialogMap[arguments?.getInt(ARGS_ID) ?: 0] = WeakReference(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = DialogEditTextBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arguments = arguments ?: return
        val id = arguments.getInt(ARGS_ID)

        binding.run {
            tvTitle.setText(arguments.getInt(ARGS_TITLE_RES))
            val defaultText = arguments.getString(ARGS_TEXT)
            etText.apply {
                maxLines = arguments.getInt(ARGS_MAX_LINE, 1)
                var type = InputType.TYPE_CLASS_TEXT
                if (maxLines > 1) type = type or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                inputType = type
                setText(defaultText)
                setHint(defaultText)
            }
            btnCancel.setOnClickListener { dismiss() }
            btnConfirm.setOnClickListener {
                val text = binding.etText.text
                if (text.isNullOrBlank()) {
                    binding.etText.error = getString(R.string.error_input_text)
                    return@setOnClickListener
                }
                _result.value = Consumable(text.toString(), id)
            }
            mhlText.post {
                val size = dialog?.window?.displaySize() ?: return@post
                mhlText.setMaxHeight(size.height - root.paddingTop - root.paddingBottom - tvTitle.height - llButton.height - llButton.marginTop)
                mhlText.updateLayoutParams<LayoutParams> {
                    height = LayoutParams.WRAP_CONTENT
                }
            }
        }
    }
}