package com.example.psychapp.pillreports.Objects;

import android.graphics.drawable.Drawable;
import android.media.Image;

import java.awt.*;
import java.util.Date;

public class PillObject {
    
    private final String name;
    private final String location;
    private final String date;
    private final String suspectContents;
    private final String shape;
    private final String logo;
    private final String colour;
    private final Drawable image;

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getSuspectContents() {
        return suspectContents;
    }

    public String getShape() {
        return shape;
    }

    public String getLogo() {
        return logo;
    }

    public String getColour() {
        return colour;
    }

    public Drawable getImage() {
        return image;
    }

    public PillObject(String name, String location, String date, String suspectContents, String shape, String logo, String colour, Drawable image) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.suspectContents = suspectContents;
        this.shape = shape;
        this.logo = logo;
        this.colour = colour;
        this.image = image;
    }
}
