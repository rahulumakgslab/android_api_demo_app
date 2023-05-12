package com.example.sondeapitestapp.model;

import java.util.ArrayList;

public class Questionnaire {
    private String id;
    private String title;
    private ArrayList<String> languages;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<String> getLanguages() {
        return languages;
    }
}
