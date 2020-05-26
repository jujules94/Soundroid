package com.example.soundroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.soundroid.db.SoundroidContract.SoundroidHistory;

import java.util.ArrayList;

public class HistoryManager {

    /** Convenience method to add an history entry.
     * @param context of the database helper.
     * @param track to be added to the history.
     * @return true is the history has been added, false otherwise.
     */
    public static boolean add(Context context, Track track) {
        return insertRow(context, new History(System.currentTimeMillis(), track.getHash()));
    }

    /** Convenience method to get all entries of history from the database.
     * @param context of the database helper.
     * @return list of history entries.
     */
    public static ArrayList<History> getRows(Context context) {
        SoundroidDbHelper dbHelper = new SoundroidDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = SoundroidHistory.getProjection();
        Cursor cursor = db.query(SoundroidHistory.TABLE_NAME, projection, null, null,null,null,"date desc");
        ArrayList<History> histories = new ArrayList<>();
        while (cursor.moveToNext()) {
            histories.add(new History(
                    cursor.getLong(0),
                    cursor.getString(1)
            ));
        }
        cursor.close();
        return histories;
    }

    /** Convenience method to insert an history into the database.
     * @param context of the database helper.
     * @param history to be inserted.
     * @return true if the row has been inserted, false otherwise.
     */
    private static boolean insertRow(Context context, History history) {
        SQLiteDatabase db = new SoundroidDbHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SoundroidHistory.COLUMN_NAME_DATE, history.getDate());
        values.put(SoundroidHistory.COLUMN_NAME_TRACK_HASH, history.getHash());
        db.insert(SoundroidHistory.TABLE_NAME, null, values);
        return true;
    }

    /** Convenience method to insert histories into the database.
     * @param context of the database helper.
     * @return true if rows has been inserted, false otherwise.
     */
    public static boolean insertRows(Context context, ArrayList<History> histories) {
        for (History history : histories) {
            if (!insertRow(context, history)) {
                return false;
            }
        }
        return true;
    }

}
