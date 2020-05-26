package com.example.soundroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.soundroid.db.SoundroidContract.SoundroidTrack;
import com.example.soundroid.db.SoundroidContract.SoundroidTracklist;
import com.example.soundroid.db.SoundroidContract.SoundroidTracklistLink;

public class TracklistManager {

    /** Convenience method for inserting an empty tracklist into the database.
     * @param context of the database helper.
     * @param tracklist to be added.
     * @return true if the tracklist has been inserted, false otherwise.
     */
    public static boolean add(Context context, Tracklist tracklist) {
        if (isTracklistAlreadyInDb(context, tracklist)) {
            return false;
        }
        SQLiteDatabase db = new SoundroidDbHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SoundroidTracklist.COLUMN_NAME_HASH, tracklist.getHash());
        values.put(SoundroidTracklist.COLUMN_NAME_NAME, tracklist.getName());
        if (db.insert(SoundroidTracklist.TABLE_NAME, null, values) == -1) {
            return false;
        }
        if (tracklist.getTracklistables().isEmpty()) {
            return true;
        }
        for (Tracklistable tracklistable : tracklist.getTracklistables()) {
            values = new ContentValues();
            values.put(SoundroidTracklistLink.COLUMN_NAME_TRACKLIST_HASH, tracklist.getHash());
            values.put(SoundroidTracklistLink.COLUMN_NAME_TRACKLISTABLE_HASH, tracklistable.getHash());
            if (db.insert(SoundroidTracklistLink.TABLE_NAME, null, values) == -1) {
                delete(context, tracklist.getHash());
                return false;
            }
        }
        return true;
    }

    /** Convenience method to add tracklists into the database.
     * @param context of the database helper.
     * @param tracklists to be inserted.
     * @return false if an error occurred, true otherwise.
     */
    public static boolean addAll(Context context, List<Tracklist> tracklists) {
        for (Tracklist tracklist : tracklists) {
            if (!add(context, tracklist)) {
                return false;
            }
        }
        return true;
    }

    /** Convenience method to get a tracklist from the database by his hash.
     * @param context of the database helper.
     * @param hash of the asked tracklist.
     * @return the tracklist or null if it doesn't exist.
     */
    public static Tracklist get(Context context, String hash) {
        SQLiteDatabase db = new SoundroidDbHelper(context).getReadableDatabase();
        String query = " select tracklist.name, tracklistable_hash, " + SoundroidTrack.getFields() +
                " from tracklist" +
                " left join tracklist_link on tracklist_link.tracklist_hash = tracklist.hash" +
                " left join track on tracklist_link.tracklistable_hash = track.hash" +
                " where tracklist.hash = ?";
        Cursor cursor = db.rawQuery(query, new String[]{ hash });
        if (cursor.getCount() == 0) {
            return null;
        };
        String name = "";
        ArrayList<Tracklistable> tracklistables = new ArrayList<Tracklistable>();
        while (cursor.moveToNext()) {
            if (name.equals("")) {
                name =  cursor.getString(0);
            }
            if (cursor.getString(1) == null) {
                /** Element is an empty playlist */
                continue;
            }
            if (cursor.getString(3) == null) {
                /** Element is a list */
                tracklistables.add(get(context, cursor.getString(1)));
            } else {
                /** Element is a track */
                tracklistables.add(new Track(
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getInt(6),
                        cursor.getInt(7),
                        cursor.getInt(8),
                        cursor.getLong(9),
                        cursor.getInt(10),
                        cursor.getInt(11),
                        Uri.parse(cursor.getString(14))
                ));
            }
        };
        cursor.close();
        return new Tracklist(hash, name, tracklistables);
    }

    /** Convenience method to add a track to a tracklist.
     * @param context of the database helper.
     * @param tracklist where the track must be added.
     * @param track to be added.
     * @return true is the track has been added, false otherwise.
     */
    public static boolean addTrack(Context context, Tracklist tracklist, Track track) {
        /** search if track is already in tracklist */
        for (Tracklistable tracklistable : tracklist.getTracklistables()) {
            if (tracklistable.isTrack() && tracklistable.getHash().equals(track.getHash())) {
                return false;
            }
        }
        SQLiteDatabase db = new SoundroidDbHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SoundroidTracklistLink.COLUMN_NAME_TRACKLIST_HASH, tracklist.getHash());
        values.put(SoundroidTracklistLink.COLUMN_NAME_TRACKLISTABLE_HASH, track.getHash());
        return -1 == db.insert(SoundroidTracklistLink.TABLE_NAME, null, values);
    }

    /** Convenience method to get all playlists from the database.
     * @param context of the database helper.
     * @return the playlists from the database.
     */
    public static ArrayList<Tracklist> getAll(Context context) {
        SQLiteDatabase db = new SoundroidDbHelper(context).getReadableDatabase();
        String query = "select tracklist.name, tracklistable_hash, " + SoundroidTrack.getFields() +
                " from tracklist" +
                " left join tracklist_link on tracklist_link.tracklist_hash = tracklist.hash" +
                " left join track on tracklist_link.tracklistable_hash = track.hash";
        HashMap<String, ArrayList<Tracklistable>> map = new HashMap<>();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            String name =  cursor.getString(0);
            if (!map.containsKey(name)) {
                map.put(name, new ArrayList<Tracklistable>());
            }
            if (cursor.getString(1) == null) {
                /** Element is an empty playlist */
                continue;
            }
            if (cursor.getString(3) == null) {
                /** Element is a list */
                map.get(name).add(get(context, cursor.getString(1)));
            } else {
                /** Element is a track */
                map.get(name).add(new Track(
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getInt(6),
                        cursor.getInt(7),
                        cursor.getInt(8),
                        cursor.getLong(9),
                        cursor.getInt(10),
                        cursor.getInt(11),
                        Uri.parse(cursor.getString(14))
                  ));
            }
        };
        cursor.close();
        ArrayList<Tracklist> tracklists = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Tracklistable>> entry : map.entrySet()) {
            tracklists.add(new Tracklist(entry.getKey(), entry.getKey(), entry.getValue()));
        }
        return tracklists;
    }

    /** Convenience method to get all tracks of a playlist.
    * @param context of the database helper.
    * @return list of tracks of the given tracklist b.
    */
    public static ArrayList<Track> getTracks(Context context, Tracklist tracklist) {
        ArrayList<Track> tracks = new ArrayList<>();
        List<Tracklistable> tracklistables = get(context, tracklist.getHash()).getTracklistables();
        for (Tracklistable tracklistable : tracklistables) {
            if (tracklistable.isTrack()) {
                //Log.d("TrackListManager", "track : " + (Track) tracklistable);
                tracks.add((Track) tracklistable);
            }
        }
        return tracks;
    }

    /** Test if the tracklist already exist in the database
     * @param context of the database helper.
     * @param tracklist to be tested.
     * @return true if the tracklist already exist, false otherwise.
     */
    public static boolean isTracklistAlreadyInDb(Context context, Tracklist tracklist) {
        SQLiteDatabase db = new SoundroidDbHelper(context).getReadableDatabase();
        String query = "select * from tracklist where hash = ?";
        Cursor cursor = db.rawQuery(query, new String[] { tracklist.getHash() });
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    /** Delete a tracklist and all links related to it in the database.
     * @param context of the database helper.
     * @param hash of the tracklist.
     */
    public static void delete(Context context, String hash) {
        SQLiteDatabase db = new SoundroidDbHelper(context).getWritableDatabase();
        db.delete(SoundroidTracklist.TABLE_NAME,SoundroidTracklist.COLUMN_NAME_HASH + " = ?", new String[]{ hash });
        db.delete(SoundroidTracklistLink.TABLE_NAME,SoundroidTracklistLink.COLUMN_NAME_TRACKLIST_HASH + " = ?", new String[]{ hash });
    }

    /** Convenience method to get all entries of tracklist from the database.
     * @param context of the database helper.
     * @return list of tracklist entries.
     */
    public static ArrayList<Tracklist> getRows(Context context) {
        SoundroidDbHelper dbHelper = new SoundroidDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = SoundroidTracklist.getProjection();
        Cursor cursor = db.query(SoundroidTracklist.TABLE_NAME, projection, null, null,null,null,null);
        ArrayList<Tracklist> tracklists = new ArrayList<>();
        while (cursor.moveToNext()) {
            tracklists.add(new Tracklist(
                    cursor.getString(0),
                    cursor.getString(1)
            ));
        }
        cursor.close();
        return tracklists;
    }

    /** Convenience method to insert a tracklist into the database.
     * @param context of the database helper.
     * @param tracklist to be inserted.
     * @return true if the row has been inserted, false otherwise.
     */
    private static boolean insertRow(Context context, Tracklist tracklist) {
        SQLiteDatabase db = new SoundroidDbHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SoundroidTracklist.COLUMN_NAME_HASH, tracklist.getHash());
        values.put(SoundroidTracklist.COLUMN_NAME_NAME, tracklist.getName());
        db.insert(SoundroidTracklist.TABLE_NAME, null, values);
        return true;
    }

    /** Convenience method to insert tracklists into the database.
     * @param context of the database helper.
     * @return true if rows has been inserted, false otherwise.
     */
    public static boolean insertRows(Context context, ArrayList<Tracklist> tracklists) {
        for (Tracklist tracklist : tracklists) {
            if (!insertRow(context, tracklist)) {
                return false;
            }
        }
        return true;
    }

    /** Delete a track from a tracklist.
     * @param context of the database helper.
     * @param tracklist that must me modified.
     * @param track to be deleted.
     */
    public static void deleteTrackFromTracklist(Context context, Tracklist tracklist, Track track) {
        SQLiteDatabase db = new SoundroidDbHelper(context).getWritableDatabase();
        db.delete(SoundroidTracklistLink.TABLE_NAME,
                SoundroidTracklistLink.COLUMN_NAME_TRACKLIST_HASH + " = ? and " + SoundroidTracklistLink.COLUMN_NAME_TRACKLISTABLE_HASH + " = ? ",
                new String[]{ tracklist.getHash(), track.getHash() });
    }

}
