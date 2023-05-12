
package com.example.sondeapitestapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ScoreResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("result")
    @Expose
    private Result result;
    @SerializedName("requestId")
    @Expose
    private String requestId;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ScoreResponse() {
    }

    /**
     * 
     * @param result
     * @param requestId
     * @param status
     */
    public ScoreResponse(String status, Result result, String requestId) {
        super();
        this.status = status;
        this.result = result;
        this.requestId = requestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

}
