package com.example.psychapp.pillreports;

public class PillScraper {
    
    int pp = 20;
    String name = "";
    String colour = "";
    String logo = "";
    String region = "all";
    String sub_region = "all";
    String state = "";

    public PillScraper(int pp, String name, String colour, String logo,
                       String region, String sub_region, String state) {
        
        this.pp = pp;
        this.name = name;
        this.colour = colour;
        this.logo = logo;
        this.region = region;
        this.sub_region = sub_region;
        this.state = state;
    }
    
    public String generatePillScraper(){
        return "https://pillreports.net/index.php?page=search_reports&sent=1&username=" +
                "&name=" + name +
                "&logo=" + logo +
                "&colour=" + colour +
                "&state=" + state +
                "&region=" + region +
                "&sub_region=" + sub_region +
                "&suspected_contents=all" +
                "&rating=Adulterated" +
                "&pp=" + pp+
                "&order=latest";
    }
}
