package com.example.sondeapitestapp.model;

import java.util.ArrayList;

public class QuestionnaireResponse {
    private String id;
    private String title;
    private String language;
    private ArrayList<Question> questions;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLanguage() {
        return language;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }
}
