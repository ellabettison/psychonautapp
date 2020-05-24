package com.example.psychapp.wikiapi.QueryObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.DecimalFormat;

public class UnitsObject {
    @JsonProperty("min")
    private float min;
    @JsonProperty("max")
    private float max;
    @JsonProperty("units")
    private String units;

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    private String getUnits() {
        if (units!=null) {
            switch (units) {
                case "seconds":
                    return "secs";
                case "minutes":
                    return "mins";
                case "hours":
                    return "hrs";
                default:
                    return units;
            }
        } else {
            return "";
        }
    }

    @Override
    public String toString(){
        DecimalFormat df = new DecimalFormat("###.#");
        return (String.format("%s - %s %s",
                df.format(min), df.format(max), getUnits()));
    }
}
