package com.example.notschoolofdrums.Filters;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.material.datepicker.CalendarConstraints;

public class CustomDateValidator2 implements CalendarConstraints.DateValidator {

    private final long startDate;
    private final long endDate;

    public CustomDateValidator2(long startDate, long endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public boolean isValid(long date) {
        return !(date < startDate || date >= endDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(startDate);
        dest.writeLong(endDate);
    }

    public static final Parcelable.Creator<CustomDateValidator2> CREATOR = new Parcelable.Creator<CustomDateValidator2>() {
        public CustomDateValidator2 createFromParcel(Parcel in) {
            return new CustomDateValidator2(in.readLong(), in.readLong());
        }

        public CustomDateValidator2[] newArray(int size) {
            return new CustomDateValidator2[size];
        }
    };
}


