
package com.example.sondeapitestapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VoiceFeature {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("score")
    @Expose
    private String score;

    /**
     * No args constructor for use in serialization
     * 
     */
    public VoiceFeature() {
    }

    /**
     * 
     * @param score
     * @param name
     */
    public VoiceFeature(String name, String score) {
        super();
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

}
