package com.example.psychapp.api.QueryObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.JSONArray;

import java.util.ArrayList;

public class SubstanceClassObject {

    @JsonProperty("psychoactive")
    private ArrayList<String> psychoactive;

    public ArrayList<String> getPsychoactive() {
        return psychoactive;
    }

}
