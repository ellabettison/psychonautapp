package com.example.psychapp.wikiapi.QueryObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RoaObject {
    @JsonProperty("name")
    private String name;
    @JsonProperty("dose")
    private DosageObject dosage;
    @JsonProperty("duration")
    private DurationObject duration;

    public DurationObject getDuration() {
        return duration;
    }

    public DosageObject getDosage() {
        return dosage;
    }

    public String getName() {
        return name;
    }
    
    @Override
    public String toString(){
        return name;
    }
}
