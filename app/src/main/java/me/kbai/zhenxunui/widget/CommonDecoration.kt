package me.kbai.zhenxunui.widget

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

/**
 * @author sean
 */
class CommonDecoration @JvmOverloads constructor(
    private val space: Int,
    private val type: Int = 0,
    private val head: Boolean = true,
    private val otherSide: Boolean = true,
    private val endDivider: Boolean = false,
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
        val position = parent.getChildAdapterPosition(view)
        val manager = parent.layoutManager
        if (manager is GridLayoutManager) {
            val params = view.layoutParams as GridLayoutManager.LayoutParams
            val spanCount = manager.spanCount
            val spanIndex = params.spanIndex
            val halfSpace = (space / 2f).roundToInt()
            if (head && position < spanCount) {
                val group = manager.spanSizeLookup.getSpanGroupIndex(position, spanCount)
                if (group == 0) outRect.top = space
            }
            outRect.bottom = space
            if (otherSide) {
                outRect.left = if (spanIndex == 0) space else halfSpace
                outRect.right = if (spanIndex + params.spanSize == spanCount) space else halfSpace
            }
        } else {
            if (type == VERTICAL) {
                outRect.bottom = space
                if (otherSide) {
                    outRect.left = space
                    outRect.right = space
                }
            } else {
                outRect.right = space
                if (otherSide) {
                    outRect.top = space
                    outRect.bottom = space
                }
            }
            if (head && position == 0) {
                if (type == VERTICAL) {
                    outRect.top = space
                } else {
                    outRect.left = space
                }
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

        for (i in 0 until if (endDivider) childCount else childCount - 1) {
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