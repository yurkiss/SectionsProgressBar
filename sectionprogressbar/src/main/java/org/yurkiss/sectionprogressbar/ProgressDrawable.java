package org.yurkiss.sectionprogressbar;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import java.util.List;


/**
 * Created by yurkiss on 7/24/16.
 */

public class ProgressDrawable extends Drawable {

    private static final float mStrokeWidth     = 5;
    public static final  int   CHECK_COLOR      = (0xFF4081 + 0xFF000000);
    public static final  int   UNCHECK_COLOR    = 0xffb5b5b5;
    public static final  int   INNER_BACK_COLOR = 0xffe2e2e2;
    public static final  int   WHITE_COLOR      = 0xffffffff;

    private final Paint mBackPaint;
    private final Paint mFrontPaint;
    private final Paint mTextPaint;
    private final RectF rectF;
    private final Rect  mBounds;
    private final Rect  mFillingBounds;

    private List<SectionsProgressBar.Section> sections;

    private int mPaintingDefaultPadding = 0;
    private float mRelativeBarHeight = 0.35f;
    private int mBarColor           = CHECK_COLOR;
    private int mBackgroundBarColor = UNCHECK_COLOR;
    private int mTextColor          = 0xffffffff;

    public ProgressDrawable() {
        mBackPaint = new Paint();
        mBackPaint.setColor(mBackgroundBarColor);
        mBackPaint.setAntiAlias(true);
        mBackPaint.setStyle(Paint.Style.FILL);
        mBackPaint.setStrokeWidth(mStrokeWidth);
//        mBackPaint.setShadowLayer(10.0f, 0.0f, 0.0f, Color.GRAY);

        mFrontPaint = new Paint();
        mFrontPaint.setColor(mBarColor);
        mFrontPaint.setAntiAlias(true);
        mFrontPaint.setStyle(Paint.Style.FILL);
        mFrontPaint.setStrokeWidth(mStrokeWidth);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mBounds = new Rect();
        mFillingBounds = new Rect();
        rectF = new RectF();

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

        // make filling progress smaller
        mFillingBounds.set(mBounds);

        // Coefficient of circles and bar reduction
        // to make filling progress smaller
        float reductCoef = 1f;

        // draw "empty" progress background
        drawProgressBar(canvas, mBackPaint, mBounds, reductCoef);

        // calculate progress for drawing filling progress
        reductCoef = 0.8f;
        rectF.set(mFillingBounds);
        float ww = 0;
        for (int i = 0; i < sections.size(); i++) {
            ww += calculateProgress(i, mFillingBounds, reductCoef);
        }

        rectF.right = ww;
//        if (ww > 0)
//            System.out.println(rectF);

        canvas.save();
        canvas.clipRect(rectF);
        drawProgressBar(canvas, mFrontPaint, mFillingBounds, reductCoef);
        canvas.restore();

    }

    private void drawProgressBar(Canvas canvas, Paint paint, Rect bounds, float p) {

        int w = bounds.width();
        int h = bounds.height();

        // Calculate bar reduction coefficient.
        // Increase reduction by taking fraction of reduction coefficient
        float pBar = (p < 1) ? p * 0.7f : p;

        // Circles radius
        float radius = h / 2f;

        // Draw bar
        float rx = 20;
        float ry = 20;

        float barH = bounds.height() * mRelativeBarHeight * pBar;

        float fillingPadding = radius - radius * p;
        float barLeft = bounds.left + fillingPadding;
        float barRight = bounds.right - fillingPadding;

        RectF rect = new RectF(barLeft, bounds.centerY() - barH / 2, barRight, bounds.centerY() + barH / 2);
        canvas.drawRoundRect(rect, rx, ry, paint);

        // draw circles
        int c = sections.size() - 1;
        float dx = (w - 2 * radius) / (float) c;

        float textSize = radius * 0.75f;
        mTextPaint.setTextSize(textSize);

        for (int i = 0; i < sections.size(); i++) {
            float cx = bounds.left + radius + dx * i;
            float cy = bounds.exactCenterY();
            canvas.drawCircle(cx, cy, radius * p, paint);
            canvas.drawText(String.valueOf(i + 1), cx, cy + textSize / 4f, mTextPaint);
        }
    }

    float calculateProgress(int secIndex, Rect bounds, float rCoef) {

        int h = bounds.height();
        float radius = h / 2f;

        float w = bounds.width();

        SectionsProgressBar.Section section = sections.get(secIndex);

        float coefProgress = (float) section.getProgress() / (float) section.getMax();

        int c = sections.size() - 1;
        float dx = (w - 2 * radius) / (float) c;

        float left = bounds.left;
        float lx1 = left + dx * secIndex;
        float lx2;
        if (secIndex < sections.size() - 1) {
            lx2 = left + dx * (secIndex + 1);
        } else {
            lx2 = bounds.right;
        }

//        if (coefProgress == 1)
//        System.out.println(String.format("h:%s, w:%s, (%s - %s) = %s, dx: %s radius: %s", h, w, lx2, lx1, lx2 - lx1, dx, radius));

        return (lx2 - lx1) * coefProgress;

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

    public float getRelativeBarHeight() {
        return mRelativeBarHeight;
    }

    public void setRelativeBarHeight(float percent) {
        this.mRelativeBarHeight = percent;
    }


    public int getBarColor() {
        return mBarColor;
    }

    public void setBarColor(int mBarColor) {
        this.mBarColor = mBarColor;
        mFrontPaint.setColor(mBarColor);
    }

    public int getBackgroundBarColor() {
        return mBackgroundBarColor;
    }

    public void setBackgroundBarColor(int mBarColor) {
        this.mBackgroundBarColor = mBarColor;
        mBackPaint.setColor(mBackgroundBarColor);
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    public List<SectionsProgressBar.Section> getSections() {
        return sections;
    }

    public void setSections(List<SectionsProgressBar.Section> sections) {
        this.sections = sections;
    }
}



