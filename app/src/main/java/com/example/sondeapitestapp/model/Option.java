package com.example.sondeapitestapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Option implements Parcelable {
    private String text;
    private String score;

    protected Option(Parcel in) {
        text = in.readString();
        score = in.readString();
    }

    public static final Creator<Option> CREATOR = new Creator<Option>() {
        @Override
        public Option createFromParcel(Parcel in) {
            return new Option(in);
        }

        @Override
        public Option[] newArray(int size) {
            return new Option[size];
        }
    };

    public String getText() {
        return text;
    }

    public String getScore() {
        return score;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(text);
        parcel.writeString(score);
    }
}
