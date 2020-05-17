package com.example.soundroid.db;

import com.google.gson.Gson;
import java.util.ArrayList;

public class PortableDatabase {

    private final ArrayList<Track> tracks;
    private final ArrayList<Tracklist> tracklists;

    public PortableDatabase(ArrayList<Track> tracks, ArrayList<Tracklist> tracklists) {
        this.tracks = tracks;
        this.tracklists = tracklists;
    }

    public static PortableDatabase fromJSON(String json) {
        return new Gson().fromJson(json, PortableDatabase.class);
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }

    public ArrayList<Tracklist> getTracklists() {
        return tracklists;
    }

}
