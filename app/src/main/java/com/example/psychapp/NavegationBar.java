package com.example.psychapp;

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

import com.example.psychapp.activities.HomeScreen;
import com.example.psychapp.activities.OverdoseSelector;

public class NavegationBar {

    private AppCompatActivity packagageContext;
    private ViewGroup navegationBar;

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
        switch (packagageContext.getLocalClassName()){
            case("com.example.psychapp.activities.HomeScreen"):
                image = navegationBar.findViewById(R.id.home_button);
                image.setImageResource(R.drawable.home);
                image.setImageTintList(null);

                ViewGroup.LayoutParams params = image.getLayoutParams();
                params.width = (int) px;
                image.setLayoutParams(params);
                image.requestLayout();

                break;
                //TODO: set width using weird width method and weight = 0
            case("com.example.psychapp.activities.OverdoseSelector"):
                image = navegationBar.findViewById(R.id.od_button);
                image.setImageResource(R.drawable.overdose2);
                image.setImageTintList(null);

                ViewGroup.LayoutParams params2 = image.getLayoutParams();
                params2.width = (int) px;
                image.setLayoutParams(params2);
                image.requestLayout();
                break;
        }
    }

    public void homePress(){
        Intent intent = new Intent(packagageContext, HomeScreen.class);
        packagageContext.startActivity(intent);
    }

    public void odPress(){
        Intent intent = new Intent(packagageContext, OverdoseSelector.class);
        packagageContext.startActivity(intent);
    }

}