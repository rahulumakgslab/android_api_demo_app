package com.example.sondeapitestapp.model;

import java.util.ArrayList;

public class Measures {

    private String id;
    private String name;
    private ArrayList<Questionnaire> questionnaires;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Questionnaire> getQuestionnaires() {
        return questionnaires;
    }
}
