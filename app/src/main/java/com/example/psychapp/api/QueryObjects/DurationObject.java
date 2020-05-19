package com.example.psychapp.api.QueryObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DurationObject {
    @JsonProperty("afterglow")
    private UnitsObject afterglow;
    @JsonProperty("comeup")
    private UnitsObject comeup;
    @JsonProperty("duration")
    private UnitsObject duration;
    @JsonProperty("offset")
    private UnitsObject offset;
    @JsonProperty("onset")
    private UnitsObject onset;
    @JsonProperty("peak")
    private UnitsObject peak;
    @JsonProperty("total")
    private UnitsObject total;

    public UnitsObject getAfterglow() {
        return afterglow;
    }

    public UnitsObject getComeup() {
        return comeup;
    }

    public UnitsObject getOffset() {
        return offset;
    }

    public UnitsObject getOnset() {
        return onset;
    }

    public UnitsObject getPeak() {
        return peak;
    }

    public UnitsObject getTotal() {
        return total;
    }

    public UnitsObject getDuration() {
        return duration;
    }
}
