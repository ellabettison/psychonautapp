package com.example.psychapp.api.QueryObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DosageObject {
    public String getUnits() {
        return units;
    }

    @JsonProperty("units")
    private String units;
    @JsonProperty("light")
    private UnitsObject light;
    @JsonProperty("common")
    private UnitsObject common;
    @JsonProperty("strong")
    private UnitsObject strong;

    public UnitsObject getLight() {
        return light;
    }

    public UnitsObject getCommon() {
        return common;
    }

    public UnitsObject getStrong() {
        return strong;
    }
}
