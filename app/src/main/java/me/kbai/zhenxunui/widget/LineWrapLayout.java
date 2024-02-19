package me.kbai.zhenxunui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

import me.kbai.zhenxunui.R;

/**
 * @author sean 2021/5/31
 */
public class LineWrapLayout extends LinearLayout {

    /**
     * 最大单行 child 个数
     */
    private int mMaxLineItemCount;

    private boolean mEnforceLineItemCount;

    /**
     * 横向布局方式
     */
    private int mHorizontalDistribution;

    private Queue<View> mLineLayoutQueue;

    private boolean mSpreadIncludeSides = true;

    private boolean mSpreadAsMaximum = true;

    private boolean mTableEnforceChildrenWidth = true;

    public LineWrapLayout(Context context) {
        this(context, null);
    }

    public LineWrapLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineWrapLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LineWrapLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        setOrientation(VERTICAL);
        //noinspection resource
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LineWrapLayout);
        mMaxLineItemCount = typedArray.getInt(R.styleable.LineWrapLayout_maxLineItemCount, mMaxLineItemCount);
        mEnforceLineItemCount = typedArray.getBoolean(R.styleable.LineWrapLayout_enforceLineItemCount, mEnforceLineItemCount);
        mHorizontalDistribution = typedArray.getInt(R.styleable.LineWrapLayout_horizontalDistribution, mHorizontalDistribution);
        mSpreadIncludeSides = typedArray.getBoolean(R.styleable.LineWrapLayout_spreadIncludeSides, mSpreadIncludeSides);
        mSpreadAsMaximum = typedArray.getBoolean(R.styleable.LineWrapLayout_spreadAsMaximum, mSpreadAsMaximum);
        mTableEnforceChildrenWidth = typedArray.getBoolean(R.styleable.LineWrapLayout_tableEnforceChildrenWidth, mTableEnforceChildrenWidth);
        typedArray.recycle();
    }

    @Override
    protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
        if (mHorizontalDistribution == 2 && mTableEnforceChildrenWidth && mMaxLineItemCount > 0) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int stableWidth = (widthSize - getPaddingLeft() - getPaddingRight()) / mMaxLineItemCount;
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(stableWidth, MeasureSpec.EXACTLY);

            final int size = getChildCount();
            for (int i = 0; i < size; ++i) {
                final View child = getChildAt(i);
                LayoutParams params = (LayoutParams) child.getLayoutParams();
                params.width = stableWidth;
                if (child.getVisibility() != GONE) {
                    measureChild(child, widthMeasureSpec, heightMeasureSpec);
                }
            }
            return;
        }
        super.measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        final int paddingHorizontal = getPaddingLeft() + getPaddingRight();
        // 单个child允许的最大宽度
        int itemMaxSpace = widthSize - paddingHorizontal;
        // 最大行宽
        int maxWidth = 0;
        // 累加高度
        int totalHeight = getPaddingTop() + getPaddingBottom();
        // 当前行宽
        int lineWidth = paddingHorizontal;
        //当前行高
        int lineHeight = 0;
        // 当前行child个数
        int lineCount = 0;

        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
            int itemXSpace = child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            int itemYSpace = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;

            if (itemXSpace > itemMaxSpace) {
                child.measure(MeasureSpec.makeMeasureSpec(itemMaxSpace - params.leftMargin - params.rightMargin, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(1 << 30 - 1, MeasureSpec.AT_MOST));
                itemXSpace = itemMaxSpace;
                itemYSpace = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;
            }

            boolean lineMax = mMaxLineItemCount > 0 && i < count - 1 && ++lineCount > mMaxLineItemCount;

            if (mHorizontalDistribution == 2) {
                if (mMaxLineItemCount > 0 && lineMax) {
                    maxWidth = Math.max(maxWidth, lineWidth);
                    lineWidth = paddingHorizontal + itemXSpace;
                    totalHeight += lineHeight;
                    lineHeight = itemYSpace;
                    lineCount = 0;
                    continue;
                }
            } else {
                boolean skip = mEnforceLineItemCount && lineCount < mMaxLineItemCount;
                if (!skip && lineWidth + itemXSpace > widthSize || lineMax) {
                    maxWidth = Math.max(maxWidth, lineWidth);
                    lineWidth = paddingHorizontal + itemXSpace;
                    totalHeight += lineHeight;
                    if (i == count - 1) {
                        totalHeight += itemYSpace;
                    } else {
                        lineHeight = itemYSpace;
                        lineCount = 0;
                    }
                    continue;
                }
            }

            lineWidth += itemXSpace;
            lineHeight = Math.max(lineHeight, itemYSpace);
            if (i == count - 1) {
                totalHeight += lineHeight;
            }
        }

        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : maxWidth,
                heightMode == MeasureSpec.EXACTLY ? heightSize : totalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        switch (mHorizontalDistribution) {
            case 1 -> spreadLayout(l, r);
            case 2 -> tableLayout(l, r);
            default -> simpleLayout(l, r);
        }
    }

    private void simpleLayout(int l, int r) {
        int count = getChildCount();
        int preLeft = getPaddingLeft();
        int preTop = getPaddingTop();
        int maxLineHeight = 0;
        int lineCount = 0;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            int height = child.getMeasuredHeight();
            int width = child.getMeasuredWidth();
            boolean skip = mEnforceLineItemCount && lineCount < mMaxLineItemCount;
            if (!skip) {
                if ((mMaxLineItemCount > 0 && lineCount >= mMaxLineItemCount) ||
                        preLeft + params.leftMargin + width + params.rightMargin + getPaddingRight() > (r - l)) {
                    //需要换行
                    preLeft = getPaddingLeft();
                    preTop += maxLineHeight;
                    maxLineHeight = 0;
                    lineCount = 0;
                }
            }
            child.layout(preLeft + params.leftMargin, preTop + params.topMargin, preLeft + params.leftMargin + width, preTop + height + params.topMargin);
            maxLineHeight = Math.max(maxLineHeight, params.topMargin + height + params.bottomMargin);
            preLeft += params.leftMargin + width + params.rightMargin;
            lineCount++;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LinearLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp instanceof LinearLayout.LayoutParams) {
            return new LayoutParams((LinearLayout.LayoutParams) lp);
        } else if (lp instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) lp);
        }
        return new LayoutParams(lp);
    }

    @Override
    protected LinearLayout.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    private void spreadLayout(int l, int r) {
        int count = getChildCount();

        int preLeft = getPaddingLeft();
        int preTop = getPaddingTop();
        int maxLineHeight = 0;
        int lineCount = 0;

        if (mLineLayoutQueue == null) {
            mLineLayoutQueue = new LinkedList<>();
        }

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            int height = child.getMeasuredHeight();
            int width = child.getMeasuredWidth();
            boolean skip = mEnforceLineItemCount && lineCount < mMaxLineItemCount;
            if (!skip) {
                if ((mMaxLineItemCount > 0 && lineCount >= mMaxLineItemCount) ||
                        preLeft + params.leftMargin + width + params.rightMargin + getPaddingRight() > (r - l)) {
                    //需要换行
                    spreadLineLayout(r - l, preLeft + getPaddingRight());
                    preLeft = getPaddingLeft();
                    preTop += maxLineHeight;
                    maxLineHeight = 0;
                    lineCount = 0;
                }
            }
            params.layoutRect.set(preLeft + params.leftMargin, preTop + params.topMargin, preLeft + params.leftMargin + width, preTop + height + params.topMargin);
            mLineLayoutQueue.offer(child);
            maxLineHeight = Math.max(maxLineHeight, params.topMargin + height + params.bottomMargin);
            preLeft += params.leftMargin + width + params.rightMargin;
            lineCount++;
        }
        spreadLineLayout(r - l, preLeft + getPaddingRight());
    }

    private void spreadLineLayout(int width, int usedWidth) {
        int size = mLineLayoutQueue.size();
        int diff = 0;
        int diffMeasureSize = mSpreadAsMaximum ? mMaxLineItemCount : size;
        int remainingSpace;
        if (mSpreadAsMaximum && size > 0) {
            remainingSpace = width - usedWidth / size * mMaxLineItemCount;
        } else {
            remainingSpace = width - usedWidth;
        }
        for (int i = 1; i <= size; i++) {
            View child = mLineLayoutQueue.poll();
            Rect rect = ((LayoutParams) Objects.requireNonNull(child).getLayoutParams()).layoutRect;

            if (mSpreadIncludeSides) {
                diff += remainingSpace / (diffMeasureSize + 1);
            } else {
                diff += (i == 1) ? 0 : (remainingSpace / (diffMeasureSize - 1));
            }
            child.layout(rect.left + diff, rect.top, rect.right + diff, rect.bottom);
        }
    }

    private void tableLayout(int l, int r) {
        final int count = getChildCount();
        int preTop = getPaddingTop();
        int maxLineHeight = 0;
        int lineCount = 0;
        final int availableWidth = (r - l - getPaddingLeft() - getPaddingRight());

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            int height = child.getMeasuredHeight();
            int width = child.getMeasuredWidth();

            if (mMaxLineItemCount > 0 && lineCount >= mMaxLineItemCount) {
                preTop += maxLineHeight;
                maxLineHeight = 0;
                lineCount = 0;
            }
            int sl = getPaddingLeft() + availableWidth / mMaxLineItemCount * lineCount;
            child.layout(sl, preTop + params.topMargin, sl + width, preTop + height + params.topMargin);
            maxLineHeight = Math.max(maxLineHeight, params.topMargin + height + params.bottomMargin);
            lineCount++;
        }
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {
        private final Rect layoutRect = new Rect();

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(LinearLayout.LayoutParams source) {
            super(source);
        }
    }
}

