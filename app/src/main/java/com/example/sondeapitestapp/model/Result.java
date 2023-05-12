
package com.example.sondeapitestapp.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("filePath")
    @Expose
    private String filePath;
    @SerializedName("measureName")
    @Expose
    private String measureName;
    @SerializedName("userIdentifier")
    @Expose
    private String userIdentifier;
    @SerializedName("inferredAt")
    @Expose
    private String inferredAt;
    @SerializedName("inference")
    @Expose
    private List<Inference> inference;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Result() {
    }

    /**
     * 
     * @param inferredAt
     * @param inference
     * @param userIdentifier
     * @param filePath
     * @param id
     * @param type
     * @param measureName
     */
    public Result(String id, String type, String filePath, String measureName, String userIdentifier, String inferredAt, List<Inference> inference) {
        super();
        this.id = id;
        this.type = type;
        this.filePath = filePath;
        this.measureName = measureName;
        this.userIdentifier = userIdentifier;
        this.inferredAt = inferredAt;
        this.inference = inference;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMeasureName() {
        return measureName;
    }

    public void setMeasureName(String measureName) {
        this.measureName = measureName;
    }

    public String getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public String getInferredAt() {
        return inferredAt;
    }

    public void setInferredAt(String inferredAt) {
        this.inferredAt = inferredAt;
    }

    public List<Inference> getInference() {
        return inference;
    }

    public void setInference(List<Inference> inference) {
        this.inference = inference;
    }

}
