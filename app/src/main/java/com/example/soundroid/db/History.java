package com.example.soundroid.db;

public class History {

    private final long date;
    private final String hash;

    public History(long date, String hash) {
        this.date = date;
        this.hash = hash;
    }

    public long getDate() {
        return date;
    }

    public String getHash() {
        return hash;
    }

}
