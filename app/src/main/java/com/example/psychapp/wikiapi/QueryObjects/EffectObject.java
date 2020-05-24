package com.example.psychapp.wikiapi.QueryObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EffectObject {
    
    @JsonProperty("name")
    String name;

    public String getName() {
        return name;
    }
    
    @Override
    public String toString(){
        return name;
    }
    
}
