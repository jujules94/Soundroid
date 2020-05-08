package com.example.soundroid.ui.player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.soundroid.Audio;
import com.example.soundroid.MusicIndexer;
import com.example.soundroid.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlayerFragment extends Fragment {

    private Handler threadHandler = new Handler();
    private MediaPlayer mediaPlayer;
    private List<Audio> tracks;
    private int currentIndex = 1;
    private boolean playing = false;

    private TextView title, album, artist, current, max;
    private SeekBar seekBar;
    private Button rewind, start, forward;
    //private ImageButton previousSong, shuffle, nextSong;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.d("Fragments", "retrieving arguments");
            tracks = (ArrayList<Audio>) getArguments().get("tracks");
        }
        tracks = MusicIndexer.indexMusic(getContext());
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

        //previousSong = root.findViewById(R.id.previousSong);
        //shuffle = root.findViewById(R.id.shuffle);
        //nextSong = root.findViewById(R.id.nextSong);

        start.setOnClickListener(v -> {
            doStartPause();
        });
        rewind.setOnClickListener(v -> {
            doRewind();
        });
        forward.setOnClickListener(v -> {
            doFastForward();
        });
        /*previousSong.setOnClickListener(v -> {
            doChangeSong(-1);
        });
        shuffle.setOnClickListener(v -> {
            doShuffleTracks();
            Log.d("Fragments", tracks.toString());
        });
        nextSong.setOnClickListener(v -> {
            doChangeSong(1);
        });*/

        // Create MediaPlayer.
        createMediaPlayer();
        setupSong();
        // Create a thread to update position of SeekBar.
        UpdateSeekBarThread updateSeekBarThread= new UpdateSeekBarThread();
        threadHandler.postDelayed(updateSeekBarThread,50);
        return root;
    }

    private void createMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnPreparedListener(media -> {
            doStop(); // Stop current media.
        });
        mediaPlayer.setOnCompletionListener(media -> {
            doComplete();
        });
    }

    // Called by MediaPlayer.OnCompletionListener
    // When Player cocmplete
    private void doComplete()  {
        Log.d("Fragments", "song completed");
        /*if (tracks.size() > currentIndex + 1) {
            resetPlayer();
        } else {
            start.setEnabled(false);
            rewind.setEnabled(true);
            forward.setEnabled(false);
        }*/
    }

    // Called by MediaPlayer.OnPreparedListener.
    // When user select a new media source, then stop current.
    private void doStop()  {
        if(this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.stop();
        }
        rewind.setEnabled(false);
        forward.setEnabled(false);
        //updateSongChangeButtons();
    }

    private void setupSong() {
        try {
            Log.d("Fragments", "media selected : " + tracks.get(currentIndex));
            Uri uri = tracks.get(currentIndex).uri;
            Toast.makeText(getContext(),"Select source: "+ uri,Toast.LENGTH_SHORT).show();
            mediaPlayer.setDataSource(getContext(), uri);
            mediaPlayer.prepareAsync();
        } catch(Exception e) {
            Log.e("Fragments", "Error Play Local Media: "+ e.getMessage());
            Toast.makeText(getContext(),"Error Play Local Media: "+ e.getMessage(),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(mp -> {
            int duration = mediaPlayer.getDuration();
            this.seekBar.setMax(duration);
            String maxTimeString = this.millisecondsToString(duration);
            max.setText(maxTimeString);
        });
    }

    // Convert millisecond to string.
    private String millisecondsToString(int milliseconds)  {
        long minutes = TimeUnit.MILLISECONDS.toMinutes((long) milliseconds);
        long seconds =  TimeUnit.MILLISECONDS.toSeconds((long) milliseconds) ;
        return minutes + ":"+ seconds;
    }

    /*private void updateSongChangeButtons() {
        if (currentIndex == 0) previousSong.setEnabled(false);
        else previousSong.setEnabled(true);
        if (currentIndex == tracks.size()-1) nextSong.setEnabled(false);
        else nextSong.setEnabled(true);
    }

    private void doChangeSong(int offset) {
        if (currentIndex + offset >= 0 && currentIndex + offset < tracks.size()) {
            currentIndex += offset;
        }
        setCurrentSong();
    }

    private void doShuffleTracks() {
        Collections.shuffle(tracks);
        currentIndex = 0;
    }*/

    private void doStartPause( )  {
        // The duration in milliseconds
        if (!playing) {
            Log.d("Fragments", "not playing, player starting");
            this.mediaPlayer.start();

            rewind.setEnabled(true);
            forward.setEnabled(true);
            start.setText("PAUSE");
            playing = true;
        } else {
            Log.d("Fragments", "playing, player pausing");
            this.mediaPlayer.pause();

            start.setText("START");
            playing = false;
        }
        //updateSongChangeButtons();
    }

    // When user click to "Rewind".
    private void doRewind( )  {
        int currentPosition = this.mediaPlayer.getCurrentPosition();
        int duration = this.mediaPlayer.getDuration();
        // 5 seconds.
        int SUBTRACT_TIME = 5000;

        if(currentPosition - SUBTRACT_TIME > 0 )  {
            this.mediaPlayer.seekTo(currentPosition - SUBTRACT_TIME);
        }
        forward.setEnabled(true);
    }

    // When user click to "Fast-Forward".
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
            // Delay thread 50 milisecond.
            Log.d("Fragments", mediaPlayer.getCurrentPosition() + " " + mediaPlayer.getDuration());
            threadHandler.postDelayed(this, 50);
        }
    }

    private void resetPlayer() {
        currentIndex++;
        setupSong();
        mediaPlayer.start();
    }
}