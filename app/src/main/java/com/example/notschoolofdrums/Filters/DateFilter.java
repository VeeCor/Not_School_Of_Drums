package com.example.notschoolofdrums.Filters;

import android.text.InputFilter;
import android.text.Spanned;

public class DateFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        StringBuilder builder = new StringBuilder(dest);
        builder.replace(dstart, dend, source.subSequence(start, end).toString());

        if (builder.length() > 10) {
            return "";
        }

        for (int i = 0; i < builder.length(); i++) {
            if (i == 2 || i == 5) {
                if (builder.charAt(i) != '-') {
                    return "";
                }
            } else if (!Character.isDigit(builder.charAt(i))) {
                return "";
            }
        }

        return null;
    }
}
