package com.example.soundroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.soundroid.db.SoundroidContract.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class TrackManager {

    /** Convenience method for inserting a track into the database.
     * @param context of the database helper.
     * @param track to be inserted.
     * @return false if an error occurred, true otherwise.
     */
    private static boolean add(Context context, Track track) {
        if (isTrackAlreadyInDb(context, track)) {
            return true;
        }
        SQLiteDatabase db = new SoundroidDbHelper(context).getWritableDatabase();
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
        values.put(SoundroidTrack.COLUMN_NAME_URI, track.getUri().toString());
        return (db.insert(SoundroidTrack.TABLE_NAME, null, values) != -1);
    }

    /** Convenience method to add tracks into the database.
     * @param context of the database helper.
     * @param tracks to be inserted.
     * @return false if an error occurred, true otherwise.
     */
    public static boolean addAll(Context context, List<Track> tracks) {
        for (Track track : tracks) {
            if (!add(context, track)) {
                return false;
            }
        }
        return true;
    }

    /** Convenience method to get a track from the database by his hash.
     * @param context of the database helper.
     * @param hash of the asked track.
     * @return the asked track or null if it doesn't exist in the database.
     */
    public static Track get(Context context, String hash) {
        SoundroidDbHelper dbHelper = new SoundroidDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = SoundroidContract.SoundroidTrack.getProjection();
        String selection = SoundroidTrack.COLUMN_NAME_HASH + " = ?";
        String[] selectionArgs = { hash };
        Cursor cursor = db.query(SoundroidTrack.TABLE_NAME, projection, selection, selectionArgs,null,null,null);
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
                cursor.getInt(8),
                Uri.parse(cursor.getString(9)));
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

    /** Get all tracks
     * @param context of the database helper.
     * @return list of tracks
     */
    public static List<Track> getAll(Context context) {
        SQLiteDatabase db = new SoundroidDbHelper(context).getReadableDatabase();
        List<Track> tracks = new ArrayList<>();
        String[] projection = SoundroidContract.SoundroidTrack.getProjection();
        Cursor cursor = db.query(SoundroidTrack.TABLE_NAME, projection, null, null,null,null,null);
        Log.d("TrackManager", Arrays.toString(cursor.getColumnNames()));
        while (cursor.moveToNext()) {
            tracks.add(new Track(
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getInt(5),
                    cursor.getInt(6),
                    cursor.getLong(7),
                    cursor.getInt(8),
                    cursor.getInt(9),
                    Uri.parse(cursor.getString(12))));
        }
        cursor.close();
        return tracks;
    }

    /** get a list of tracks that check filters in arguments.
     * @param context of the database helper.
     * @param artist filter, null to not use this filter.
     * @param album filter, null to not use this filter.
     * @param title filter, null to not use this filter.
     * @return list of tracks that check filters in arguments.
     */
    public static ArrayList<Tracklistable> get(Context context, String artist, String album, String title) {
        SQLiteDatabase db = new SoundroidDbHelper(context).getReadableDatabase();
        ArrayList<Tracklistable> tracks = new ArrayList<>();
        String[] projection = SoundroidContract.SoundroidTrack.getProjection();
        String[] selectionArgs = null;
        StringBuilder selectionBuilder = new StringBuilder();
        if (artist != null) {
            selectionBuilder.append(SoundroidTrack.COLUMN_NAME_ARTIST + " = ? ");
            selectionArgs = new String[] { artist };
        }
        if (album != null) {
            selectionBuilder.append(SoundroidTrack.COLUMN_NAME_ALBUM + " = ? ");
            selectionArgs = new String[] { album };
        }
        if (title != null) {
            selectionBuilder.append(SoundroidTrack.COLUMN_NAME_NAME + " like ? ");
            selectionArgs = new String[] { "%" + title + "%" };
        }
        Cursor cursor = db.query(SoundroidTrack.TABLE_NAME, projection, selectionBuilder.toString(), selectionArgs,null,null,null);
        while (cursor.moveToNext()) {
            tracks.add(new Track(
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getInt(5),
                    cursor.getInt(6),
                    cursor.getLong(7),
                    cursor.getInt(8),
                    cursor.getInt(9),
                    Uri.parse(cursor.getString(12))));
        }
        cursor.close();
        return tracks;
    }

    /** Return hashes of all track indexed.
     * @param context of the database helper.
     * @return list of tracks indexed.
     */
    public static List<String> getHashes(Context context) {
        SQLiteDatabase db = new SoundroidDbHelper(context).getReadableDatabase();
        List<String> hashes = new ArrayList<>();
        String query = "select " + SoundroidTrack.COLUMN_NAME_HASH + " from " + SoundroidTrack.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            hashes.add(cursor.getString(0));
        }
        cursor.close();
        return hashes;
    }

    /** Delete all tracks and all links of the given track prints
     * @param context of the database helper.
     * @param hashes of the tracks to remove
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void deleteAll(Context context, List<String> hashes) {
        SQLiteDatabase db = new SoundroidDbHelper(context).getWritableDatabase();
        StringJoiner joiner = new StringJoiner(",", "(", ")");
        hashes.forEach(hash -> joiner.add("'" + hash + "'"));
        String predicate = " in " + joiner.toString();
        db.delete(SoundroidTrack.TABLE_NAME,SoundroidTrack.COLUMN_NAME_HASH + predicate, null);
        db.delete(SoundroidTracklistLink.TABLE_NAME, SoundroidTracklistLink.COLUMN_NAME_TRACKLISTABLE_HASH + predicate, null);
    }

}
