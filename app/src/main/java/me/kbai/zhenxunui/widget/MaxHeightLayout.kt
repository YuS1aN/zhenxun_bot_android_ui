package me.kbai.zhenxunui.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.res.use
import me.kbai.zhenxunui.R

class MaxHeightLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var mMaxHeight = -1

    init {
        context.obtainStyledAttributes(attrs, R.styleable.MaxHeightLayout).use {
            mMaxHeight = it.getDimensionPixelSize(R.styleable.MaxHeightLayout_maxHeight, mMaxHeight)
        }
    }

    fun setMaxHeight(maxHeight: Int) {
        mMaxHeight = maxHeight
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpec = if (mode == MeasureSpec.AT_MOST && mMaxHeight >= 0) {
            MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST)
        } else {
            heightMeasureSpec
        }
        super.onMeasure(widthMeasureSpec, heightSpec)
    }
}