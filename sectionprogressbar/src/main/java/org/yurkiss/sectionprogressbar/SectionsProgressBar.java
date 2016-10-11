package org.yurkiss.sectionprogressbar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v4.util.Pools;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Property;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yurkiss on 7/25/16.
 */

public class SectionsProgressBar extends ImageView {

    private static final String TAG                    = SectionsProgressBar.class.getSimpleName();
    private static final int    PROGRESS_ANIM_DURATION = 80 * 5;

    private ProgressDrawable progressDrawable;
    private List<Section>    sections;
    private AnimatorSet      animSet;

    private long    mUiThreadId;
    private boolean mAttached;
    boolean mRefreshIsPosted;
    final ArrayList<RefreshData> mRefreshData = new ArrayList<RefreshData>();
    private RefreshProgressRunnable mRefreshProgressRunnable;

    private int duration = 30 * 16;

    public SectionsProgressBar(Context context) {
        this(context, null);
    }

    public SectionsProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mRefreshData != null) {
            synchronized (this) {
                final int count = mRefreshData.size();
                for (int i = 0; i < count; i++) {
                    final RefreshData rd = mRefreshData.get(i);
                    doRefreshProgress(rd.section, rd.progress);
                    rd.recycle();
                }
                mRefreshData.clear();
            }
        }
        mAttached = true;
    }

    private void init() {

        mUiThreadId = Thread.currentThread().getId();

        sections = new ArrayList<>();

        progressDrawable = new ProgressDrawable();
        progressDrawable.setBarColor(0xFFFF4081);
        progressDrawable.setSections(sections);

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

    //    void setProgress(Section section, int progress) {
    synchronized void doRefreshProgress(Section section, int progress) {
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
                    if (sec.getProgress() > 0) {
                        animators.add(createAnimator(sec, 0));
                    }
                }
            }

            if (animSet == null) {
                animSet = new AnimatorSet();
            }
            if (animSet.isRunning()) {
                animSet.cancel();
                animSet = new AnimatorSet();
            }
            animSet.playSequentially(animators);
            animSet.start();

        }
    }

    public synchronized void refreshProgress(Section section, int progress) {
        if (mUiThreadId == Thread.currentThread().getId()) {
            doRefreshProgress(section, progress);
        } else {
            if (mRefreshProgressRunnable == null) {
                mRefreshProgressRunnable = new RefreshProgressRunnable();
            }

            final RefreshData rd = RefreshData.obtain(section, progress);
            mRefreshData.add(rd);
            if (mAttached && !mRefreshIsPosted) {
                post(mRefreshProgressRunnable);
                mRefreshIsPosted = true;
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
//        invalidate();
        postInvalidate();
    }

    public void removeSection(int i) {
        if (i < sections.size()) {
            sections.remove(i);
            progressDrawable.setSections(sections);
//            invalidate();
            postInvalidate();
        }
    }

    public synchronized void removeAllSections() {
        sections.clear();
        progressDrawable.setSections(sections);
//        invalidate();
        postInvalidate();
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


    private final IntProperty<SectionsProgressBar.Section> VISUAL_PROGRESS =
            new IntProperty<SectionsProgressBar.Section>("visual_progress") {
                @Override
                public void setValue(SectionsProgressBar.Section object, int value) {
                    object.progress = value;
                    SectionsProgressBar.this.invalidate();
                }

                @Override
                public Integer get(SectionsProgressBar.Section object) {
                    return object.progress;
                }
            };

    abstract class IntProperty<T> extends Property<T, Integer> {

        public IntProperty(String name) {
            super(Integer.class, name);
        }

        /**
         * A type-specific variant of {@link #set(Object, Integer)} that is faster when dealing
         * with fields of type <code>int</code>.
         */
        public abstract void setValue(T object, int value);

        @Override
        final public void set(T object, Integer value) {
            setValue(object, value.intValue());
        }

    }

    private Animator createAnimator(final Section section, int newProgress) {
        Log.d(TAG, String.format("Creating animator from %s to %s for %s", section.getProgress(), newProgress, section.toString()));
        final ObjectAnimator progressAnimator = ObjectAnimator.ofInt(section, VISUAL_PROGRESS, section.getProgress(), newProgress);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            progressAnimator.setAutoCancel(true);
        }
        progressAnimator.setDuration(PROGRESS_ANIM_DURATION);
        progressAnimator.setInterpolator(new LinearInterpolator());

//        ValueAnimator progressAnimator = ValueAnimator.ofInt(section.getProgress(), newProgress);
//        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                int animatedValue = (int) animation.getAnimatedValue();
//                section.progress = animatedValue;
////                invalidate();
//                postInvalidate();
//            }
//        });
//
//        progressAnimator.setInterpolator(new LinearInterpolator());
//        progressAnimator.setDuration(duration);

        return progressAnimator;
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
            bar.refreshProgress(this, max);
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
            if (pr == progress) return;
            bar.refreshProgress(this, pr);
        }

        public void incrementProgress(int i) {

            if (bar == null) {
                throw new IllegalStateException("Section has to be attached to progress bar.");
            }
            bar.refreshProgress(this, progress + i);
        }

        public void invalidateProgress() {
            if (bar == null) {
                throw new IllegalStateException("Section has to be attached to progress bar.");
            }
            bar.refreshProgress(this, 0);
        }

    }

    private class RefreshProgressRunnable implements Runnable {
        public void run() {
            synchronized (SectionsProgressBar.this) {
                final int count = mRefreshData.size();
                for (int i = 0; i < count; i++) {
                    final RefreshData rd = mRefreshData.get(i);
                    doRefreshProgress(rd.section, rd.progress);
                    rd.recycle();
                }
                mRefreshData.clear();
                mRefreshIsPosted = false;
            }
        }
    }

    private static class RefreshData {
        private static final int POOL_MAX = 24;

        private static final Pools.SynchronizedPool<RefreshData> sPool =
                new Pools.SynchronizedPool<RefreshData>(POOL_MAX);

        public Section section;
        public int     progress;

        public static RefreshData obtain(Section section, int progress) {
            RefreshData rd = sPool.acquire();
            if (rd == null) {
                rd = new RefreshData();
            }
            rd.section = section;
            rd.progress = progress;
            return rd;
        }

        public void recycle() {
            sPool.release(this);
        }
    }


}

