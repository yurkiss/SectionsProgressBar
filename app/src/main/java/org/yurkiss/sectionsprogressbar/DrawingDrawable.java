package org.yurkiss.sectionsprogressbar;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;


/**
 * Created by yurkiss on 7/24/16.
 */

public class DrawingDrawable extends Drawable {

    private static final float mStrokeWidth     = 5;
    public static final  int   CHECK_COLOR      = (0xFF4081 + 0xFF000000);
    public static final  int   UNCHECK_COLOR    = 0xffb5b5b5;
    public static final  int   INNER_BACK_COLOR = 0xffe2e2e2;
    public static final  int   WHITE_COLOR      = 0xffffffff;

    private Rect mBounds;
    private float mBarHeight  = 0.35f;
    private int   pointsCount = 5;

    private final Paint mBackPaint;
    private final Paint mTextPaint;

    private int mBarColor               = UNCHECK_COLOR;
    private int mTextColor              = 0xffffffff;
    private int mPaintingDefaultPadding = 0;

    public DrawingDrawable() {
        mBackPaint = new Paint();
        mBackPaint.setColor(WHITE_COLOR);
        mBackPaint.setAntiAlias(true);
        mBackPaint.setStyle(Paint.Style.FILL);
        mBackPaint.setStrokeWidth(mStrokeWidth);
//        mBackPaint.setShadowLayer(10.0f, 0.0f, 0.0f, Color.GRAY);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mBounds = new Rect();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mBounds.set(bounds);
        mBounds.top += mPaintingDefaultPadding;
        mBounds.left += mPaintingDefaultPadding;
        mBounds.bottom -= mPaintingDefaultPadding;
        mBounds.right -= mPaintingDefaultPadding;
//        System.out.println(bounds);
    }

    @Override
    public void draw(Canvas canvas) {

        int w = mBounds.width();
        int h = mBounds.height();

        float rx = 20;
        float ry = 20;

        float barH = mBounds.height() * mBarHeight;

        RectF rect = new RectF(mBounds.left, mBounds.centerY() - barH / 2, mBounds.right, mBounds.centerY() + barH / 2);

        mBackPaint.setColor(mBarColor);
        canvas.drawRoundRect(rect, rx, ry, mBackPaint);

        float radius = h / 2f;
        int c = pointsCount - 1;
        float dx = (float) (w - 2 * radius) / (float) c;

        float textSize = radius * 0.75f;
        mTextPaint.setTextSize(textSize);

        for (int i = 0; i < pointsCount; i++) {
            float cx = mBounds.left + radius + dx * i;
            float cy = mBounds.exactCenterY();
            canvas.drawCircle(cx, cy, radius, mBackPaint);
            canvas.drawText(String.valueOf(i + 1), cx, cy + textSize / 4f, mTextPaint);
        }

    }

    @Override
    public void setAlpha(int alpha) {
        mBackPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mBackPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public float getBarHeight() {
        return mBarHeight;
    }

    public void setBarHeight(float percent) {
        this.mBarHeight = percent;
    }

    public int getPointsCount() {
        return pointsCount;
    }

    public void setPointsCount(int pointsCount) {
        this.pointsCount = pointsCount;
    }

    public int getBarColor() {
        return mBarColor;
    }

    public void setBarColor(int mBarColor) {
        this.mBarColor = mBarColor;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }
}
