package com.example.notschoolofdrums.Adapters;

import android.net.Uri;

import com.google.firebase.Timestamp;

public class NewsForAdapter {

    String text;
    String timestamp;
    Uri imageUri;

    public NewsForAdapter(){}

    public NewsForAdapter(String text, String timestamp, Uri imageUri) {
        this.text = text;
        this.timestamp = timestamp;
        this.imageUri = imageUri;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}
