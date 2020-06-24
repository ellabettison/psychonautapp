package com.example.psychapp.experiencereports.Objects;

import java.io.Serializable;

public class ExperienceSectionObject implements Serializable {
    private final String title;
    private final String body;

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
    
    public ExperienceSectionObject(String title, String body) {
        this.title = title;
        this.body = body;
    }
}
