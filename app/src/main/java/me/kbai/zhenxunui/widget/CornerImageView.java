package me.kbai.zhenxunui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import me.kbai.zhenxunui.R;

/**
 * @author sean 2020/8/20
 */
public class CornerImageView extends AppCompatImageView {

    private float mTopLeftRadius;
    private float mTopRightRadius;
    private float mBottomLeftRadius;
    private float mBottomRightRadius;
    private float mAspectRatio;
    private float mRotationDegrees;

    /**
     * 绘制方式    0: clip    1: Shader    2: Xfermode
     */
    private int mDrawMode = 2;

    private final Path mPath = new Path();
    private Path mOutPath;

    private Bitmap mBitmap;
    private Canvas mCanvas;

    private Paint mPaint;

    public CornerImageView(Context context) {
        this(context, null);
    }

    public CornerImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CornerImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CornerImageView, 0, 0);
        mTopLeftRadius = typedArray.getDimensionPixelSize(R.styleable.CornerImageView_topLeftRadius, 0);
        mTopRightRadius = typedArray.getDimensionPixelSize(R.styleable.CornerImageView_topRightRadius, 0);
        mBottomLeftRadius = typedArray.getDimensionPixelSize(R.styleable.CornerImageView_bottomLeftRadius, 0);
        mBottomRightRadius = typedArray.getDimensionPixelSize(R.styleable.CornerImageView_bottomRightRadius, 0);
        int radius = typedArray.getDimensionPixelSize(R.styleable.CornerImageView_cornerRadius, 0);
        if (mTopLeftRadius == 0) {
            mTopLeftRadius = radius;
        }
        if (mTopRightRadius == 0) {
            mTopRightRadius = radius;
        }
        if (mBottomLeftRadius == 0) {
            mBottomLeftRadius = radius;
        }
        if (mBottomRightRadius == 0) {
            mBottomRightRadius = radius;
        }
        mAspectRatio = typedArray.getFloat(R.styleable.CornerImageView_aspectRatio, 0);
        mDrawMode = typedArray.getInt(R.styleable.CornerImageView_drawMode, mDrawMode);
        mRotationDegrees = typedArray.getFloat(R.styleable.CornerImageView_rotationDegrees, 0);
        typedArray.recycle();
    }

    public void setCorners(float topLeft, float topRight, float bottomLeft, float bottomRight) {
        mTopLeftRadius = topLeft;
        mTopRightRadius = topRight;
        mBottomLeftRadius = bottomLeft;
        mBottomRightRadius = bottomRight;
    }

    public void setCorners(float radius) {
        setCorners(radius, radius, radius, radius);
    }

    public void setCorners(int radius) {
        setCorners((float) radius);
    }

    public void setAspectRatio(float aspectRatio) {
        mAspectRatio = aspectRatio;
    }

    public void setRotationDegrees(float degrees) {
        mRotationDegrees = degrees;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        // 如果设置了宽高比且固定了一边的尺寸
        if (mAspectRatio > 0) {
            if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
                int widthSize = MeasureSpec.getSize(widthMeasureSpec);
                setMeasuredDimension(widthSize, Math.round(widthSize / mAspectRatio));
                return;
            } else if (widthMode != MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
                int heightSize = MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(Math.round(heightSize * mAspectRatio), heightSize);
                return;
                // 处理一些在列表控件(RecyclerView ListView)中的情况，没有考虑到所有情况，后续碰到再添加
                // VERTICAL
            } else if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.UNSPECIFIED) {
                ViewGroup parent = ((ViewGroup) getParent());
                // item宽/高:MATCH_PARENT/固定值
                int heightSize = parent.getLayoutParams().height;
                if (heightSize > 0) {
                    setMeasuredDimension(Math.round(heightSize * mAspectRatio), heightSize);
                    return;
                }
                // item宽/高:MATCH_PARENT/WRAP_PARENT
                int widthSize = MeasureSpec.getSize(widthMeasureSpec);
                if (widthSize > 0) {
                    setMeasuredDimension(widthSize, Math.round(widthSize / mAspectRatio));
                    return;
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final int widthI = right - left;
        final int heightI = bottom - top;
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        final int paddingRight = getPaddingRight();
        final int paddingBottom = getPaddingBottom();
        final float widthF = (float) widthI;
        final float heightF = (float) heightI;
        final float widthNoPadding = widthF - paddingLeft - paddingRight;
        final float heightNoPadding = heightF - paddingTop - paddingBottom;
        mPath.reset();
        if (max(mTopLeftRadius, mTopRightRadius, mBottomLeftRadius, mBottomRightRadius) > Math.min(widthF - paddingLeft - paddingRight, heightF - paddingTop - paddingBottom)) {
            mPath.addCircle(paddingLeft + widthNoPadding / 2, paddingTop + heightNoPadding / 2, Math.min(widthNoPadding, heightNoPadding) / 2, Path.Direction.CW);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mPath.addRoundRect(paddingLeft, paddingTop, widthF - paddingRight, heightF - paddingBottom, new float[]{
                        mTopLeftRadius, mTopLeftRadius,
                        mTopRightRadius, mTopRightRadius,
                        mBottomRightRadius, mBottomRightRadius,
                        mBottomLeftRadius, mBottomLeftRadius
                }, Path.Direction.CW);
            } else {
                mPath.moveTo(mTopLeftRadius + paddingLeft, paddingTop);
                mPath.lineTo(widthF - mTopRightRadius - paddingRight, paddingTop);
                mPath.quadTo(widthF - paddingRight, paddingTop, widthF - paddingRight, mTopRightRadius + paddingTop);
                mPath.lineTo(widthF - paddingRight, heightF - mBottomRightRadius - paddingBottom);
                mPath.quadTo(widthF - paddingRight, heightF - paddingBottom, widthF - mBottomRightRadius - paddingRight, heightF - paddingBottom);
                mPath.lineTo(mBottomLeftRadius + paddingLeft, heightF - paddingBottom);
                mPath.quadTo(paddingLeft, heightF - paddingBottom, paddingLeft, heightF - mBottomLeftRadius - paddingBottom);
                mPath.lineTo(paddingLeft, mTopLeftRadius + paddingTop);
                mPath.quadTo(paddingLeft, paddingTop, mTopLeftRadius + paddingLeft, paddingTop);
            }
        }

        if (mDrawMode == 1) {
            if (mPaint == null) {
                mPaint = new Paint();
                mPaint.setAntiAlias(true);
                mPaint.setColor(Color.WHITE);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            }
            createShaderBitmap(widthI, heightI);
        } else if (mDrawMode == 2) {
            if (mPaint == null) {
                mPaint = new Paint();
                mPaint.setAntiAlias(true);
                mPaint.setStyle(Paint.Style.FILL);
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
                    mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                } else {
                    mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
                }
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                if (mOutPath == null) {
                    mOutPath = new Path();
                }
                mOutPath.reset();
                mOutPath.addRect(-1, -1, widthF + 1, heightF + 1, Path.Direction.CW);
                mOutPath.op(mPath, Path.Op.DIFFERENCE);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final float degrees = mRotationDegrees;
        int rotateSaveCount = -1;
        if (Math.abs(degrees) > 1e-6) {
            float cw = getWidth() - getPaddingLeft() - getPaddingRight();
            float ch = getHeight() - getPaddingTop() - getPaddingBottom();
            rotateSaveCount = canvas.save();
            canvas.rotate(degrees, getPaddingLeft() + cw / 2, getPaddingTop() + ch / 2);
        }
        if (mDrawMode == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.saveLayer(0, 0, getWidth(), getHeight(), null);
            } else {
                canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
            }
            canvas.clipPath(mPath);
            super.onDraw(canvas);
            canvas.restore();
        } else if (mDrawMode == 1) {
            if (mCanvas == null || mBitmap.isRecycled()) {
                if (!createShaderBitmap(getWidth(), getHeight())) {
                    super.onDraw(canvas);
                    return;
                }
            }
            super.onDraw(mCanvas);
            mPaint.setShader(new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            canvas.drawPath(mPath, mPaint);
        } else if (mDrawMode == 2) {
            canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
            super.onDraw(canvas);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
                canvas.drawPath(mPath, mPaint);
            } else {
                canvas.drawPath(mOutPath, mPaint);
            }
            canvas.restore();
        } else {
            super.onDraw(canvas);
        }
        if (rotateSaveCount >= 0) {
            canvas.restoreToCount(rotateSaveCount);
        }
    }

    private boolean createShaderBitmap(int width, int height) {
        if (width <= 0 && height <= 0) {
            return false;
        }
        if (mBitmap == null || mBitmap.isRecycled() || mBitmap.getWidth() != width || mBitmap.getHeight() != height) {
            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            return true;
        }
        return false;
    }

    private float max(float... num) {
        float max = 0;
        for (float f : num) {
            if (f > max) max = f;
        }
        return max;
    }
}
