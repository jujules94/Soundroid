package com.example.soundroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.soundroid.db.SoundroidContract.*;

public class SoundroidDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "Soundroid.db";

    private static final String SQL_CREATE_TRACK =
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

    private static final String SQL_DELETE_TRACK = "DROP TABLE IF EXISTS " + SoundroidTrack.TABLE_NAME;

    private static final String SQL_CREATE_TRACKLIST =
            "CREATE TABLE " + SoundroidTracklist.TABLE_NAME + " (" +
                    SoundroidTracklist.COLUMN_NAME_HASH + " TEXT PRIMARY KEY," +
                    SoundroidTracklist.COLUMN_NAME_NAME + " TEXT)";

    private static final String SQL_DELETE_TRACKLIST = "DROP TABLE IF EXISTS " + SoundroidTracklist.TABLE_NAME;

    private static final String SQL_CREATE_TRACKLIST_LINK =
            "CREATE TABLE " + SoundroidTracklistLink.TABLE_NAME + " (" +
                    SoundroidTracklistLink.COLUMN_NAME_TRACKLIST_HASH + " TEXT," +
                    SoundroidTracklistLink.COLUMN_NAME_TRACKLISTABLE_HASH + " TEXT," +
                    "PRIMARY KEY (" + SoundroidTracklistLink.COLUMN_NAME_TRACKLIST_HASH + ", " + SoundroidTracklistLink.COLUMN_NAME_TRACKLISTABLE_HASH + " ));";

    private static final String SQL_DELETE_TRACKLIST_LINK = "DROP TABLE IF EXISTS " + SoundroidTracklistLink.TABLE_NAME;

    public SoundroidDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TRACK);
        db.execSQL(SQL_CREATE_TRACKLIST);
        db.execSQL(SQL_CREATE_TRACKLIST_LINK);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TRACK);
        db.execSQL(SQL_DELETE_TRACKLIST);
        db.execSQL(SQL_DELETE_TRACKLIST_LINK);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}