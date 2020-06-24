package com.example.psychapp.elements;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.Layout;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.psychapp.R;
import com.example.psychapp.activities.ExperienceReportSearch;
import com.example.psychapp.activities.HomeScreen;
import com.example.psychapp.activities.OverdoseSelector;
import com.example.psychapp.activities.PillReports;
import com.example.psychapp.activities.Splash;

public class NavegationBar {

    private AppCompatActivity packagageContext;
    private ViewGroup navegationBar;
    private boolean erowidEnabled = false;


    public NavegationBar(AppCompatActivity packagageContext, ViewGroup navegationBar) {

        int BUTTON_WIDTH = 50;

        Resources r = packagageContext.getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                BUTTON_WIDTH,
                r.getDisplayMetrics()
        );

        this.packagageContext = packagageContext;
        this.navegationBar = navegationBar;
        ImageButton image;
        //TODO: extract duplication
        System.out.printf("\n\n ##################### PACKAGE NAME %s \n\n", packagageContext.getLocalClassName());
        switch (packagageContext.getLocalClassName()) {
            case ("com.example.psychapp.activities.HomeScreen"):
                image = navegationBar.findViewById(R.id.home_button);
                image.setImageResource(R.drawable.home);
                image.setImageTintList(null);

                ViewGroup.LayoutParams params = image.getLayoutParams();
                params.width = (int) px;
                image.setLayoutParams(params);
                image.requestLayout();

                break;
            //TODO: set width using weird width method and weight = 0
            case ("com.example.psychapp.activities.OverdoseSelector"):
                image = navegationBar.findViewById(R.id.od_button);
                image.setImageResource(R.drawable.overdose2);
                image.setImageTintList(null);

                ViewGroup.LayoutParams params2 = image.getLayoutParams();
                params2.width = (int) px;
                image.setLayoutParams(params2);
                image.requestLayout();
                break;

            case ("com.example.psychapp.activities.PillReports"):
                image = navegationBar.findViewById(R.id.pill_button);
                image.setImageResource(R.drawable.pill_exp);
                image.setImageTintList(null);

                ViewGroup.LayoutParams params3 = image.getLayoutParams();
                params3.width = (int) px;
                image.setLayoutParams(params3);
                image.requestLayout();
                break;
        }

        navegationBar.findViewById(R.id.back_button).setOnClickListener(v-> packagageContext.finish());
        navegationBar.findViewById(R.id.home_button).setOnClickListener(v -> homePress());
        navegationBar.findViewById(R.id.od_button).setOnClickListener(v -> odPress());
        navegationBar.findViewById(R.id.pill_button).setOnClickListener(v -> pillPress());
        navegationBar.findViewById(R.id.experienceButton).setOnClickListener(v -> experiencePress());

    }

    private void homePress(){
        Intent intent = new Intent(packagageContext, HomeScreen.class);
        packagageContext.startActivity(intent);
    }

    private void odPress(){
        Intent intent = new Intent(packagageContext, OverdoseSelector.class);
        packagageContext.startActivity(intent);
    }

    private void pillPress(){
        Intent intent = new Intent(packagageContext, Splash.class);
        packagageContext.startActivity(intent);
    }

    private void experiencePress(){
        Intent intent = new Intent(packagageContext, ExperienceReportSearch.class);
        packagageContext.startActivity(intent);
    }

}
