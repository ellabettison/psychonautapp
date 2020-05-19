package com.example.psychapp.api.QueryObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class SubstanceObject {
    
    @JsonProperty("name")
    private String name;
    @JsonProperty("effects")
    private ArrayList<EffectObject> effects;
    @JsonProperty("roas")
    private ArrayList<RoaObject> roas;
    @JsonProperty("unsafeInteractions")
    private ArrayList<SubstanceObject> unsafeInteractions;
    @JsonProperty("dangerousInteractions")
    private ArrayList<SubstanceObject> dangerousInteractions;
    @JsonProperty("toxicity")
    private ArrayList<String> toxicity;

    public ArrayList<String> getToxicity() {
        return toxicity;
    }

    public ArrayList<SubstanceObject> getUnsafeInteractions() {
        return unsafeInteractions;
    }

    public ArrayList<SubstanceObject> getDangerousInteractions() {
        return dangerousInteractions;
    }

    public String getName() {
        return name;
    }

    public ArrayList<EffectObject> getEffects() {
        return effects;
    }

    public ArrayList<RoaObject> getRoas() {
        return roas;
    }
    
    @Override
    public String toString(){
        return (String.format("Name: %s\nEffects: %s\nRoas: %s\n", name, effects, roas));
    }
}
