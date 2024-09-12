package com.example.notschoolofdrums.Adapters;

public class EntriesForAdapter {

    private String type, username, chosenClass, consist, level, lesson, time, date;

    public EntriesForAdapter() {}

    public EntriesForAdapter(String type, String username, String chosenClass, String consist, String level, String lesson, String time, String date) {

        this.type = type;
        this.username = username;
        this.chosenClass = chosenClass;
        this.consist = consist;
        this.level = level;
        this.lesson = lesson;
        this.time = time;
        this.date = date;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getChosenClass() { return chosenClass; }
    public void setChosenClass(String chosenClass) { this.chosenClass = chosenClass; }
    public String getConsist() { return consist; }
    public void setConsist(String consist) { this.consist = consist; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getLesson() { return lesson; }
    public void setLesson(String lesson) { this.lesson = lesson; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
