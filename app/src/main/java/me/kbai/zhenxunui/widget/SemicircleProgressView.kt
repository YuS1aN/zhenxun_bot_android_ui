package me.kbai.zhenxunui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import me.kbai.zhenxunui.R
import me.kbai.zhenxunui.extends.dp
import me.kbai.zhenxunui.extends.getThemeColor
import kotlin.math.min

class SemicircleProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val SMOOTH_ANIM_TIME = 500F
    }

    private var mStrokeWidth: Int

    private val mBackgroundPaint: Paint
    private val mFillPaint: Paint

    private var mTargetProgress = 0f
    private var mCurProgress = 0f
    private var mProgressWhenSetProgress = 0f
    private var mTimestampWhenSetProgress = 0L

    init {
        context.obtainStyledAttributes(attrs, R.styleable.SemicircleProgressView).use {
            mStrokeWidth = it.getDimensionPixelSize(
                R.styleable.SemicircleProgressView_strokeWidth, 4.dp.toInt()
            )
        }
        mBackgroundPaint = initPaint().apply {
            color = context.getColor(R.color.switch_unchecked)
        }
        mFillPaint = initPaint().apply {
            color = context.getThemeColor(android.R.attr.colorPrimaryDark)
        }
    }

    private fun initPaint() = Paint().apply {
        strokeWidth = mStrokeWidth.toFloat()
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    fun setProgressSmooth(progress: Float) {
        mProgressWhenSetProgress = mCurProgress
        mTargetProgress = progress
        mTimestampWhenSetProgress = System.currentTimeMillis()
        invalidate()
    }

    fun setProgressImmediately(progress: Float) {
        mTargetProgress = progress
        mCurProgress = progress
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
            val widthSize = MeasureSpec.getSize(widthMeasureSpec)
            setMeasuredDimension(
                widthSize,
                (widthSize - paddingLeft - paddingRight) / 2 + paddingTop + paddingBottom
            )
        } else if (widthMode != MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            val heightSize = MeasureSpec.getSize(heightMeasureSpec)
            setMeasuredDimension(
                (heightSize - paddingTop - paddingBottom) * 2 + paddingLeft + paddingRight,
                heightSize
            )
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onDraw(canvas: Canvas) {
        val diameter = min(
            width - paddingLeft - paddingRight,
            (height - paddingTop - paddingBottom) * 2
        ) - mStrokeWidth
        val left = paddingLeft + mStrokeWidth / 2f
        val top = paddingTop + mStrokeWidth / 2f
        val right = left + diameter
        val bottom = top + diameter

        canvas.drawArc(left, top, right, bottom, -180f, 180f, false, mBackgroundPaint)
        canvas.drawArc(left, top, right, bottom, -180f, mCurProgress * 180, false, mFillPaint)

        if (mCurProgress != mTargetProgress) {
            var percent =
                (System.currentTimeMillis() - mTimestampWhenSetProgress) / SMOOTH_ANIM_TIME
            if (percent > 1) percent = 1f
            mCurProgress =
                mProgressWhenSetProgress + (mTargetProgress - mProgressWhenSetProgress) * percent
            postInvalidateOnAnimation()
        }
    }
}