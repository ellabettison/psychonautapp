package com.example.psychapp.wikiapi.QueryObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class SubstanceClassObject {

    @JsonProperty("psychoactive")
    private ArrayList<String> psychoactive;

    public ArrayList<String> getPsychoactive() {
        return psychoactive;
    }

}
