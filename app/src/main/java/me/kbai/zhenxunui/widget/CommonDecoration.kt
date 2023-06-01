package me.kbai.zhenxunui.widget

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

/**
 * @author sean
 */
class CommonDecoration @JvmOverloads constructor(
    private val space: Int,
    private val type: Int = 0,
    private val head: Boolean = true,
    private val end: Boolean = true,
    private val otherSide: Boolean = true,
    private val dividerColor: Int? = null
) : RecyclerView.ItemDecoration() {
    companion object {
        const val VERTICAL = 0
        const val HORIZONTAL = 1
    }

    private val mPaint by lazy {
        Paint().apply {
            color = dividerColor!!
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getChildAdapterPosition(view) != state.itemCount - 1 || end) {
            if (type == VERTICAL) {
                outRect.bottom = space
                outRect.left = space
                outRect.right = space
            } else {
                outRect.right = space
                outRect.top = space
                outRect.bottom = space
            }
        }

        if (head && parent.getChildAdapterPosition(view) == 0) {
            if (type == VERTICAL) {
                outRect.top = space
            } else {
                outRect.left = space
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (dividerColor == null) return

        if (type == VERTICAL) {
            drawDividerForVertical(c, parent)
        }
    }

    private fun drawDividerForVertical(c: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount

        for (i in 0 until if (end) childCount else childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = (child.bottom + params.bottomMargin + child.translationY.roundToInt())
            val bottom: Int = top + space
            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
        }

        if (head) {
            c.drawRect(left.toFloat(), 0f, right.toFloat(), space.toFloat(), mPaint)
        }
    }
}