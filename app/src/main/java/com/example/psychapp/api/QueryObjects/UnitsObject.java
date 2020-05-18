package com.example.psychapp.api.QueryObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    public String getUnits() {
        return units;
    }
}
