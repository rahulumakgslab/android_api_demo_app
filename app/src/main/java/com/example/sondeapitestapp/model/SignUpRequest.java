package com.example.sondeapitestapp.model;

public class SignUpRequest {
    private String yearOfBirth;
    private String gender;
    private String language;
    private Device device;
//"device": {
//    "type": "MOBILE",
//    "manufacturer": "VIVO"
//  }
    public SignUpRequest(String yearOfBirth, String gender, String language) {
        this.yearOfBirth = yearOfBirth;
        this.gender = gender;
        this.language = language;

        this.device=new Device("MOBILE","SAMSUNG");
    }
}
