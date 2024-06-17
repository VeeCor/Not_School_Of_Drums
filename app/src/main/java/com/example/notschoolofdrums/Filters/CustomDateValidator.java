package com.example.notschoolofdrums.Filters;

import android.os.Parcel;
import android.os.Parcelable;


import com.google.android.material.datepicker.CalendarConstraints;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class CustomDateValidator implements CalendarConstraints.DateValidator {

    private final long startDate;
    private final long endDate;
    private final List<Integer> blockedDays;

    public CustomDateValidator(long startDate, long endDate, List<Integer> blockedDays) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.blockedDays = blockedDays;
    }

    @Override
    public boolean isValid(long date) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+03:00"));
        calendar.setTimeInMillis(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (date < startDate || date > endDate) {
            return false;
        }

        return blockedDays.contains(dayOfWeek);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(startDate);
        dest.writeLong(endDate);
        dest.writeList(blockedDays);
    }

    public static final Parcelable.Creator<CustomDateValidator> CREATOR = new Parcelable.Creator<CustomDateValidator>() {
        @Override
        public CustomDateValidator createFromParcel(Parcel source) {
            long startDate = source.readLong();
            long endDate = source.readLong();
            List<Integer> blockedDays = source.readArrayList(Integer.class.getClassLoader());
            return new CustomDateValidator(startDate, endDate, blockedDays);
        }

        @Override
        public CustomDateValidator[] newArray(int size) {
            return new CustomDateValidator[size];
        }
    };
}
