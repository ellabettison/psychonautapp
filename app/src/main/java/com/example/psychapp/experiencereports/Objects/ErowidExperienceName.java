package com.example.psychapp.experiencereports.Objects;

public class ErowidExperienceName {
    
    private String name;
    private String url;
    private String substances;
    private String date;

    public ErowidExperienceName(String name, String url, String substances, String date) {
        this.name = name;
        this.url = url;
        this.substances = substances;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getSubstances() {
        return substances;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "ErowidExperienceName{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", substances='" + substances + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
