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
import android.widget.Toast;

import com.example.psychapp.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class OverdoseInfo extends AppCompatActivity {

    private static ArrayList<String> substanceClasses = new ArrayList<>();
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

    private String standardise(String inp){
        if (inp.substring(inp.length() - 1).equals("s")){
            return (inp.substring(0, inp.length()-1).toLowerCase());
        }
        return inp.toLowerCase();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_overdose_info);

        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(view -> toggle());

        final Typeface manjari = ResourcesCompat.getFont(this, R.font.manjari_bold);

        getOdInfo();

        TextView treatmentLabel = findViewById(R.id.treatmentLabel);
        treatmentLabel.setTypeface(manjari);
        TextView symptomsLabel = findViewById(R.id.symptomsLabel);
        symptomsLabel.setTypeface(manjari);

    }

    private String readFile(){
        substanceClasses = (ArrayList<String>) Objects.requireNonNull(getIntent()
                .getSerializableExtra("substanceClasses"));

        BufferedReader reader = null;
        String content = "";
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("od.txt")));

            content = reader.readLine();
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        return content;
    }

    private void getOdInfo(){

        String content = readFile();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, JsonNode> classMap = null;
        try {
            classMap = objectMapper.readValue(content,
                    new TypeReference<Map<String, JsonNode>>() {});
        } catch (JsonProcessingException e) {
            String alertMessage = "sorry, something went wrong";
            Toast toast = Toast.makeText(this, alertMessage, Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
        assert classMap != null;
        JsonNode odInfo = classMap.get("general");
        String substanceClassFinal = "general";

        for (String substanceClass: substanceClasses){
            if (classMap.containsKey(standardise(substanceClass))){
                odInfo = classMap.get(standardise(substanceClass));
                substanceClassFinal = substanceClass;

            }
        }

        setInfo(substanceClassFinal, odInfo);

    }

    private void setInfo(String substanceClassFinal, JsonNode odInfo){
        ObjectMapper objectMapper = new ObjectMapper();
        final Typeface manjari = ResourcesCompat.getFont(this, R.font.manjari_bold);
        TextView header = findViewById(R.id.header);
        header.setTypeface(manjari);
        String headerText = String.format("%s overdose", standardise(substanceClassFinal));
        header.setText(headerText);

        try {
            Map<String, JsonNode> symptomsTreatmentMap = objectMapper.readValue(objectMapper.writeValueAsString(odInfo),
                    new TypeReference<Map<String, JsonNode>>() {});
            TextView treatmentInfo = findViewById(R.id.treatmentInfo);
            String treatmentString = objectMapper.writeValueAsString(symptomsTreatmentMap.get("treatment"));
            treatmentInfo.setText(treatmentString.substring(1, treatmentString.length()-1).toLowerCase());
            treatmentInfo.setTypeface(manjari);
            ArrayList<JsonNode> symptomsList = objectMapper.readValue(objectMapper
                    .writeValueAsString(symptomsTreatmentMap.get("symptoms")), new TypeReference<ArrayList<JsonNode>>() {});
            getSymptoms(symptomsList);
        } catch (JsonProcessingException e) {
            String alertMessage = "sorry, something went wrong";
            Toast toast = Toast.makeText(this, alertMessage, Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
    }


    protected void getSymptoms(ArrayList<JsonNode> symptoms) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        int dividerHeight = (int) (getResources().getDisplayMetrics().density * 10);
        final Typeface manjari = ResourcesCompat.getFont(this, R.font.manjari_bold);

        LinearLayout contentLayout = findViewById(R.id.symptomsList);
        for (JsonNode effect : symptoms) {
            TextView effectText = new TextView(new ContextThemeWrapper(this, R.style.EffectLabel), null, 0);
            effectText.setTypeface(manjari);
            String symptomName = objectMapper.writeValueAsString(effect.get("name"));
            effectText.setText(symptomName.substring(1, symptomName.length()-1).toLowerCase());
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
//        mControlsView.setVisibility(View.GONE);
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
