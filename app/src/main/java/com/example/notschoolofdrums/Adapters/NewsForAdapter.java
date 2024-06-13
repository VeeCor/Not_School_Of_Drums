package com.example.notschoolofdrums.Adapters;

import android.net.Uri;

public class NewsForAdapter {

    String postText, dateTime;
    Uri imageUri;

    public NewsForAdapter() {}

    public NewsForAdapter(String PostText, String timestamp, Uri imageUri) {
        this.postText = PostText;
        this.dateTime = timestamp;
        this.imageUri = imageUri;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String text) {
        this.postText = text;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}