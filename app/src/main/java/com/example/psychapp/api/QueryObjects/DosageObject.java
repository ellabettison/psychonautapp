package com.example.psychapp.api.QueryObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DosageObject {
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
