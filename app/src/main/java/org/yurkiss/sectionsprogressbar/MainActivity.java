package org.yurkiss.sectionsprogressbar;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.yurkiss.sectionprogressbar.SectionsProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.img)
    SectionsProgressBar bar;

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
        bar.setImageLevel(0);
        section1 = new SectionsProgressBar.Section(250);
        section2 = new SectionsProgressBar.Section(250, bar);
        SectionsProgressBar.Section section3 = new SectionsProgressBar.Section(250);
        section4 = new SectionsProgressBar.Section(250, bar);

        bar.addSection(section1);
        bar.addSection(section3);

//        chartView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        img.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    }


    private void setSectionProgress(int value) {
//        TransitionManager.beginDelayedTransition(mTransitionsContainer, new ProgressTransition());
        value = Math.max(0, Math.min(bar.getMax(), value));
            SectionsProgressBar.Section section = bar.getSection(0);
        if (value == 0){
            section.invalidateProgress();
        } else {
            section.incrementProgress(value);
        }
//        img.setProgress(value);

    }

    @OnClick(R.id.b2_0)
    void OnClick20(View v) {
        SectionsProgressBar.Section section = bar.getSection(1);
        section.invalidateProgress();
    }

    @OnClick(R.id.b2_100)
    void OnClick2100(View v) {
        SectionsProgressBar.Section section = bar.getSection(1);
        section.setProgress(section.getMax() + 1);
    }

    @OnClick(R.id.b2_15)
    void OnClick215(View v) {
        SectionsProgressBar.Section section = bar.getSection(1);
        section.incrementProgress(25);
    }



    @OnClick(R.id.b25)
    void OnClick(View v) {
        setSectionProgress(0);
    }

    @OnClick(R.id.b75)
    void OnClick75(View v) {
        setSectionProgress(bar.getMax());
    }

    @OnClick(R.id.b15)
    void OnClick15(View v) {
        setSectionProgress(25);
    }

    @OnClick(R.id.bAdd)
    void OnClickBAdd(View v) {
        new SectionsProgressBar.Section(250, bar);
    }

    @OnClick(R.id.bRemove)
    void OnClickBRemove(View v) {
        bar.removeSection(0);
    }



}
