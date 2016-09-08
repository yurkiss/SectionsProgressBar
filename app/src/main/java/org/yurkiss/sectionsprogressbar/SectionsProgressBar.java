package org.yurkiss.sectionsprogressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
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
    private int             progress;

    private List<Section> sections;

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

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        Rect rect = backgroundDrawable.copyBounds();
        int d = 5;
        rect.left += d;
        rect.top += d;
        rect.right -= d;
        rect.bottom -= d;

        fillingDrawable.setBounds(rect);
        rect.right *= progress / 100f;

        // Set padding
        canvas.translate(getPaddingLeft(), getPaddingTop());
        canvas.clipRect(rect);
        fillingDrawable.draw(canvas);
        canvas.restore();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int percent) {
        this.progress = percent;
        invalidate();
    }

    public void setProgressBackgroundColor(int color){
        backgroundDrawable.setBarColor(color);
    }

    public int getProgressBackgroundColor(){
        return backgroundDrawable.getBarColor();
    }
    public void setProgressBarColor(int color){
        fillingDrawable.setBarColor(color);
    }

    public int getProgressBarColor(){
        return fillingDrawable.getBarColor();
    }

    public void addSection(Section section){
        sections.add(section);
        section.attachProgressBar(this);
    }

    public static class Section {

        private int max;
        private SectionsProgressBar bar;

        public Section(int max) {
            this.max = max;
        }

        public Section(int max, SectionsProgressBar bar) {
            this.max = max;
            bar.addSection(this);
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;

        }

        void attachProgressBar(SectionsProgressBar bar){
            this.bar = bar;
        }

        public void incrementProgress() {
            incrementProgress(1);
        }

        public void incrementProgress(int i){
            if (bar == null) {
                throw new IllegalStateException("Section has to be attached to progress bar.");
            }
            bar.setProgress(bar.getProgress() + i);
        }

    }



}
