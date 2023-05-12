package com.example.sondeapitestapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class UserAnswer implements Parcelable {
    private Integer optionIndex;
    private ArrayList<Integer> optionIndexes;
    private String response;
    private Boolean isSkipped;

    public UserAnswer() {

    }

    protected UserAnswer(Parcel in) {
        if (in.readByte() == 0) {
            optionIndex = null;
        } else {
            optionIndex = in.readInt();
        }
        optionIndexes = in.readArrayList(Integer.class.getClassLoader());
        response = in.readString();
        byte tmpIsSkipped = in.readByte();
        isSkipped = tmpIsSkipped == 0 ? null : tmpIsSkipped == 1;
    }

    public static final Creator<UserAnswer> CREATOR = new Creator<UserAnswer>() {
        @Override
        public UserAnswer createFromParcel(Parcel in) {
            return new UserAnswer(in);
        }

        @Override
        public UserAnswer[] newArray(int size) {
            return new UserAnswer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (optionIndex == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(optionIndex);
        }
        parcel.writeList(getOptionIndexes());
        parcel.writeString(response);
        parcel.writeByte((byte) (isSkipped == null ? 0 : isSkipped ? 1 : 2));
    }

    public Integer getOptionIndex() {
        return optionIndex;
    }

    public void setOptionIndex(Integer optionIndex) {
        this.optionIndex = optionIndex;
    }

    public ArrayList<Integer> getOptionIndexes() {
        return optionIndexes;
    }

    public void setOptionIndexes(ArrayList<Integer> optionIndexes) {
        this.optionIndexes = optionIndexes;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Boolean getSkipped() {
        return isSkipped;
    }

    public void setSkipped(Boolean skipped) {
        isSkipped = skipped;
    }
}