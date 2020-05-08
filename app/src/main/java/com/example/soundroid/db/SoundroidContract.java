package com.example.soundroid.db;

import android.provider.BaseColumns;

public final class SoundroidContract {

    private SoundroidContract() {}

    public static class SoundroidTrack implements BaseColumns {
        public static final String TABLE_NAME = "track";
        public static final String COLUMN_NAME_HASH = "hash";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_ARTIST = "artist";
        public static final String COLUMN_NAME_ALBUM = "album";
        public static final String COLUMN_NAME_DISK_NUMBER = "disk_number";
        public static final String COLUMN_NAME_TRACK_NUMBER = "track_number";
        public static final String COLUMN_NAME_BITRATE = "bitrate";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_MINUTES = "minutes";
        public static final String COLUMN_NAME_SECONDS = "seconds";
        public static final String COLUMN_NAME_MARK = "mark";
        public static final String COLUMN_NAME_NUMBEROFCLICKS = "number_of_clicks";
    }
}