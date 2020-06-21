package com.example.psychapp.activities;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ContentFrameLayout;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.example.psychapp.R;
import com.example.psychapp.elements.NavegationBar;
import com.example.psychapp.experiencereports.ExperienceNameScraper;
import com.example.psychapp.pillreports.WebScraper;
import com.example.psychapp.summaryscraper.SummaryScraper;
import com.example.psychapp.wikiapi.APIClient;
import com.example.psychapp.wikiapi.QueryBuilder;
import com.example.psychapp.wikiapi.QueryObjects.DurationObject;
import com.example.psychapp.wikiapi.QueryObjects.EffectObject;
import com.example.psychapp.wikiapi.QueryObjects.RoaObject;
import com.example.psychapp.wikiapi.QueryObjects.SubstanceObject;
import com.example.psychapp.wikiapi.QueryObjects.UnitsObject;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SubstanceInfo extends AppCompatActivity {

    private String substanceName;
    private SubstanceObject substanceObject;
    private String sumText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Typeface manjari = ResourcesCompat.getFont(this, R.font.manjari_bold);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_substance_info);

        mVisible = true;
        //mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(view -> toggle());

        substanceName = Objects.requireNonNull(getIntent()
                .getSerializableExtra("substanceName")).toString();
        TextView substanceText = findViewById(R.id.substanceName);
        substanceText.setTypeface(manjari);
        substanceText.setText(substanceName);

        try {
            getSubstanceInfo();
        } catch (ExecutionException | InterruptedException | NullPointerException e) {
            String alertMessage = "sorry, the substance could not be found";
            Toast toast = Toast.makeText(this, alertMessage, Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }

        ImageButton overdoseButton1 = findViewById(R.id.overdoseButton1);
        Button overdoseButton2 = findViewById(R.id.overdoseButton2);

        overdoseButton1.setOnClickListener(v -> {
            callOdPage();
        });
        overdoseButton2.setOnClickListener(v -> {
            callOdPage();
        });



//        for (View roa: roas){
//            if (roa != null) {
//                roa.setMinimumWidth(500);
//            }
//        }

        TextView summary = findViewById(R.id.summary);
        SummaryScraper summaryScraper = new SummaryScraper();
        summary.setTypeface(manjari);


        summary.setOnClickListener(v -> {
            if (summary.getText().equals(getResources().getString(R.string.clickToExpand))){
                if (sumText == null) {
                    sumText = summaryScraper.getSummary(substanceObject.getSubstanceClass().getPsychoactive().get(0), substanceName, this);
                }
                summary.setText(sumText);
            } else {
                summary.setText(R.string.clickToExpand);
            }
        });



//        LinearLayout roaLayout = findViewById(R.id.roaLayout);
//        ViewGroup.LayoutParams params = roaLayout.getLayoutParams();
//        params.width = (width*3);
//        roaLayout.setLayoutParams(params);
//        roaLayout.requestLayout();

//        scrollView.setMinimumWidth((width));
//        scrollView.requestLayout();

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        setupNavbar();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        setupDosageLabel();
        setupScrollLabel();
    }

    private void setupScrollLabel(){

        View contentScroll = findViewById(R.id.contentScroll);

        View summaryContent = findViewById(R.id.summary);
        View interactionsContent = findViewById(R.id.interactions);
        View toxicityContent = findViewById(R.id.toxicity);
        View effectsContent = findViewById(R.id.effects);
        View experienceContent = findViewById(R.id.experiences);

        ArrayList<View> scrollContents = new ArrayList<>(Arrays.asList(
                summaryContent, interactionsContent, toxicityContent,
                effectsContent, experienceContent
        ));

        int[] summaryLocation = new int[2];
        TextView summaryLabel = findViewById(R.id.summaryLabel);
        summaryLabel.getLocationOnScreen(summaryLocation);
        int summaryY = summaryLocation[1];

        contentScroll.getViewTreeObserver().addOnScrollChangedListener(() -> {
            for (View roa: scrollContents){
                if (roa != null) {
                    int[] roaLoc = new int[2];
                    roa.getLocationOnScreen(roaLoc);
                    if (roaLoc[1]+200< summaryY && (roaLoc[1]+200 + roa.getHeight()) > summaryY) {
                        summaryLabel.setText(roa.getTag().toString());
                    }
                }
            }
        });

    }

    private void setupDosageLabel(){

        HorizontalScrollView scrollView = findViewById(R.id.roaScroll);

        View roa1 = findViewById(R.id.roa1);
        View roa2 = findViewById(R.id.roa2);
        View roa3 = findViewById(R.id.roa3);
        ArrayList<View> roas = new ArrayList<>(Arrays.asList(roa1, roa2, roa3));

        int[] roaLoc1 = new int[2];
        roa1.getLocationOnScreen(roaLoc1);
        int centre = roaLoc1[0];

        TextView dosageLabel = findViewById(R.id.dosageLabel);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            for (View roa: roas){
                if (roa != null) {
                    int[] roaLoc = new int[2];
                    roa.getLocationOnScreen(roaLoc);
                    if (roaLoc[0]-300 < centre && roaLoc[0]+300 > centre) {
                        dosageLabel.setText(String.format("dosage - %s", roa.getTag()));
                    }
                }
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        // convert roaBorder from dp to px
        float roaBorder = 20f;
        Resources r = getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                roaBorder,
                r.getDisplayMetrics()
        );

        // set width of roa info
        for (View roa: roas){
            if (roa != null) {
                ViewGroup.LayoutParams params = roa.getLayoutParams();
                params.width = (int) (width - (px*2));
                roa.setLayoutParams(params);
                roa.requestLayout();
            }

        }
    }

    private void setupNavbar(){
        ViewGroup navegationBarLayout = findViewById(R.id.navegationBar);

        NavegationBar navegationBar = new NavegationBar(SubstanceInfo.this, navegationBarLayout);

        navegationBarLayout.findViewById(R.id.home_button).setOnClickListener(v -> navegationBar.homePress());
        navegationBarLayout.findViewById(R.id.od_button).setOnClickListener(v -> navegationBar.odPress());
        navegationBarLayout.findViewById(R.id.pill_button).setOnClickListener(v -> navegationBar.pillPress());
        navegationBarLayout.findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }

    private void getExperienceNames() throws ExecutionException, InterruptedException {
        String pillReportsUrl = "https://psychonautwiki.org/wiki/Experience_index";
        LinearLayout contentLayout = findViewById(R.id.experiences);
        TextView experienceReportsLabel = findViewById(R.id.experienceReportsLabel);
        int dividerHeight = (int) (getResources().getDisplayMetrics().density * 10);
        final Typeface manjari = ResourcesCompat.getFont(this, R.font.manjari_bold);

        WebScraper webScraper = new WebScraper();
        String html = webScraper.execute(pillReportsUrl).get();
        ExperienceNameScraper nameScraper = new ExperienceNameScraper();
        Pair<ArrayList<String>, ArrayList<String>> reports = nameScraper.execute(html, substanceName).get();

        if ((reports != null) && !reports.first.isEmpty()) {
            experienceReportsLabel.setTypeface(manjari);
            for (int i = 0; i < reports.first.size(); i++) {
                Button effectText = new Button(new ContextThemeWrapper(this, R.style.EffectLabel), null, 0);
                effectText.setTypeface(manjari);
                effectText.setText(reports.first.get(i).toLowerCase());
                effectText.setTag(reports.second.get(i));
                contentLayout.addView(effectText);
                ImageView divider = new ImageView(this);
                divider.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dividerHeight));
                contentLayout.addView(divider);

                effectText.setOnClickListener(v -> {
                    Intent intent = new Intent(SubstanceInfo.this, ExperienceReport.class);
                    intent.putExtra("experienceReportName", effectText.getText());
                    intent.putExtra("experienceReportUrl", effectText.getTag().toString());
                    startActivity(intent);
                });

            }
        } else {
            ((ViewGroup) findViewById(R.id.contentLayout)).removeView(experienceReportsLabel);
        }
    }

    protected void callOdPage(){
        Intent intent = new Intent(SubstanceInfo.this, OverdoseInfo.class);
        ArrayList<String> className = substanceObject.getSubstanceClass().getPsychoactive();
        if (substanceObject.getName().toLowerCase().equals("alcohol") ){
            className.add(substanceObject.getName());
        } else if (substanceObject.getName().toLowerCase().equals("ghb")
                || substanceObject.getName().toLowerCase().equals("gbl")){
            className.add("ghb and gbl");
        }
        intent.putExtra("substanceClasses", className);
        startActivity(intent);
    }

    protected void getDuration(View view, int roaNo) {
        final Typeface manjari = ResourcesCompat.getFont(this, R.font.manjari_bold);

        if (substanceObject.getRoas() != null && substanceObject.getRoas().size() > 0) {
            DurationObject durationObject = substanceObject.getRoas().get(roaNo).getDuration();

            TextView onset = view.findViewById(R.id.onset);
            if (durationObject.getOnset() != null) {
                onset.setText(String.format("onset\n%s", durationObject.getOnset().toString()));
                onset.setTypeface(manjari);
            } else {
                onset.setVisibility(View.INVISIBLE);
            }

            TextView comeup = view.findViewById(R.id.comeup);
            if (durationObject.getComeup() != null) {
                comeup.setText(String.format("comeup\n%s", durationObject.getComeup().toString()));
                comeup.setTypeface(manjari);
            } else {
                comeup.setVisibility(View.INVISIBLE);
            }

            TextView peak = view.findViewById(R.id.peak);
            if (durationObject.getPeak() != null) {
                peak.setText(String.format("peak\n%s", durationObject.getPeak().toString()));
                peak.setTypeface(manjari);
            } else {
                peak.setVisibility(View.INVISIBLE);
            }

            TextView offset = view.findViewById(R.id.offset);
            if (durationObject.getOffset() != null) {
                offset.setText(String.format("offset\n%s", durationObject.getOffset().toString()));
                offset.setTypeface(manjari);
            } else {
                offset.setVisibility(View.INVISIBLE);
            }

            TextView aftereffects = view.findViewById(R.id.aftereffects);
            if (durationObject.getAfterglow() != null) {
                aftereffects.setText(String.format("after effects\n%s", durationObject.getAfterglow().toString()));
                aftereffects.setTypeface(manjari);
            } else {
                aftereffects.setVisibility(View.INVISIBLE);
            }

            TextView total = view.findViewById(R.id.total);
            if (durationObject.getTotal() != null) {
                total.setText(String.format("total\n%s", durationObject.getTotal().toString()));
                total.setTypeface(manjari);
            } else {
                total.setVisibility(View.INVISIBLE);
            }
        }

    }

    protected void getSubstanceInfo() throws ExecutionException, InterruptedException {
        APIClient apiClient = new APIClient();
        QueryBuilder queryBuilder = new QueryBuilder();
        String query = queryBuilder.queryByName(substanceName).withName().withRoas()
                .withEffects().withInteractions().withToxicity().getQuery();

        try {
            substanceObject = apiClient.execute(query).get().get(0);
            TextView dosageLabel = findViewById(R.id.dosageLabel);
            dosageLabel.setText(String.format("dosage - %s", substanceObject.getRoas().get(0).getName()));
            getDosage(findViewById(R.id.roa1), 0);
            getEffects();
            getInteractions();
            getDuration(findViewById(R.id.roa1), 0);
            getToxicity();
            getExperienceNames();
            if (substanceObject.getRoas().size()>1){
                getDosage(findViewById(R.id.roa2), 1);
                getDuration(findViewById(R.id.roa2), 1);
            } else {
                View roa2 = findViewById(R.id.roa2);
                Space space = findViewById(R.id.space1);
                ViewGroup parent = (ViewGroup) roa2.getParent();
                parent.removeView(roa2);
                parent.removeView(space);
            }
            if (substanceObject.getRoas().size()>2){
                getDosage(findViewById(R.id.roa3), 2);
                getDuration(findViewById(R.id.roa3), 2);
            } else {
                View roa3 = findViewById(R.id.roa3);
                Space space = findViewById(R.id.space2);
                ViewGroup parent = (ViewGroup) roa3.getParent();
                parent.removeView(roa3);
                parent.removeView(space);
            }

        } catch (IndexOutOfBoundsException e) {
            String alertMessage = "sorry, the substance could not be found";
            Toast toast = Toast.makeText(this, alertMessage, Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
    }

    protected void getToxicity() {
        final Typeface manjari = ResourcesCompat.getFont(this, R.font.manjari_bold);
        ArrayList<String> toxicity = substanceObject.getToxicity();
        TextView toxicityList = findViewById(R.id.toxicity);

        if (toxicity != null && toxicity.size() > 0) {

            for (int i = 0; i < toxicity.size(); i++) {
                toxicityList.append(toxicity.get(i));
                if (i != toxicity.size() - 1)
                    toxicityList.append("\n");
            }

            toxicityList.setTypeface(manjari);
        } else {
            // remove toxicity info
            ViewGroup contentLayout = findViewById(R.id.contentLayout);
            contentLayout.removeView(toxicityList);
            TextView toxicityLabel = findViewById(R.id.toxicityLabel);
            contentLayout.removeView(toxicityLabel);
        }

    }

    protected void getInteractions() {
        final Typeface manjari = ResourcesCompat.getFont(this, R.font.manjari_bold);
        TextView unsafeLabel = findViewById(R.id.unsafeLabel);
        unsafeLabel.setTypeface(manjari);
        ArrayList<SubstanceObject> unsafeInteractions = substanceObject.getUnsafeInteractions();
        boolean visible = true;
        LinearLayout unsafeLayout = findViewById(R.id.unsafeInteractions);
        if (unsafeInteractions != null) {
            StringBuilder unsafeString = new StringBuilder();
            for (int i = 0; i < unsafeInteractions.size(); i++) {
                unsafeString.append(unsafeInteractions.get(i).getName().toLowerCase());
                if (i != unsafeInteractions.size() - 1) {
                    unsafeString.append("\n");
                }
            }
            TextView effectText = new TextView(new ContextThemeWrapper(this, R.style.EffectLabel), null, 0);
            effectText.setTypeface(manjari);
            effectText.setText(unsafeString.toString());
            unsafeLayout.addView(effectText);
        } else {
            visible = false;
        }

        ArrayList<SubstanceObject> dangerousInteractions = substanceObject.getDangerousInteractions();
        LinearLayout dangerousLayout = findViewById(R.id.dangerousInteractions);
        TextView dangerousLabel = findViewById(R.id.dangerousLabel);
        dangerousLabel.setTypeface(manjari);
        if (dangerousInteractions != null) {
            StringBuilder dangerousString = new StringBuilder();
            for (int i = 0; i < dangerousInteractions.size(); i++) {
                dangerousString.append(dangerousInteractions.get(i).getName().toLowerCase());
                if (i != dangerousInteractions.size() - 1) {
                    dangerousString.append("\n");
                }
            }
            TextView effectText2 = new TextView(new ContextThemeWrapper(this, R.style.EffectLabel), null, 0);
            effectText2.setTypeface(manjari);
            effectText2.setText(dangerousString.toString());
            dangerousLayout.addView(effectText2);
        } else {
            // remove interactions view if there are no interactions listed
            if (!visible) {
                LinearLayout interactions = findViewById(R.id.interactions);
                TextView interactionsLabel = findViewById(R.id.interactionsLabel);
                ViewGroup contentLayout = findViewById(R.id.contentLayout);
                contentLayout.removeView(interactions);
                contentLayout.removeView(interactionsLabel);
            }
        }
    }

    protected void getEffects() {
        int dividerHeight = (int) (getResources().getDisplayMetrics().density * 10);
        final Typeface manjari = ResourcesCompat.getFont(this, R.font.manjari_bold);

        LinearLayout contentLayout = findViewById(R.id.effects);
        for (EffectObject effect : substanceObject.getEffects()) {
            TextView effectText = new TextView(new ContextThemeWrapper(this, R.style.EffectLabel), null, 0);
            effectText.setTypeface(manjari);
            effectText.setText(effect.getName().toLowerCase());
            contentLayout.addView(effectText);
            ImageView divider = new ImageView(this);
            divider.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dividerHeight));
            contentLayout.addView(divider);
        }
    }

    protected void getDosage(View view, int roaNo) {
        final Typeface manjari = ResourcesCompat.getFont(this, R.font.manjari_bold);
        RoaObject mainRoa = substanceObject.getRoas().get(roaNo);
        view.setTag(mainRoa.getName());

        if (mainRoa.getDosage() != null) {

            String units = mainRoa.getDosage().getUnits();

            TextView lightText = view.findViewById(R.id.lightStrength);
            lightText.setTypeface(manjari);
            TextView commonText = view.findViewById(R.id.commonStrength);
            commonText.setTypeface(manjari);
            TextView strongText = view.findViewById(R.id.strongStrength);
            strongText.setTypeface(manjari);

            UnitsObject lightUnitsDosage = mainRoa.getDosage().getLight();
            TextView lightDosage = view.findViewById(R.id.lightDosage);
            lightDosage.setTypeface(manjari);
            if (lightUnitsDosage != null) {
                lightDosage.setText(String.format("%s%s", lightUnitsDosage.toString(), units));
            }

            UnitsObject commonUnitsDosage = mainRoa.getDosage().getCommon();
            TextView commonDosage = view.findViewById(R.id.commonDosage);
            commonDosage.setTypeface(manjari);
            if (commonUnitsDosage != null) {
                commonDosage.setText(String.format("%s%s", commonUnitsDosage.toString(), units));
            }

            UnitsObject strongUnitsDosage = mainRoa.getDosage().getStrong();
            TextView strongDosage = view.findViewById(R.id.strongDosage);
            strongDosage.setTypeface(manjari);
            if (strongUnitsDosage != null) {
                strongDosage.setText(String.format("%s%s", strongUnitsDosage.toString(), units));
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
