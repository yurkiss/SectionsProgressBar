package org.yurkiss.sectionsprogressbar;

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

public class ProgressDrawable extends Drawable /*implements Animatable */ {

    private static final float mStrokeWidth     = 5;
    public static final  int   CHECK_COLOR      = (0xFF4081 + 0xFF000000);
    public static final  int   UNCHECK_COLOR    = 0xffb5b5b5;
    public static final  int   INNER_BACK_COLOR = 0xffe2e2e2;
    public static final  int   WHITE_COLOR      = 0xffffffff;
    private static final long  FRAME_DURATION   = 1000 / 60;
//    private final static float OFFSET_PER_FRAME = 0.01f;
//    private static final long ANIMATION_DURATION = 1500;

    private final Paint mBackPaint;
    private final Paint mFrontPaint;
    private final Paint mTextPaint;
    private final RectF rectF;
    private       Rect  mBounds;
    private       Rect  mFillingBounds;

    private int mPaintingDefaultPadding = 0;
    private List<SectionsProgressBar.Section> sections;

    private float mBarHeight = 0.35f;

    private boolean mRunning;
    private long    mStartTime;
    private long    duration;


    private int mBarColor  = CHECK_COLOR;
    private int mBackgroundBarColor = UNCHECK_COLOR;
    private int mTextColor = 0xffffffff;

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

        // draw "empty" progress background
        drawProgressBar(canvas, mBackPaint, mBounds);

        // make filling progress smaller
        mFillingBounds.set(mBounds);
        int d = 5;
        mFillingBounds.left += d;
        mFillingBounds.top += d;
        mFillingBounds.right -= d;
        mFillingBounds.bottom -= d;

        // calculate progress for drawing filling progress
        rectF.set(mFillingBounds);
        float ww = 0;
        for (int i = 0; i < sections.size(); i++) {
            ww += calculateProgress(i, mFillingBounds);
        }
        rectF.right = ww;

        canvas.save();
        canvas.clipRect(rectF);
        drawProgressBar(canvas, mFrontPaint, mFillingBounds);
        canvas.restore();

    }

    private void drawProgressBar(Canvas canvas, Paint paint, Rect bounds) {

        int w = bounds.width();
        int h = bounds.height();

        // draw bar
        float rx = 20;
        float ry = 20;

        float barH = bounds.height() * mBarHeight;
        RectF rect = new RectF(bounds.left, bounds.centerY() - barH / 2, bounds.right, bounds.centerY() + barH / 2);
        canvas.drawRoundRect(rect, rx, ry, paint);

        // draw circles
        float radius = h / 2f;
        int c = sections.size() - 1;
        float dx = (float) (w - 2 * radius) / (float) c;

        float textSize = radius * 0.75f;
        mTextPaint.setTextSize(textSize);

        for (int i = 0; i < sections.size(); i++) {
            float cx = bounds.left + radius + dx * i;
            float cy = bounds.exactCenterY();
            canvas.drawCircle(cx, cy, radius, paint);
            canvas.drawText(String.valueOf(i + 1), cx, cy + textSize / 4f, mTextPaint);
        }
    }

    float calculateProgress(int secIndex, Rect bounds) {

        int w = bounds.width();
        int h = bounds.height();

        SectionsProgressBar.Section section = sections.get(secIndex);

        float coef = (float) section.getProgress() / (float) section.getMax();

        float radius = h / 2f;
        int c = sections.size() - 1;
        float dx = (float) (w - 2 * radius) / (float) c;

        float cx = bounds.left + dx * secIndex;
        float cx2;
        if (secIndex < sections.size() - 1) {
            cx2 = bounds.left + dx * (secIndex + 1);
        } else {
            cx2 = bounds.right;
        }

        return (cx2 - cx) * coef;

    }


//    @Override
//    public void start() {
//        if (!isRunning()) {
//            mRunning = true;
//
//            mStartTime = AnimationUtils.currentAnimationTimeMillis();
//
//            scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION);
//            invalidateSelf();
//        }
//    }
//
//    @Override
//    public void stop() {
//        if (isRunning()) {
//            unscheduleSelf(mUpdater);
//            mRunning = false;
//        }
//    }
//
//    @Override
//    public void scheduleSelf(Runnable what, long when) {
//        mRunning = true;
//        super.scheduleSelf(what, when);
//    }

    //    @Override
    public boolean isRunning() {
        return mRunning;
    }

//    public int getMax() {
//        return max;
//    }
//
//    public void setMax(int max) {
//        this.max = max;
//    }


//    private float getOffsetPerFrame() {
//        return  1f / (float)(mBounds.right - mBounds.left);
//    }

//    private final Runnable mUpdater = new Runnable() {
//        @Override
//        public void run() {
//            long now = AnimationUtils.currentAnimationTimeMillis();

//            getOffsetPerFrame();
//            long duration = now - mStartTime;
//            if (duration >= ANIMATION_DURATION) {
//                mStartColor = mEndColor;
//                mEndColor = randomColor();
//                mStartTime = now;
//                mCurrentColor = mStartColor;
//            } else {
//                float fraction = duration / (float) ANIMATION_DURATION;
//                //@formatter:off
//                mCurrentColor = Color.rgb(
//                        evaluate(fraction, Color.red(mStartColor), Color.red(mEndColor)),     // red
//                        evaluate(fraction, Color.green(mStartColor), Color.green(mEndColor)), // green
//                        evaluate(fraction, Color.blue(mStartColor), Color.blue(mEndColor)));  // blue
//                //@formatter:on
//            }
//            if (isRunning()) {
//                scheduleSelf(mUpdater, AnimationUtils.currentAnimationTimeMillis() + FRAME_DURATION);
//            }
//            invalidateSelf();
//        }
//    };

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



