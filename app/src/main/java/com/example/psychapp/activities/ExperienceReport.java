package com.example.psychapp.activities;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.psychapp.R;
import com.example.psychapp.elements.NavegationBar;
import com.example.psychapp.experiencereports.ExperienceReportScraper;
import com.example.psychapp.experiencereports.Objects.ExperienceReportObject;
import com.example.psychapp.experiencereports.Objects.ExperienceSectionObject;
import com.example.psychapp.pillreports.WebScraper;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ExperienceReport extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            //mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Typeface manjari = ResourcesCompat.getFont(this, R.font.manjari_bold);
        int dividerHeight = (int) (getResources().getDisplayMetrics().density * 10);

        setContentView(R.layout.activity_experience_report);

        mContentView = findViewById(R.id.fullscreen_content);
        mContentView.setOnClickListener(view -> toggle());

        mVisible = true;

        String experienceReportName = Objects.requireNonNull(getIntent()
                .getSerializableExtra("experienceReportName")).toString();
        String experienceReportUrl = Objects.requireNonNull(getIntent()
                .getSerializableExtra("experienceReportUrl")).toString();
        ExperienceReportObject report = (ExperienceReportObject) getIntent()
                .getSerializableExtra("experienceReport");

        ((TextView) findViewById(R.id.experienceReportTitle)).setTypeface(manjari);

        if (report == null) {
            report = getExperienceReport(experienceReportName, experienceReportUrl);
        }

        ((TextView) findViewById(R.id.experienceReportTitle)).setText(report.getTitle());
        LinearLayout contentLayout = findViewById(R.id.experienceReportList);

        for (ExperienceSectionObject section: report.getReport()) {
//                    if (section.getBody() != null) {
            if (section.getTitle() != null) {
                TextView sectionTitle = new TextView(new ContextThemeWrapper(this, R.style.subheading), null, 0);
                sectionTitle.setTypeface(manjari);
                sectionTitle.setText(section.getTitle());
                contentLayout.addView(sectionTitle);
            }
            if (section.getBody() != null) {
                TextView sectionBody = new TextView(new ContextThemeWrapper(this, R.style.EffectLabel), null, 0);
                sectionBody.setTypeface(manjari);
                sectionBody.setText(section.getBody());
                contentLayout.addView(sectionBody);
                ImageView divider = new ImageView(this);
                divider.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dividerHeight));
                contentLayout.addView(divider);
            }
//                    } else {
//                        TextView sectionBody = new TextView(new ContextThemeWrapper(this, R.style.subheading), null, 0);
//                        sectionBody.setTextSize(20);
//                        sectionBody.setTypeface(manjari);
//                        sectionBody.setText(section.getTitle());
//                        contentLayout.addView(sectionBody);
//                    }
        }

        setupNavbar();
    }

    private ExperienceReportObject getExperienceReport(String experienceReportName, String experienceReportUrl){
        final Typeface manjari = ResourcesCompat.getFont(this, R.font.manjari_bold);
        WebScraper webScraper = new WebScraper();
        String paragraphs = "";
        try {
            paragraphs = webScraper.execute("https://psychonautwiki.org/" + experienceReportUrl).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if (!paragraphs.equals("")){
            ExperienceReportScraper reportScraper = new ExperienceReportScraper();
            try {

                return reportScraper.execute(experienceReportName, paragraphs).get();


            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void setupNavbar(){
        ViewGroup navegationBarLayout = findViewById(R.id.navegationBar);

        NavegationBar navegationBar = new NavegationBar(ExperienceReport.this, navegationBarLayout);

//        navegationBarLayout.findViewById(R.id.home_button).setOnClickListener(v -> navegationBar.homePress());
//        navegationBarLayout.findViewById(R.id.od_button).setOnClickListener(v -> navegationBar.odPress());
//        navegationBarLayout.findViewById(R.id.pill_button).setOnClickListener(v -> navegationBar.pillPress());
//        navegationBarLayout.findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        //mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

}
