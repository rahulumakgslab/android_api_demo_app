package com.example.sondeapitestapp.model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ScoreRequest {
    private String filePath;
    private String measureName;
    private String questionnaireResponseId;

    private ArrayList<Infer> infer;

    public ScoreRequest(String userIdentifier, String filePath, String measureName) {

        this.filePath = filePath;
        this.measureName = measureName;
        this.infer=new ArrayList<>();
        this.infer.add(new Infer("Acoustic","v3"));
    }

    public void setQuestionnaireResponseId(String questionnaireResponseId) {
        this.questionnaireResponseId = questionnaireResponseId;
    }
}
