package com.example.sondeapitestapp.model;

public class VoiceFeatureJobResponse {
    private String jobId;
    private String requestId;

    public VoiceFeatureJobResponse(String jobId, String requestId) {
        this.jobId = jobId;
        this.requestId = requestId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
