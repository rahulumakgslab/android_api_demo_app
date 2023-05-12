package com.example.sondeapitestapp.model;

public class S3FilePathRequest {
    private String fileType;
    private String countryCode;
    private String userIdentifier;

    public S3FilePathRequest(String fileType, String countryCode, String userIdentifier) {
        this.fileType = fileType;
        this.countryCode = countryCode;
        this.userIdentifier = userIdentifier;
    }
}
