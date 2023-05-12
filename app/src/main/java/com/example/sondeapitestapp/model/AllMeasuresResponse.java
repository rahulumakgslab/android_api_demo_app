package com.example.sondeapitestapp.model;

import java.util.ArrayList;

public class AllMeasuresResponse {

    private String requestId;
    private ArrayList<Measures> measures;

    public String getRequestId() {
        return requestId;
    }

    public ArrayList<Measures> getMeasures() {
        return measures;
    }
}
