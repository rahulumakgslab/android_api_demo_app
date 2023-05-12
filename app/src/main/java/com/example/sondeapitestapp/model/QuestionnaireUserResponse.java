package com.example.sondeapitestapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class QuestionnaireUserResponse {
    private String id;
    private String language;
    private String userIdentifier;
    private String respondedAt;

    @SerializedName("questionResponses")
    private ArrayList<UserAnswer> userAnswers;

    public QuestionnaireUserResponse(String id, String language, String userIdentifier, String respondedAt, ArrayList<UserAnswer> userAnswers) {
        this.id = id;
        this.language = language;
        this.userIdentifier = userIdentifier;
        this.respondedAt = respondedAt;
        this.userAnswers = userAnswers;
    }
}
