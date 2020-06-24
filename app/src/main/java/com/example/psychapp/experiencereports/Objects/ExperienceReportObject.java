package com.example.psychapp.experiencereports.Objects;

import java.io.Serializable;
import java.util.ArrayList;

public class ExperienceReportObject implements Serializable {
    private final String title;
    private final ArrayList<ExperienceSectionObject> report;

    public String getTitle() {
        return title;
    }
    
    public ExperienceReportObject(String title, ArrayList<ExperienceSectionObject> report) {
        this.title = title;
        this.report = report;
    }

    public ArrayList<ExperienceSectionObject> getReport() {
        return report;
    }
    
}
