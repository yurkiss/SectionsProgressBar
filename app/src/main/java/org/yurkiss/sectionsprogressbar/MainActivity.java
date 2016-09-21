package org.yurkiss.sectionsprogressbar;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.img)
    SectionsProgressBar img;

//    @Bind(R.id.shadow)
//    ChartView chartView;

    @Bind(R.id.b25) Button btn25;
    @Bind(R.id.b75) Button btn75;
    @Bind(R.id.b15) Button btn15;

    @Bind(R.id.b2_0) Button btn2_0;
    @Bind(R.id.b2_100) Button btn2_100;
    @Bind(R.id.b2_15) Button btn2_15;

    @Bind(R.id.contentContainer)
    ViewGroup mTransitionsContainer;

    private Handler mHandler;
    private int mLevel;
    private int delayMillis;
    private SectionsProgressBar.Section section1;
    private SectionsProgressBar.Section section2;
    private SectionsProgressBar.Section section4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        img.startAnimation();

        ButterKnife.bind(this);
        img.setImageLevel(0);
        section1 = new SectionsProgressBar.Section(250);
        section2 = new SectionsProgressBar.Section(250, img);
        SectionsProgressBar.Section section3 = new SectionsProgressBar.Section(250);
        section4 = new SectionsProgressBar.Section(250, img);

        img.addSection(section1);
        img.addSection(section3);

//        chartView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mHandler = new Handler();
        delayMillis = 50;
//        mHandler.postDelayed(animateImage, delayMillis);
    }

//    private Runnable animateImage = new Runnable() {
//
//        @Override
//        public void run() {
//            doTheAnimation();
//        }
//    };
//
//    private void doTheAnimation() {
//        mLevel += 1;
//        img.setProgress(mLevel);
//        img.invalidate();
//        if (mLevel <= 100) {
//            mHandler.postDelayed(animateImage, delayMillis);
//        } else {
//            mHandler.removeCallbacks(animateImage);
//        }
//    }

    private void setSectionProgress(int value) {
//        TransitionManager.beginDelayedTransition(mTransitionsContainer, new ProgressTransition());
        value = Math.max(0, Math.min(img.getMax(), value));
        if (value == 0){
            section2.invalidateProgress();
        } else {
            section2.incrementProgress(value);
        }
//        img.setProgress(value);

    }

    @OnClick(R.id.b2_0)
    void OnClick20(View v) {
        section4.invalidateProgress();
    }

    @OnClick(R.id.b2_100)
    void OnClick2100(View v) {
        section4.setProgress(section4.getMax() + 1);
    }

    @OnClick(R.id.b2_15)
    void OnClick215(View v) {
        section4.incrementProgress(25);
    }



    @OnClick(R.id.b25)
    void OnClick(View v) {
        setSectionProgress(0);
    }

    @OnClick(R.id.b75)
    void OnClick75(View v) {
        setSectionProgress(img.getMax());
    }

    @OnClick(R.id.b15)
    void OnClick15(View v) {
        setSectionProgress(25);
    }

    @OnClick(R.id.bAdd)
    void OnClickBAdd(View v) {
        new SectionsProgressBar.Section(250, img);
    }

    @OnClick(R.id.bRemove)
    void OnClickBRemove(View v) {
        img.removeSection(0);
    }


//    private static class ProgressTransition extends Transition {
//
//        /**
//         * Property is like a helper that contain setter and getter in one place
//         */
//        private static final Property<SectionsProgressBar, Integer> PROGRESS_PROPERTY =
//                new IntProperty<SectionsProgressBar>() {
//
//                    @Override
//                    public void setValue(SectionsProgressBar progressBar, int value) {
//                        progressBar.setProgress(value);
//                    }
//
//                    @Override
//                    public Integer get(SectionsProgressBar progressBar) {
//                        return progressBar.getProgress();
//                    }
//                };
//
//        /**
//         * Internal name of property. Like a intent bundles
//         */
//        private static final String PROPNAME_PROGRESS = "ProgressTransition:progress";
//
//        @Override
//        public void captureStartValues(TransitionValues transitionValues) {
//            captureValues(transitionValues);
//        }
//
//        @Override
//        public void captureEndValues(TransitionValues transitionValues) {
//            captureValues(transitionValues);
//        }
//
//        private void captureValues(TransitionValues transitionValues) {
//            if (transitionValues.view instanceof SectionsProgressBar) {
//                // save current progress in the values map
//                SectionsProgressBar progressBar = ((SectionsProgressBar) transitionValues.view);
//                transitionValues.values.put(PROPNAME_PROGRESS, progressBar.getProgress());
//            }
//        }
//
//        @Override
//        public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues,
//                                       TransitionValues endValues) {
//            if (startValues != null && endValues != null && endValues.view instanceof SectionsProgressBar) {
//                SectionsProgressBar progressBar = (SectionsProgressBar) endValues.view;
//                int start = (Integer) startValues.values.get(PROPNAME_PROGRESS);
//                int end = (Integer) endValues.values.get(PROPNAME_PROGRESS);
//                if (start != end) {
//                    // first of all we need to apply the start value, because right now
//                    // the view is have end value
//                    progressBar.setProgress(start);
//                    // create animator with our progressBar, property and end value
//                    return ObjectAnimator.ofInt(progressBar, PROGRESS_PROPERTY, end);
//                }
//            }
//            return null;
//        }
//    }
//
}
