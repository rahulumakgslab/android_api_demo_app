
package com.example.sondeapitestapp.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Inference {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("score")
    @Expose
    private Score score;
    @SerializedName("voiceFeatures")
    @Expose
    private List<VoiceFeature> voiceFeatures;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Inference() {
    }

    /**
     * 
     * @param score
     * @param voiceFeatures
     * @param type
     * @param version
     */
    public Inference(String type, String version, Score score, List<VoiceFeature> voiceFeatures) {
        super();
        this.type = type;
        this.version = version;
        this.score = score;
        this.voiceFeatures = voiceFeatures;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public List<VoiceFeature> getVoiceFeatures() {
        return voiceFeatures;
    }

    public void setVoiceFeatures(List<VoiceFeature> voiceFeatures) {
        this.voiceFeatures = voiceFeatures;
    }

}
