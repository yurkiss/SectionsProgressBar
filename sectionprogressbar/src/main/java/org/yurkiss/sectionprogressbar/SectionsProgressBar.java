package org.yurkiss.sectionprogressbar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
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

    private AnimatorSet animSet;
    private int duration = 30 * 16;

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

    private boolean isLast(Section section) {
        int i = sections.indexOf(section);
        return i == sections.size() - 2;
    }

    void setProgress(Section section, int progress) {
        List<Animator> animators = new ArrayList<>();

        progress = Math.max(0, Math.min(section.getMax(), progress));

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

            // For the terminal section set all progress if last is full
            if (isLast(section)) {
                if (section.getMax() == progress) {
                    SectionsProgressBar.Section terminalSec = sections.get(sections.size() - 1);
                    animator = createAnimator(terminalSec, terminalSec.getMax());
                    animators.add(animator);
                }
            }

            // Decrement progress
            if (progress < section.getProgress()) {
                for (int p = i + 1; p < sections.size(); p++) {
                    Section sec = sections.get(p);
                    animators.add(createAnimator(sec, progress));
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

    public List<Section> getSections() {
        return sections;
    }

    public Section getSection(int i) {
        if (i < sections.size()) {
            return sections.get(i);
        }
        return null;
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

        boolean isFull() {
            return getProgress() == getMax();
        }

        boolean isEmpty() {
            return getProgress() == 0;
        }

        void attachProgressBar(SectionsProgressBar bar) {
            this.bar = bar;
        }

        public void incrementProgress() {
            incrementProgress(1);
        }

        public void setProgress(int pr) {
            if (bar == null) {
                throw new IllegalStateException("Section has to be attached to progress bar.");
            }
            bar.setProgress(this, pr);
        }

        public void incrementProgress(int i) {

            if (bar == null) {
                throw new IllegalStateException("Section has to be attached to progress bar.");
            }
            bar.setProgress(this, progress + i);
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
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                section.progress = animatedValue;
                postInvalidate();
            }
        });

        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.setDuration(duration);

        return progressAnimator;
    }

}

