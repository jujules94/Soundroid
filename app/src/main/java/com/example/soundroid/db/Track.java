package com.example.soundroid.db;

import android.net.Uri;
import android.util.Log;

public class Track implements Tracklistable {

    private final String hash;
    private final String name;
    private final String artist;
    private final String album;
    private final int diskNumber;
    private final int trackNumber;
    private final int bitrate;
    private final long date;
    private final int minutes;
    private final int seconds;
    private final int mark;
    private final int numberOfClick;
    private final Uri uri;

    public Track(String artist, String album, String name, int diskNumber, int trackNumber, int bitrate, long date, int minutes, int seconds, Uri uri) {
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.diskNumber = diskNumber;
        this.trackNumber = trackNumber;
        this.bitrate = bitrate;
        this.date = date;
        this.minutes = minutes;
        this.seconds = seconds;
        this.mark = 0;
        this.numberOfClick = 0;
        this.uri = uri;
        this.hash = generateHash();
    }

    @Override
    public String getHash() {
        return hash;
    }

    @Override
    public boolean isTrack() {
        return true;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public int getDiskNumber() {
        return diskNumber;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public int getBitrate() {
        return bitrate;
    }

    public long getDate() {
        return date;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getMark() {
        return mark;
    }

    public int getNumberOfClick() { return numberOfClick; }

    public Uri getUri() {
        return uri;
    }

    public String generateHash() {
        return "" + (artist.hashCode() + album.hashCode() + name.hashCode());
    }

    @Override
    public String toString() {
        return "Track{" +
                "name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", diskNumber=" + diskNumber +
                ", trackNumber=" + trackNumber +
                ", bitrate=" + bitrate +
                ", date=" + date +
                ", minutes=" + minutes +
                ", seconds=" + seconds +
                ", mark=" + mark +
                ", numberOfClick=" + numberOfClick +
                ", uri=" + uri +
                ", hash='" + hash + '\'' +
                '}';
    }
}
