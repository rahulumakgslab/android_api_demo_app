package com.example.sondeapitestapp.model;

public class S3FilePathResponse {
    private String requestId;
    private String signedURL;
    private String filePath;

    public String getRequestId() {
        return requestId;
    }

    public String getSignedURL() {
        return signedURL;
    }

    public String getFilePath() {
        return filePath;
    }
}
