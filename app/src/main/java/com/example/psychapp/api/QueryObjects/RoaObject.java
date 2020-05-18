package com.example.psychapp.api.QueryObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RoaObject {
    @JsonProperty("name")
    private String name;
    @JsonProperty("dose")
    private DosageObject dosage;

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
