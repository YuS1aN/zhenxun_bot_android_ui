package me.kbai.zhenxunui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import me.kbai.zhenxunui.R;

public class ExpandLayout extends LinearLayout {

    private boolean isExpand;
    private long animationDuration;
    private boolean lock;

    private int maxHeight;

    public ExpandLayout(Context context) {
        this(context, null);
    }

    public ExpandLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        readAttr(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isExpand) setViewHeight(this, 0);
    }

    public static void setViewHeight(View view, int height) {
        final ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = height;
        view.requestLayout();
    }

    public boolean isExpand() {
        return isExpand;
    }

    /**
     * 折叠view
     */
    public void collapse(boolean animator) {
        isExpand = false;
        if (animator) {
            animateToggle(animationDuration);
        } else {
            setViewHeight(this, 0);
        }
    }

    /**
     * 展开view
     */
    public void expand() {
        isExpand = true;
        animateToggle(animationDuration);
    }

    public void toggleExpand() {
        if (lock) {
            return;
        }
        if (isExpand) {
            collapse(true);
        } else {
            expand();
        }
    }

    private void readAttr(Context context, AttributeSet attrs) {
        TypedArray array = null;
        try {
            //noinspection resource
            array = context.obtainStyledAttributes(attrs, R.styleable.ExpandLayout);
            isExpand = array.getBoolean(R.styleable.ExpandLayout_expanded, true);
            maxHeight = array.getDimensionPixelSize(R.styleable.ExpandLayout_maxHeight, 0);
            animationDuration = array.getInteger(R.styleable.ExpandLayout_animationDuration, 300);
        } catch (Throwable e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        } finally {
            if (array != null) array.recycle();
        }
    }

    /**
     * 切换动画实现
     */
    private void animateToggle(long animationDuration) {
        //展开时重新计算高度
        if (isExpand) {
            measure(
                    MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            );
        }
        int viewHeight = maxHeight > 0 ? Math.min(getMeasuredHeight(), maxHeight) : getMeasuredHeight();

        ValueAnimator heightAnimation = isExpand ?
                ValueAnimator.ofFloat(0f, viewHeight) : ValueAnimator.ofFloat(viewHeight, 0f);
        heightAnimation.setDuration(animationDuration / 2);
        heightAnimation.setStartDelay(animationDuration / 2);

        heightAnimation.addUpdateListener(animation -> {
            int value = (int) (float) animation.getAnimatedValue();
            setViewHeight(this, value);
            if (value == viewHeight || value == 0) {
                lock = false;
            }
        });

        heightAnimation.start();
        lock = true;
    }
}
