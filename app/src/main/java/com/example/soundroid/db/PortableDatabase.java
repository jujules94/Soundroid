package com.example.soundroid.db;

import com.google.gson.Gson;
import java.util.ArrayList;

public class PortableDatabase {

    private final ArrayList<Track> tracks;
    private final ArrayList<Tracklist> tracklists;
    private final ArrayList<TracklistLink> tracklistLinks;

    public PortableDatabase(ArrayList<Track> tracks, ArrayList<Tracklist> tracklists, ArrayList<TracklistLink> tracklistLinks) {
        this.tracks = tracks;
        this.tracklists = tracklists;
        this.tracklistLinks = tracklistLinks;
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

    public ArrayList<TracklistLink> getTracklistLinks() {
        return tracklistLinks;
    }

}
