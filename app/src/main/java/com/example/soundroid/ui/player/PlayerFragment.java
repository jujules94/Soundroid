package com.example.soundroid.ui.player;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.soundroid.MainActivity;
import com.example.soundroid.R;
import com.example.soundroid.db.Track;
import com.example.soundroid.db.TrackManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class PlayerFragment extends Fragment {

    private SharedPreferences sp;
    private final static String[] methods = new String[]{"GET", "POST"};
    private final static int percent_limit = 25;

    private Handler threadHandler = new Handler();
    private MediaPlayer mediaPlayer;
    private List<Track> tracks = new ArrayList<>();
    private int currentIndex = 0;
    private boolean playing = false, toggleBreak = false;

    private TextView title, album, artist, current, max;
    private SeekBar seekBar;
    private Button rewind, start, forward;
    private ImageButton previousSong, shuffle, nextSong;
    private MainActivity main;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getActivity().getSharedPreferences("PREFS", MODE_PRIVATE);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_player, container, false);
        main = (MainActivity) getActivity();
        tracks = main.getSelectedTracks();

        title = root.findViewById(R.id.title);
        album = root.findViewById(R.id.album);
        artist = root.findViewById(R.id.artist);
        current = root.findViewById(R.id.currentPosition);
        max = root.findViewById(R.id.maxPosition);

        seekBar = root.findViewById(R.id.seekBar);
        seekBar.setClickable(false);

        rewind = root.findViewById(R.id.rewind);
        start = root.findViewById(R.id.start);
        forward = root.findViewById(R.id.forward);

        previousSong = root.findViewById(R.id.previousSong);
        shuffle = root.findViewById(R.id.shuffle);
        nextSong = root.findViewById(R.id.nextSong);

        FloatingActionButton fab = root.findViewById(R.id.fab);

        if (tracks.size() == 0) return root;
        fab.setOnClickListener(view -> shareTrack(tracks.get(currentIndex)));
        start.setOnClickListener(v -> doStartPause());
        rewind.setOnClickListener(v -> doRewind());
        forward.setOnClickListener(v -> doFastForward());
        previousSong.setOnClickListener(v ->doChangeSong(-1));
        shuffle.setOnClickListener(v ->doShuffleTracks());
        nextSong.setOnClickListener(v ->doChangeSong(1));

        // Create MediaPlayer.
        updateSongChangeButtons();
        mediaPlayer = new MediaPlayer();
        setupMediaPlayer(mediaPlayer);
        setupSong(mediaPlayer);
        // Create a thread to update position of SeekBar.
        UpdateSeekBarThread updateSeekBarThread= new UpdateSeekBarThread();
        threadHandler.postDelayed(updateSeekBarThread,50);
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Fragments", "destroy");
        if (mediaPlayer != null) mediaPlayer.stop();
    }

    private void setupMediaPlayer(MediaPlayer mp) {
        mp.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
        mp.setOnCompletionListener(this::doComplete);
    }

    private void doComplete(MediaPlayer mp)  {
        Log.d("Fragments", "song completed");
        currentIndex++;
        if (tracks.size() > currentIndex) resetPlayer(mp);
        else updateSongChangeButtons();
    }

    private void setupSong(MediaPlayer mp) {
        try {
            Uri uri = tracks.get(currentIndex).getUri();
            Log.d("Fragments", "tracks : " + tracks);
            Log.d("Fragments", "media selected : " + tracks.get(currentIndex) + ", source : " + uri);
            mp.setDataSource(getContext(), uri);
            mp.prepareAsync();
        } catch(Exception e) {
            Log.e("Fragments", "Error Play Local Media: "+ e.getMessage());
            e.printStackTrace();
        }
        mp.setOnPreparedListener(media -> {
            int duration = media.getDuration();
            this.seekBar.setMax(duration);
            String maxTimeString = this.millisecondsToString(duration);
            max.setText(maxTimeString);

            Track t = tracks.get(currentIndex);
            title.setText(t.getName());
            album.setText(t.getAlbum());
            artist.setText(t.getArtist());

            if (!toggleBreak) {
                playing = false;
                doStartPause();
            }
        });
    }

    private boolean enoughBatteryLevel() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getContext().registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level * 100 / (float)scale;
        return percent_limit < batteryPct;
    }

    // Convert millisecond to string.
    private String millisecondsToString(int milliseconds)  {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds =  TimeUnit.MILLISECONDS.toSeconds(milliseconds - (minutes * 60000));
        return minutes + ":" + (seconds < 10 ? "0"+seconds : seconds);
    }

    private void updateSongChangeButtons() {
        toggleBreak = false;
        if (currentIndex == 0) previousSong.setEnabled(false);
        else previousSong.setEnabled(true);
        if (currentIndex == tracks.size()-1) nextSong.setEnabled(false);
        else nextSong.setEnabled(true);
    }
    private void doChangeSong(int offset) {
        if (currentIndex + offset >= 0 && currentIndex + offset < tracks.size()) {
            currentIndex += offset;
        }
        updateSongChangeButtons();
        resetPlayer(mediaPlayer);
    }
    private void doShuffleTracks() {
        Collections.shuffle(tracks);
        currentIndex = 0;
        updateSongChangeButtons();
        resetPlayer(mediaPlayer);
    }

    private void doStartPause()  {
        if (!playing) {
            Log.d("Fragments", "not playing, player starting");
            if (!mediaPlayer.isPlaying()) mediaPlayer.start();

            start.setText("PAUSE");
            playing = true;
        } else {
            Log.d("Fragments", "playing, player pausing");
            if (mediaPlayer.isPlaying()) this.mediaPlayer.pause();

            start.setText("START");
            playing = false;
        }
    }

    private void doRewind( )  {
        int currentPosition = this.mediaPlayer.getCurrentPosition();
        // 5 seconds.
        int SUBTRACT_TIME = 5000;

        if(currentPosition - SUBTRACT_TIME > 0 )  {
            this.mediaPlayer.seekTo(currentPosition - SUBTRACT_TIME);
        }
        forward.setEnabled(true);
    }

    private void doFastForward( )  {
        int currentPosition = this.mediaPlayer.getCurrentPosition();
        int duration = this.mediaPlayer.getDuration();
        // 5 seconds.
        int ADD_TIME = 5000;

        if(currentPosition + ADD_TIME < duration)  {
            this.mediaPlayer.seekTo(currentPosition + ADD_TIME);
        }
    }


    // Thread to Update position for SeekBar.
    class UpdateSeekBarThread implements Runnable {

        public void run()  {
            if (!enoughBatteryLevel()) {
                if (!toggleBreak) {
                    toggleBreak = !toggleBreak;
                    playing = true;
                    doStartPause();
                    start.setEnabled(false);
                }
            } else {
                if (toggleBreak) {
                    toggleBreak = !toggleBreak;
                    playing = false;
                    doStartPause();
                    start.setEnabled(true);
                }
            }

            int currentPosition = mediaPlayer.getCurrentPosition();
            String currentPositionStr = millisecondsToString(currentPosition);
            current.setText(currentPositionStr);

            seekBar.setProgress(currentPosition);
            threadHandler.postDelayed(this, 50);
        }
    }

    private void resetPlayer(MediaPlayer mp) {
        mp.reset();
        setupMediaPlayer(mp);
        setupSong(mp);
    }

    private void shareTrack(Track t) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Share the current track :");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        Log.d("SHARE", sp.getString("PREFS_URL", "https://"));
        if (sp.contains("PREFS_URL")) input.setText(sp.getString("PREFS_URL", null));
        else input.setText("https://");
        builder.setView(input);
        builder.setSingleChoiceItems(methods, 0, null);
        builder.setPositiveButton("SEND", (dialog, which) -> {
            String URL = input.getText().toString();
            int pos = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
            Log.d("SHARE", methods[pos] + " " + URL);
            sp.edit()
                    .putString("PREFS_URL", URL)
                    .apply();
            input.setText(URL);
            request(URL, t, methods[pos].equals("GET") ? Request.Method.GET : Request.Method.POST);
        });

        builder.show();
    }

    public void request(String url, Track t, int request)  {
        if (request != Request.Method.GET && request != Request.Method.POST) return;

        RequestQueue queue = Volley.newRequestQueue(getContext());
        if (request == Request.Method.GET) {
            url += "?title="+t.getName()+"&artist="+t.getArtist()+"&album="+t.getAlbum();
        }
        StringRequest stringRequest = new StringRequest(request, url, response -> {
            Toast.makeText(getContext(), "track shared !", Toast.LENGTH_LONG).show();
            Log.d("SHARE", response);
        }, error -> {
            Log.d("SHARE", "error found ! " + error.toString() + " : " + error.networkResponse.statusCode);
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<>();
                params.put("title", t.getName());
                params.put("artist", t.getArtist());
                params.put("album", t.getAlbum());
                return params;
            }
        };
        queue.add(stringRequest);
    }
}