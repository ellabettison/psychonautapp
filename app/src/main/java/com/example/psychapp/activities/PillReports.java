package com.example.psychapp.activities;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.psychapp.NavegationBar;
import com.example.psychapp.R;
import com.example.psychapp.pillreports.Objects.PillObject;
import com.example.psychapp.pillreports.PillScraper;
import com.example.psychapp.pillreports.PillScraperBuilder;
import com.example.psychapp.pillreports.WebScraperCaller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PillReports extends AppCompatActivity {
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
            mControlsView.setVisibility(View.VISIBLE);
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

        String url;
        Serializable urlInp = (getIntent()
                .getSerializableExtra("url"));
        if (urlInp != null){
            url = urlInp.toString();
        } else {
            PillScraperBuilder builder = new PillScraperBuilder();
            url = builder.createPillScraper().generatePillScraper();
        }

        setContentView(R.layout.activity_pill_reports);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);

        //TODO: is this needed?? check
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        findViewById(R.id.searchButton).setOnClickListener(v -> {
            Intent intent = new Intent(PillReports.this, AdvancedPillSearch.class);
            startActivity(intent);
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        ViewGroup navegationBarLayout = findViewById(R.id.navegationBar);
        NavegationBar navegationBar = new NavegationBar(PillReports.this, navegationBarLayout);
        navegationBarLayout.findViewById(R.id.home_button).setOnClickListener(v -> navegationBar.homePress());
        navegationBarLayout.findViewById(R.id.od_button).setOnClickListener(v -> navegationBar.odPress());

        try {
            getPills(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getPills(String url) throws Exception {
        PillScraperBuilder builder = new PillScraperBuilder();
        //String url = builder.createPillScraper().generatePillScraper();
        final Typeface manjari = ResourcesCompat.getFont(this, R.font.manjari_bold);

        WebScraperCaller webScraperCaller = new WebScraperCaller();
        ArrayList<PillObject> pills = webScraperCaller.getPills(url, 0);
        Log.d("PILLS", "numer of pills: " + pills.size());

        ((TextView)findViewById(R.id.pillReportsLabel)).setTypeface(manjari);

        LinearLayout pillReportList = findViewById(R.id.pillReportList);

        int dividerHeight = (int) (getResources().getDisplayMetrics().density * 15);
        for (PillObject pill: pills){
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            View pillView = inflater.inflate(R.layout.pill_info,
                    (ViewGroup) findViewById(R.id.pillInfo), false);

            TextView name = pillView.findViewById(R.id.name);
            name.setText(pill.getName().toLowerCase());
            name.setTypeface(manjari);
            TextView date = pillView.findViewById(R.id.date);
            date.setText(pill.getDate().substring(0, pill.getDate().length()-3).toLowerCase());
            date.setTypeface(manjari);
            TextView location = pillView.findViewById(R.id.location);
            location.setText(pill.getLocation().toLowerCase());
            location.setTypeface(manjari);
            TextView colour = pillView.findViewById(R.id.colour);
            colour.setText(String.format("colour: %s", pill.getColour().toLowerCase()));
            colour.setTypeface(manjari);
            TextView logo = pillView.findViewById(R.id.logo);
            logo.setText(String.format("logo: %s", pill.getLogo().toLowerCase()));
            logo.setTypeface(manjari);
            TextView shape = pillView.findViewById(R.id.shape);
            shape.setText(String.format("shape: %s", pill.getShape().toLowerCase()));
            shape.setTypeface(manjari);
            TextView contents = pillView.findViewById(R.id.suspectedContents);
            contents.setText(String.format("suspected contents: %s", pill.getSuspectContents().toLowerCase()));
            contents.setTypeface(manjari);

            ImageView image = pillView.findViewById(R.id.pillImage);
            image.setImageDrawable(pill.getImage());
            image.setImageTintList(null);
            image.refreshDrawableState();

            pillReportList.addView(pillView);

            ImageView divider = new ImageView(this);
            divider.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dividerHeight));
            pillReportList.addView(divider);
        }

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
        mControlsView.setVisibility(View.GONE);
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
