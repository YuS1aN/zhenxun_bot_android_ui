package me.kbai.zhenxunui.widget

import android.app.Activity
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import me.kbai.zhenxunui.R

/**
 * @author Sean on 2023/5/31
 */
open class ClipAreaImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var mTargetId: Int = 0
    private var mRoundRadius: Float = 0f
    private val mTargetLocation = IntArray(2)
    private val mSelfLocation = IntArray(2)
    private var mPaint: Paint = Paint()
    private var mPath = Path()
    private var mOutPath: Path? = null

    init {
        var typedArray: TypedArray? = null
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClipAreaImageView)
            mTargetId =
                typedArray.getResourceId(R.styleable.ClipAreaImageView_clipTarget, mTargetId)
            mRoundRadius =
                typedArray.getDimension(R.styleable.ClipAreaImageView_roundRadius, mRoundRadius)
        } finally {
            typedArray?.recycle()
        }

        mPaint.run {
            isAntiAlias = true
            style = Paint.Style.FILL
            xfermode = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
                PorterDuffXfermode(PorterDuff.Mode.DST_IN)
            } else {
                PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        val target = getTargetView()
        if (target == null) {
            super.onDraw(canvas)
            return
        }

        target.getLocationInWindow(mTargetLocation)
        getLocationInWindow(mSelfLocation)

        val clipLeft = mTargetLocation[0] - mSelfLocation[0]
        val clipTop = mTargetLocation[1] - mSelfLocation[1]
        val clipRight = clipLeft + target.width
        val clipBottom = clipTop + target.height

        resetRoundPath(target.width.toFloat(), target.height.toFloat())

        canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
        canvas.clipRect(clipLeft, clipTop, clipRight, clipBottom)
        super.onDraw(canvas)

        canvas.translate(clipLeft.toFloat(), clipTop.toFloat())
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            canvas.drawPath(mPath, mPaint)
        } else {
            canvas.drawPath(mOutPath!!, mPaint)
        }
        canvas.restore()
    }

    private fun getTargetView(): View? {
        if (mTargetId == 0) return null
        val context = context
        if (context is Activity) {
            return context.findViewById(mTargetId)
        }
        return (parent as ViewGroup).findViewById(mTargetId)
    }

    private fun resetRoundPath(targetWidth: Float, targetHeight: Float) {
        mPath.reset()
        mPath.addRoundRect(
            0f,
            0f,
            targetWidth,
            targetHeight,
            mRoundRadius,
            mRoundRadius,
            Path.Direction.CW
        )
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            if (mOutPath == null) {
                mOutPath = Path()
            }
            mOutPath!!.reset()
            mOutPath!!.addRect(-1f, -1f, targetWidth + 1, targetHeight + 1, Path.Direction.CW)
            mOutPath!!.op(mPath, Path.Op.DIFFERENCE)
        }
    }
}