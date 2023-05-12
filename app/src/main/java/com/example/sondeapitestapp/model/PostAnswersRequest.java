package com.example.sondeapitestapp.model;

import com.google.gson.annotations.SerializedName;

public class PostAnswersRequest {

    @SerializedName("questionnaire")
    private QuestionnaireUserResponse questionnaire;

    public PostAnswersRequest(QuestionnaireUserResponse questionnaire) {
        this.questionnaire = questionnaire;
    }
}
