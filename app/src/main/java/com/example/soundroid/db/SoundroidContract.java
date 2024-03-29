package com.example.soundroid.db;

import android.provider.BaseColumns;

public final class SoundroidContract {

    public static class SoundroidTrack implements BaseColumns {

        public static final String TABLE_NAME = "track";
        public static final String COLUMN_NAME_HASH = "hash";
        public static final String COLUMN_NAME_ARTIST = "artist";
        public static final String COLUMN_NAME_ALBUM = "album";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DISK_NUMBER = "disk_number";
        public static final String COLUMN_NAME_TRACK_NUMBER = "track_number";
        public static final String COLUMN_NAME_BITRATE = "bitrate";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_MINUTES = "minutes";
        public static final String COLUMN_NAME_SECONDS = "seconds";
        public static final String COLUMN_NAME_MARK = "mark";
        public static final String COLUMN_NAME_NUMBEROFCLICKS = "number_of_clicks";
        public static final String COLUMN_NAME_URI = "uri";

        public static String getFields() {
            return "track.hash, track.artist, track.album, track.name, track.disk_number, track.track_number, track.bitrate, track.date, track.minutes, track.seconds, track.mark, track.number_of_clicks, track.uri ";
        }

        public static String[] getProjection() {
            return new String[] {
                    SoundroidTrack.COLUMN_NAME_HASH,
                    SoundroidTrack.COLUMN_NAME_ARTIST,
                    SoundroidTrack.COLUMN_NAME_ALBUM,
                    SoundroidTrack.COLUMN_NAME_NAME,
                    SoundroidTrack.COLUMN_NAME_DISK_NUMBER,
                    SoundroidTrack.COLUMN_NAME_TRACK_NUMBER,
                    SoundroidTrack.COLUMN_NAME_BITRATE,
                    SoundroidTrack.COLUMN_NAME_DATE,
                    SoundroidTrack.COLUMN_NAME_MINUTES,
                    SoundroidTrack.COLUMN_NAME_SECONDS,
                    SoundroidTrack.COLUMN_NAME_MARK,
                    SoundroidTrack.COLUMN_NAME_NUMBEROFCLICKS,
                    SoundroidTrack.COLUMN_NAME_URI
            };
        }

    }

    public static class SoundroidTracklist implements BaseColumns {

        public static final String TABLE_NAME = "tracklist";
        public static final String COLUMN_NAME_HASH = "hash";
        public static final String COLUMN_NAME_NAME = "name";

        public static String[] getProjection() {
            return new String[] {
                    COLUMN_NAME_HASH,
                    COLUMN_NAME_NAME
            };
        }

    }

    public static class SoundroidTracklistLink implements BaseColumns {

        public static final String TABLE_NAME = "tracklist_link";
        public static final String COLUMN_NAME_TRACKLIST_HASH = "tracklist_hash";
        public static final String COLUMN_NAME_TRACKLISTABLE_HASH = "tracklistable_hash";

        public static String[] getProjection() {
            return new String[] {
                    COLUMN_NAME_TRACKLIST_HASH,
                    COLUMN_NAME_TRACKLISTABLE_HASH
            };
        }

    }

    public static class SoundroidHistory implements BaseColumns {

        public static final String TABLE_NAME = "history";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TRACK_HASH = "track_hash";

        public static String[] getProjection() {
            return new String[] {
                    COLUMN_NAME_DATE,
                    COLUMN_NAME_TRACK_HASH
            };
        }

    }

}