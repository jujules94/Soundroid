package com.example.soundroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.soundroid.db.SoundroidContract.*;
import com.example.soundroid.io.StorageManager;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class SoundroidDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 9;
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
                    SoundroidTrack.COLUMN_NAME_NUMBEROFCLICKS + " INTEGER," +
                    SoundroidTrack.COLUMN_NAME_URI + " TEXT)";

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
        create(db);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       clear(db);
       create(db);
    }

    private static void create(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TRACK);
        db.execSQL(SQL_CREATE_TRACKLIST);
        db.execSQL(SQL_CREATE_TRACKLIST_LINK);
    }

    private static void clear(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_TRACK);
        db.execSQL(SQL_DELETE_TRACKLIST);
        db.execSQL(SQL_DELETE_TRACKLIST_LINK);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /** Save current database state as json parsed PortableDatabase file in the device external storage.
     *  Database will be saved in root/Soundroid directory of the device external storage.
     * @param context of the database helper.
     * @param name of the file.
     * @return false if an error occurred, true otherwise
     */
    public static boolean save(Context context, String name) {
        /*
        RuntimeTypeAdapterFactory<Tracklistable> typeFactory = RuntimeTypeAdapterFactory
                .of(Tracklistable.class, "type")
                .registerSubtype(Track.class)
                .registerSubtype(Tracklist.class);
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(typeFactory).create();
        String json = gson.toJson(new PortableDatabase(
                TrackManager.getAll(context),
                TracklistManager.getAll(context)
        ));
        /** create file
        return StorageManager.createJsonFile(name, json);
         */
        return false;
    }

    /** Replace current database by the given database in arguments.
     *  The given database must be a Path of json file representation of a PortableDatabase object.
     * @param context of the database helper.
     * @param name of a Json file representation of a PortableDatabase object.
     * @return false if an error occurred, true otherwise
     */
    public static boolean load(Context context, String name) {
        /*
        SQLiteDatabase db = new SoundroidDbHelper(context).getReadableDatabase();
        clear(db);
        create(db);/
        RuntimeTypeAdapterFactory<Tracklistable> typeFactory = RuntimeTypeAdapterFactory
                .of(Tracklistable.class, "type")
                .registerSubtype(Track.class)
                .registerSubtype(Tracklist.class);
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(typeFactory).create();

        //String json = StorageManager.readJsonFile(name);
        PortableDatabase database = null;
        try {
            database = gson.fromJson(new FileReader(StorageManager.getSoundroidDirectory() + "/" + name + ".json"), PortableDatabase.class);
        } catch (FileNotFoundException e) {
            int a = 1;
        }
        TrackManager.addAll(context, database.getTracks());
        TracklistManager.addAll(context, database.getTracklists());
        return true;
        */
        return false;
    }

}