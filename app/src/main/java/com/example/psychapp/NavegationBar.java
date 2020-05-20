package com.example.psychapp;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.core.content.ContextCompat;

import com.example.psychapp.activities.HomeScreen;
import com.example.psychapp.activities.OverdoseInfo;

public class NavegationBar {

    final int BACK_INDEX = 0;
    final int HOME_INDEX = 0;
    final int PILL_INDEX = 0;
    final int OD_INDEX = 0;

    private Context packagageContext;
    private ViewGroup navegationBar;

    public NavegationBar(Context packagageContext, ViewGroup navegationBar) {
        this.packagageContext = packagageContext;
        this.navegationBar = navegationBar;
        switch (packagageContext.getPackageName()){
            case("HomeScreen"):
                View image = ((ViewGroup) navegationBar.getChildAt(0)).getChildAt(HOME_INDEX);
                image.setBackground(ContextCompat.getDrawable(packagageContext, R.drawable.home));
                //TODO: set width using weird width method and weight = 0
            case("OdScreen"):
                ((ViewGroup) navegationBar.getChildAt(0)).getChildAt(OD_INDEX)
                        .setBackground(ContextCompat.getDrawable(packagageContext, R.drawable.overdose));
        }
    }

    public void homePress(){
        Intent intent = new Intent(packagageContext, HomeScreen.class);
        packagageContext.startActivity(intent);
    }

    public void odPress(){
        Intent intent = new Intent(packagageContext, OverdoseInfo.class);
        packagageContext.startActivity(intent);
    }

}
