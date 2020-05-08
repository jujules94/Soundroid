package com.example.soundroid.db;

public class Track {

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

    public Track(String artist, String album, String name, int diskNumber, int trackNumber, int bitrate, long date, int minutes, int seconds) {
        this.hash = artist + album + name;
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
    }

    public String getHash() {
        return hash;
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

    public int getNumberOfClick() {
        return numberOfClick;
    }

    @Override
    public String toString() {
        return "Track{" +
                "hash='" + hash + '\'' +
                ", name='" + name + '\'' +
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
                '}';
    }

}
