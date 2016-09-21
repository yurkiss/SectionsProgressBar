package org.yurkiss.sectionsprogressbar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yurkiss on 7/25/16.
 */

public class SectionsProgressBar extends ImageView {

    private ProgressDrawable progressDrawable;

    private List<Section> sections;

    private int         progress;
    private RectF       rectF;
    private Rect        rect;
    private AnimatorSet animSet;
    private int duration = 30 * 15;

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

        progressDrawable = new ProgressDrawable();
        progressDrawable.setBarColor(0xFFFF4081);

        setImageDrawable(progressDrawable);
        rectF = new RectF();
        rect = new Rect();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


    public int getMax() {
        int max = 0;
        for (Section section : sections) {
            max += section.getMax();
        }
        return max;
    }

    public int getProgress() {
        int progress = 0;
        for (Section section : sections) {
            progress += section.getProgress();
        }
        return progress;
    }

    void setProgress(Section section, int progress) {
        List<Animator> animators = new ArrayList<>();

        if (progress > section.getMax()) {
            progress = section.getMax();
        }

        int i = sections.indexOf(section);
        if (i >= 0) {
            for (int p = 0; p < i; p++) {
                Section sec = sections.get(p);
                if (sec.getProgress() < sec.getMax()) {
                    Animator animator = createAnimator(sec, sec.getMax());
                    animators.add(animator);
                }
            }

            Animator animator = createAnimator(section, progress);
            animators.add(animator);

            if (progress == 0) {
                for (int p = i + 1; p < sections.size(); p++) {
                    Section sec = sections.get(p);
                    animators.add(createAnimator(sec, 0));
                }
            }

            if (!isAnimationStarted()) {
                animSet = new AnimatorSet();
                animSet.playSequentially(animators);
                animSet.start();
            }
        }
    }

    private boolean isAnimationStarted() {
        return animSet != null && animSet.isStarted();
    }

//    private void setProgress(int progress) {
////        System.out.println(progress);
//        if (!isAnimationStarted()) {
//            animator = createAnimator(progress);
//            animator.start();
//            return;
//        }
//
//        this.progress = Math.max(0, Math.min(getMax(), progress));
//        postInvalidate();
//    }

    public void setProgressBackgroundColor(int color) {
        progressDrawable.setBackgroundBarColor(color);
    }

    public int getProgressBackgroundColor() {
        return progressDrawable.getBackgroundBarColor();
    }

    public void setProgressBarColor(int color) {
        progressDrawable.setBarColor(color);
    }

    public int getProgressBarColor() {
        return progressDrawable.getBarColor();
    }

    public void addSection(Section section) {
        sections.add(section);
        section.attachProgressBar(this);
        progressDrawable.setSections(sections);
        postInvalidate();
    }

    public void removeSection(int i) {
        if (i < sections.size()) {
            sections.remove(i);
            progressDrawable.setSections(sections);
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
            if (max < progress) { //cutting progress when max changed
                progress = max;
            }
        }

        int getProgress() {
            return progress;
        }

        void finish() {
            bar.setProgress(this, max);
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
            bar.setProgress(this, progress + i);
        }

        public void setProgress(int pr) {
            if (bar == null) {
                throw new IllegalStateException("Section has to be attached to progress bar.");
            }
            bar.setProgress(this, pr);
        }

        public void invalidateProgress() {
            if (bar == null) {
                throw new IllegalStateException("Section has to be attached to progress bar.");
            }
            bar.setProgress(this, 0);
        }

    }

    private Animator createAnimator(final Section section, int newProgress) {

        ValueAnimator progressAnimator = ValueAnimator.ofInt(section.getProgress(), newProgress);
//        ValueAnimator progressAnimator = ValueAnimator.ofObject(new FloatEvaluator(), scale, newScale);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
//                setProgress(section, animatedValue);
//                section.incrementProgress(animatedValue);
                section.progress = animatedValue;
                postInvalidate();
            }
        });

//        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(this, "progress", progress, newScale);
        progressAnimator.setInterpolator(new LinearInterpolator());
//        duration = Math.abs(newScale - progress) * 16;
        progressAnimator.setDuration(duration);
//        System.out.println(String.format("Duration: %s", duration));

//        progressAnimator.setRepeatMode(ValueAnimator.RESTART);
//        progressAnimator.setRepeatCount(ValueAnimator.INFINITE);

//        AnimatorSet set = new AnimatorSet();
//        Animator progressAnimator = getAnimator(SECONDARY_PROGRESS, new AccelerateDecelerateInterpolator());
//        Animator secondaryProgressAnimator = getAnimator(PROGRESS, new AccelerateInterpolator());
//        set.playTogether(progressAnimator, secondaryProgressAnimator);
//        set.setDuration(duration);
        return progressAnimator;
    }

}

