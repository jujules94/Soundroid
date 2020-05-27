package com.example.soundroid;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.soundroid.db.Track;
import com.example.soundroid.db.TrackManager;

import java.util.ArrayList;
import java.util.List;

public class MusicIndexer {

    /** Method to extract tracks from the file system.
     * @param context of the database helper
     */
    public static void indexMusic(Context context) {
        List<String> hashes = TrackManager.getHashes(context);
        List<Track> tracksToAdd = new ArrayList<>();
        String[] projection = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.AudioColumns.TRACK,
                MediaStore.Audio.AudioColumns.ALBUM_ID
        };
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null
        );
        /* columns */
        int _id = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
        int _artist = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
        int _album = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
        int _title = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
        int _albumId = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID);
        int _trackId = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TRACK);
        int _date = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR);
        int _duration = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
        /* create tracks */
        while (cursor.moveToNext()) {
            Track track = new Track(
                    cursor.getString(_artist),
                    cursor.getString(_album),
                    cursor.getString(_title),
                    Integer.parseInt(cursor.getString(_albumId)), Integer.parseInt(cursor.getString(_trackId)), 320,
                    Long.parseLong(cursor.getString(_date) == null ? "0" : cursor.getString(_date)), 2, 40,
                    ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursor.getLong(_id)));
            //Log.d("Indexer", Integer.parseInt(cursor.getString(_albumId)) + " " + Integer.parseInt(cursor.getString(_trackId)) + " " + cursor.getString(_date) + " " + cursor.getLong(_duration));
            int index = hashes.indexOf(track.getHash());
            if (index == -1) {
                tracksToAdd.add(track);
            } else {
                hashes.remove(index);
            }
        }
        if (!hashes.isEmpty()) {
            TrackManager.deleteAll(context, hashes);
        }
        if (!tracksToAdd.isEmpty()) {
            TrackManager.addAll(context, tracksToAdd);
        }
    }

}
