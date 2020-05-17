package com.example.soundroid.db;

public class TracklistLink {

    private final String tracklistHash;
    private final String tracklistableHash;

    public TracklistLink(String tracklistHash, String tracklistableHash) {
        this.tracklistHash = tracklistHash;
        this.tracklistableHash = tracklistableHash;
    }

    public String getTracklistHash() {
        return tracklistHash;
    }

    public String getTracklistableHash() {
        return tracklistableHash;
    }

}
