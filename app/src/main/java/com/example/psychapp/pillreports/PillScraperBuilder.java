package com.example.psychapp.pillreports;

public class PillScraperBuilder {
    
    private int pp = 10;
    private String name = "";
    private String colour = "";
    private String logo = "";
    private String region = "all";
    private String sub_region = "all";
    private String state = "";

    public PillScraperBuilder setPp(int pp) {
        this.pp = pp;
        return this;
    }

    public PillScraperBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public PillScraperBuilder setColour(String colour) {
        this.colour = colour;
        return this;
    }

    public PillScraperBuilder setLogo(String logo) {
        this.logo = logo;
        return this;
    }

    public PillScraperBuilder setRegion(String region) {
        this.region = region;
        return this;
    }

    public PillScraperBuilder setSub_region(String sub_region) {
        this.sub_region = sub_region;
        return this;
    }

    public PillScraperBuilder setState(String state) {
        this.state = state;
        return this;
    }

    public PillScraper createPillScraper() {
        return new PillScraper(pp, name, colour, logo, region, sub_region, state);
    }
}