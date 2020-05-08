package com.example.soundroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.soundroid.db.SoundroidContract.SoundroidTrack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrackManager {

    public TrackManager() {}

    /**
     * Convenience method for inserting a track into the database.
     *
     * @param context of the database helper.
     * @param track to be inserted.
     * @return the row ID of the newly inserted track, or -1 if an error occurred or the track already exist.
     */
    public static long add(Context context, Track track) {
        if (isTrackAlreadyInDb(context, track)) {
            return -1;
        }
        SoundroidDbHelper dbHelper = new SoundroidDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SoundroidTrack.COLUMN_NAME_HASH, track.getHash());
        values.put(SoundroidTrack.COLUMN_NAME_NAME, track.getName());
        values.put(SoundroidTrack.COLUMN_NAME_ARTIST, track.getArtist());
        values.put(SoundroidTrack.COLUMN_NAME_ALBUM, track.getAlbum());
        values.put(SoundroidTrack.COLUMN_NAME_DISK_NUMBER, track.getDiskNumber());
        values.put(SoundroidTrack.COLUMN_NAME_TRACK_NUMBER, track.getTrackNumber());
        values.put(SoundroidTrack.COLUMN_NAME_BITRATE, track.getBitrate());
        values.put(SoundroidTrack.COLUMN_NAME_DATE, track.getDate());
        values.put(SoundroidTrack.COLUMN_NAME_MINUTES, track.getMinutes());
        values.put(SoundroidTrack.COLUMN_NAME_SECONDS, track.getSeconds());
        values.put(SoundroidTrack.COLUMN_NAME_MARK, track.getMark());
        values.put(SoundroidTrack.COLUMN_NAME_NUMBEROFCLICKS, track.getNumberOfClick());
        return db.insert(SoundroidTrack.TABLE_NAME, null, values);
    }

    /**
     * Convenience method to get a track from the database by his hash.
     * @param context of the database helper.
     * @param hash of the asked track.
     * @return the asked track or null if it doesn't exist in the database.
     */
    public static Track get(Context context, String hash) {
        SoundroidDbHelper dbHelper = new SoundroidDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
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
                SoundroidTrack.COLUMN_NAME_NUMBEROFCLICKS
        };
        String selection = SoundroidTrack.COLUMN_NAME_HASH + " = ?";
        String[] selectionArgs = { hash };
        Cursor cursor = db.query(
                SoundroidTrack.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if (!cursor.moveToNext()) {
            return null;
        };
        Track track = new Track(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getInt(4),
                cursor.getInt(5),
                cursor.getLong(6),
                cursor.getInt(7),
                cursor.getInt(8));
        cursor.close();
        return track;
    }

    /**
     * Test if the track exist in the database
     * @param context of the database helper.
     * @param track of the track to test.
     * @return true if the track already exist, false otherwise.
     */
    public static boolean isTrackAlreadyInDb(Context context, Track track) {
        return get(context, track.getHash()) != null;
    }

}
