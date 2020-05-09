package com.example.soundroid.ui.player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.soundroid.R;
import com.example.soundroid.db.Track;
import com.example.soundroid.db.TrackManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlayerFragment extends Fragment {

    private Handler threadHandler = new Handler();
    private MediaPlayer mediaPlayer;
    private List<Track> tracks;
    private int currentIndex = 0;
    private boolean playing = false;

    private TextView title, album, artist, current, max;
    private SeekBar seekBar;
    private Button rewind, start, forward;
    private ImageButton previousSong, shuffle, nextSong;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.d("Fragments", "retrieving arguments");
            tracks = (ArrayList<Track>) getArguments().get("tracks");
        }
        tracks = TrackManager.getAll(getContext());
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

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

    private void setupMediaPlayer(MediaPlayer mp) {
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
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

            playing = false;
            doStartPause();
        });
    }

    // Convert millisecond to string.
    private String millisecondsToString(int milliseconds)  {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds =  TimeUnit.MILLISECONDS.toSeconds(milliseconds - (minutes * 60000));
        return minutes + ":" + (seconds < 10 ? "0"+seconds : seconds);
    }

    private void updateSongChangeButtons() {
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
}