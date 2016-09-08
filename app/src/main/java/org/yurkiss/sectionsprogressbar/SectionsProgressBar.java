package org.yurkiss.sectionsprogressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yurkiss on 7/25/16.
 */

public class SectionsProgressBar extends ImageView {

    private DrawingDrawable backgroundDrawable;
    private DrawingDrawable fillingDrawable;

    private List<Section> sections;

    private int   progress;
    private RectF rectF;
    private Rect  rect;

    public SectionsProgressBar(Context context) {
        super(context);
        init();
    }

    public SectionsProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        sections = new ArrayList<>();

        backgroundDrawable = new DrawingDrawable();

        fillingDrawable = new DrawingDrawable();
        fillingDrawable.setBarColor(0xFFFF4081);

        setImageDrawable(backgroundDrawable);
        rectF = new RectF();
        rect = new Rect();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        backgroundDrawable.copyBounds(rect);
        int d = 5;
        rect.left += d;
        rect.top += d;
        rect.right -= d;
        rect.bottom -= d;

        fillingDrawable.setBounds(rect);
        float per = (float) progress / (float) getMax();

        rectF.set(rect);
        rectF.right *= per;

        // Set padding
        canvas.translate(getPaddingLeft(), getPaddingTop());
        canvas.clipRect(rectF);
        fillingDrawable.draw(canvas);
        canvas.restore();
    }


    public int getMax() {
        int max = 0;
        for (Section section : sections) {
            max += section.getMax();
        }
        return max;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(getMax(), progress));
        postInvalidate();
    }

    public void setProgressBackgroundColor(int color) {
        backgroundDrawable.setBarColor(color);
    }

    public int getProgressBackgroundColor() {
        return backgroundDrawable.getBarColor();
    }

    public void setProgressBarColor(int color) {
        fillingDrawable.setBarColor(color);
    }

    public int getProgressBarColor() {
        return fillingDrawable.getBarColor();
    }

    public void addSection(Section section) {
        sections.add(section);
        section.attachProgressBar(this);
        backgroundDrawable.setPointsCount(sections.size());
        fillingDrawable.setPointsCount(sections.size());
        postInvalidate();
    }

    public void removeSection(int i) {
        if (i < sections.size()) {
            sections.remove(i);
            backgroundDrawable.setPointsCount(sections.size());
            fillingDrawable.setPointsCount(sections.size());
            postInvalidate();
        }
    }

    public static class Section {

        private SectionsProgressBar bar;

        private int max;
        private int progress;

        public Section(int max) {
            setMax(max);
        }

        public Section(int max, SectionsProgressBar bar) {
            setMax(max);
            bar.addSection(this);
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
            if (max < progress) {
                bar.setProgress(bar.getProgress() - (progress - max));
                progress = max;
            }
        }

        void attachProgressBar(SectionsProgressBar bar) {
            this.bar = bar;
        }

        public void incrementProgress() {
            incrementProgress(1);
        }

        public void incrementProgress(int i) {

            if (bar == null) {
                throw new IllegalStateException("Section has to be attached to progress bar.");
            }
            if (progress + i < max) {
                progress += i;
                bar.setProgress(bar.getProgress() + i);
            }
        }

        public void invalidateProgress(){
            progress = 0;
        }

    }


}
