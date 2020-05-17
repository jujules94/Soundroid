package com.example.soundroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class TracklistLinkManager {

    /** Convenience method to get all entries of links from the database.
     * @param context of the database helper.
     * @return list of links.
     */
    public static ArrayList<TracklistLink> getRows(Context context) {
        SoundroidDbHelper dbHelper = new SoundroidDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = SoundroidContract.SoundroidTrack.getProjection();
        Cursor cursor = db.query(SoundroidContract.SoundroidTrack.TABLE_NAME, projection, null, null,null,null,null);
        ArrayList<TracklistLink> tracklistLinks = new ArrayList<>();
        while (cursor.moveToNext()) {
            tracklistLinks.add(new TracklistLink(
                    cursor.getString(0),
                    cursor.getString(1)
            ));
        }
        cursor.close();
        return tracklistLinks;
    }

    /** Convenience method to insert a link into the database.
     * @param context of the database helper.
     * @param link to be inserted.
     * @return true if the row has been inserted, false otherwise.
     */
    private static boolean insertRow(Context context, TracklistLink link) {
        SQLiteDatabase db = new SoundroidDbHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SoundroidContract.SoundroidTracklistLink.COLUMN_NAME_TRACKLIST_HASH, link.getTracklistHash());
        values.put(SoundroidContract.SoundroidTracklistLink.COLUMN_NAME_TRACKLISTABLE_HASH, link.getTracklistableHash());
        return -1 == db.insert(SoundroidContract.SoundroidTrack.TABLE_NAME, null, values);
    }

    /** Convenience method to insert links into the database.
     * @param context of the database helper.
     * @return true if rows has been inserted, false otherwise.
     */
    public static boolean insertRows(Context context, ArrayList<TracklistLink> links) {
        for (TracklistLink link : links) {
            if (!insertRow(context, link)) {
                return false;
            }
        }
        return true;
    }

}
