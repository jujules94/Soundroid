package com.example.soundroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.soundroid.db.SoundroidContract.SoundroidTrack;

public class SoundroidDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Soundroid.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SoundroidTrack.TABLE_NAME + " (" +
                    SoundroidTrack.COLUMN_NAME_HASH + " TEXT PRIMARY KEY," +
                    SoundroidTrack.COLUMN_NAME_NAME + " TEXT," +
                    SoundroidTrack.COLUMN_NAME_ARTIST + " TEXT," +
                    SoundroidTrack.COLUMN_NAME_ALBUM + " TEXT," +
                    SoundroidTrack.COLUMN_NAME_DISK_NUMBER + " INTEGER," +
                    SoundroidTrack.COLUMN_NAME_TRACK_NUMBER + " INTEGER," +
                    SoundroidTrack.COLUMN_NAME_BITRATE + " INTEGER," +
                    SoundroidTrack.COLUMN_NAME_DATE + " INTEGER," +
                    SoundroidTrack.COLUMN_NAME_MINUTES + " INTEGER," +
                    SoundroidTrack.COLUMN_NAME_SECONDS + " INTEGER," +
                    SoundroidTrack.COLUMN_NAME_MARK + " INTEGER," +
                    SoundroidTrack.COLUMN_NAME_NUMBEROFCLICKS + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SoundroidTrack.TABLE_NAME;

    public SoundroidDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}