package com.example.soundroid;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.soundroid.db.Track;
import com.example.soundroid.db.TrackManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MusicIndexer {

    /** Method to extract tracks from the file system.
     * @param context of the database helper.
     * @return list of tracks
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void indexMusic(Context context) {
        List<String> hashes = TrackManager.getHashes(context);
        List<Track> tracksToAdd = new ArrayList<Track>();
        String[] projection = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.DURATION,
        };
        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null
            )) {
            /* columns */
            int _id = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int _artist = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            int _album = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
            int _title = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            int _date = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED);
            int _duration = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            /* create tracks */
            while (cursor.moveToNext()) {
                Track track = new Track(
                        cursor.getString(_artist),
                        cursor.getString(_album),
                        cursor.getString(_title),
                        1, 1, 320,
                        500L, 2, 40,
                        ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursor.getLong(_id)));
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

}
