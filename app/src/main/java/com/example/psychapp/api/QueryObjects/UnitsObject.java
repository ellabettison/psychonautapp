package com.example.psychapp.api.QueryObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UnitsObject {
    @JsonProperty("min")
    private int min;
    @JsonProperty("max")
    private int max;
    @JsonProperty("units")
    private String units;

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public String getUnits() {
        return units;
    }
}
