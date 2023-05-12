package com.example.sondeapitestapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Question implements Parcelable {

    private String type;
    private String text;
    private boolean isSkippable;
    private ArrayList<Option> options;
    private String inputDataType;

    protected Question(Parcel in) {
        type = in.readString();
        text = in.readString();
        isSkippable = in.readByte() != 0;
        options = in.createTypedArrayList(Option.CREATOR);
        inputDataType = in.readString();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public boolean isSkippable() {
        return isSkippable;
    }

    public ArrayList<Option> getOptions() {
        return options;
    }

    public String getInputDataType() {
        return inputDataType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(type);
        parcel.writeString(text);
        parcel.writeByte((byte) (isSkippable ? 1 : 0));
        parcel.writeTypedList(options);
        parcel.writeString(inputDataType);
    }
}
