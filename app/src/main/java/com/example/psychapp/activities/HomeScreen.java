package com.example.psychapp.activities;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.psychapp.NavegationBar;
import com.example.psychapp.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class HomeScreen extends AppCompatActivity {

    private int totalChildren;
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
//            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = this::hide;

    protected void search(String searchTerm){
        Intent intent = new Intent(HomeScreen.this, SubstanceInfo.class);
        intent.putExtra("substanceName", searchTerm);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        TextView searchText = findViewById(R.id.searchText);
//        searchText.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                    search(searchText.getText().toString());
//                }
//                return true;
//            }
//        });
        ImageButton searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> {
            search(searchText.getText().toString());
            searchText.setText("");
        });


        mVisible = true;
        //mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(view -> toggle());

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        LinearLayout buttonsLayout = findViewById(R.id.buttonsLayout);
        LinearLayout subLayout;

        int totalChildrenCount = 0;

        // generate onclick events for each button
        for (int i = 0; i < buttonsLayout.getChildCount(); i++){
            subLayout = (LinearLayout) buttonsLayout.getChildAt(i);
            for (int j = 0; j < subLayout.getChildCount(); j++){
                totalChildren++;
                ImageButton imageButton = (ImageButton) subLayout.getChildAt(j);
                imageButton.setOnClickListener(v -> {
                    Intent intent = new Intent(HomeScreen.this, SubstanceSelector.class);
                    intent.putExtra("substanceClass", imageButton.getTag().toString());
                    startActivity(intent);
                });
            }

        }

        totalChildren = totalChildrenCount;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        resize(width, height);

        ViewGroup navegationBarLayout = findViewById(R.id.navegationBar);

        NavegationBar navegationBar = new NavegationBar(HomeScreen.this, navegationBarLayout);

        navegationBarLayout.findViewById(R.id.od_button).setOnClickListener(v -> navegationBar.odPress());
        navegationBarLayout.findViewById(R.id.pill_button).setOnClickListener(v -> navegationBar.pillPress());


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            resize(height, width);
        } else {
            resize(width, height);
        }
    }

    protected void resize(int width, int height){

        LinearLayout buttonsLayout = findViewById(R.id.buttonsLayout);
        LinearLayout subLayout;

        int BUTTON_WIDTH = 250;

        Resources r = getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                BUTTON_WIDTH,
                r.getDisplayMetrics()
        );

        int buttonsPerLayout = (int) (width/px);

        int rowsWithChildren = (totalChildren/buttonsPerLayout)-1;
        int childrenOnLastRow = totalChildren % buttonsPerLayout;
        int childrenToMove = 0;

        LinearLayout nextLayout = (LinearLayout) buttonsLayout.getChildAt(buttonsLayout.getChildCount()-1);

        //relocate children
        for (int i = buttonsLayout.getChildCount()-2; i >= 0; i--){
            subLayout = (LinearLayout) buttonsLayout.getChildAt(i);
            if (nextLayout != subLayout && subLayout.getChildCount() < buttonsPerLayout) {
                if (i > rowsWithChildren){
                    childrenToMove = nextLayout.getChildCount();
                } else if (i == rowsWithChildren){
                    childrenToMove = nextLayout.getChildCount() - childrenOnLastRow;
                } else {
                    childrenToMove = nextLayout.getChildCount() - buttonsPerLayout;
                }

                for (int k = 0; k < childrenToMove; k++) {
                    ImageButton childToMove = (ImageButton) nextLayout.getChildAt(0);
                    if (childToMove != null) {
                        nextLayout.removeView(childToMove);
                        subLayout.addView(childToMove);
                    }
                }
            }
            if (nextLayout.getChildCount() == 0){
                buttonsLayout.removeView(nextLayout);
            }
            nextLayout = subLayout;

        }

        // resize layout heights for screen height
        for (int i = 0; i < buttonsLayout.getChildCount(); i++){
            LinearLayout buttonLayout = (LinearLayout) buttonsLayout.getChildAt(i);
            if (buttonLayout.getChildCount() != 0) {
                ViewGroup.LayoutParams params = buttonLayout.getLayoutParams();
                params.height = (int) (height/3);
                buttonLayout.setLayoutParams(params);
                buttonLayout.requestLayout();
            }

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
