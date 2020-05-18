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
import com.example.psychapp.api.APIClient;
import com.example.psychapp.api.QueryBuilder;
import com.example.psychapp.api.QueryObjects.EffectObject;
import com.example.psychapp.api.QueryObjects.RoaObject;
import com.example.psychapp.api.QueryObjects.SubstanceObject;
import com.example.psychapp.api.QueryObjects.UnitsObject;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SubstanceInfo extends AppCompatActivity {

    private String substanceName;
    private SubstanceObject substanceObject;
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
    //private View mControlsView;
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
        final Typeface manjari = ResourcesCompat.getFont(this, R.font.manjari_bold);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_substance_info);

        mVisible = true;
        //mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        substanceName = Objects.requireNonNull(getIntent()
                .getSerializableExtra("substanceName")).toString();
        TextView substanceText = findViewById(R.id.substanceName);
        substanceText.setTypeface(manjari);
        substanceText.setText(substanceName);

        try {
            getSubstanceInfo();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    protected void getSubstanceInfo() throws ExecutionException, InterruptedException {
        final Typeface manjari = ResourcesCompat.getFont(this, R.font.manjari_bold);
        APIClient apiClient = new APIClient();
        QueryBuilder queryBuilder = new QueryBuilder();
        String query = queryBuilder.queryByName(substanceName).withName().withRoas()
                .withEffects().withInteractions().getQuery();

        substanceObject = apiClient.execute(query).get().get(0);
        RoaObject mainRoa = substanceObject.getRoas().get(0);
        String units = mainRoa.getDosage().getUnits();
        System.out.printf("\nUNITS: %s\n", units);

        TextView lightText = findViewById(R.id.lightStrength);
        lightText.setTypeface(manjari);
        TextView commonText = findViewById(R.id.commonStrength);
        commonText.setTypeface(manjari);
        TextView strongText = findViewById(R.id.strongStrength);
        strongText.setTypeface(manjari);

        UnitsObject lightUnitsDosage = mainRoa.getDosage().getLight();
        TextView lightDosage = findViewById(R.id.lightDosage);
        String lightDosageString = String.format("%d - %d %s",
                lightUnitsDosage.getMin(),
                lightUnitsDosage.getMax(),
                units);
        lightDosage.setText(lightDosageString);
        lightDosage.setTypeface(manjari);

        UnitsObject commonUnitsDosage = mainRoa.getDosage().getCommon();
        TextView commonDosage = findViewById(R.id.commonDosage);
        String commonDosageString = String.format("%d - %d %s",
                commonUnitsDosage.getMin(),
                commonUnitsDosage.getMax(),
                units);
        commonDosage.setText(commonDosageString);
        commonDosage.setTypeface(manjari);

        UnitsObject strongUnitsDosage = mainRoa.getDosage().getStrong();
        TextView strongDosage = findViewById(R.id.strongDosage);
        String strongDosageString = String.format("%d - %d %s",
                strongUnitsDosage.getMin(),
                strongUnitsDosage.getMax(),
                units);
        strongDosage.setText(strongDosageString);
        strongDosage.setTypeface(manjari);

        int dividerHeight = (int) (getResources().getDisplayMetrics().density * 10);

        LinearLayout contentLayout = findViewById(R.id.effectsList);
        for (EffectObject effect: substanceObject.getEffects()){
            TextView effectText = new TextView(new ContextThemeWrapper(this, R.style.EffectLabel), null, 0);
            effectText.setTypeface(manjari);
            effectText.setText(effect.getName());
            contentLayout.addView(effectText);
            ImageView divider = new ImageView(this);
            divider.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dividerHeight));
            contentLayout.addView(divider);
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
