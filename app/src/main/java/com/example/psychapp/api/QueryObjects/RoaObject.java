package com.example.psychapp.api.QueryObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RoaObject {
    @JsonProperty("name")
    private String name;

    public String getName() {
        return name;
    }
    
    @Override
    public String toString(){
        return name;
    }
}
