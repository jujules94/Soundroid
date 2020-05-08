package com.example.soundroid.db;

import java.util.ArrayList;
import java.util.List;

public class Tracklist implements Tracklistable {

    private final String hash;
    private final String name;
    private final List<Tracklistable> tracklistables;

    public Tracklist(String name) {
        this(name, name, new ArrayList<Tracklistable>());
    }
    public Tracklist(String hash, String name, ArrayList<Tracklistable> tracklistables) {
        this.hash = hash;
        this.name = name;
        this.tracklistables = tracklistables;
    }

    @Override
    public String getHash() {
        return hash;
    }

    public String getName() {
        return name;
    }

    public List<Tracklistable> getTracklistables() {
        return tracklistables;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name + " {");
        for (Tracklistable tracklistable : tracklistables) {
            builder.append(" " + tracklistable.toString() + " ");
        }
        builder.append("}");
        return builder.toString();
    }

}
