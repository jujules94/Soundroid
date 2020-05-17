package com.example.soundroid.ui.playlists;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.example.soundroid.R;
import com.example.soundroid.db.Track;
import com.example.soundroid.db.Tracklist;
import com.example.soundroid.db.TracklistManager;
import com.example.soundroid.db.Tracklistable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class PlaylistFragment extends Fragment implements PlaylistAdapter.ClickListener {

    private SharedPreferences sp;
    private List<Tracklistable> tracklistableList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_playlist, container, false);

        sp = getActivity().getSharedPreferences("PREFS", MODE_PRIVATE);

        FloatingActionButton fab = root.findViewById(R.id.add_playlist);
        fab.setOnClickListener(view -> addPlaylist());

        return root;
    }

    private void addPlaylist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add a new playlist :");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        input.setHint("type the name of the new playlist");
        builder.setView(input);
        builder.setPositiveButton("SEND", (dialog, which) -> {
            String name = input.getText().toString();
            Tracklist tracklist = new Tracklist(name);
            if (TracklistManager.create(getContext(), tracklist)) {
                Toast.makeText(getContext(), "The tracklist has been created !", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "The tracklist can't be added. Name already taken or SQL error.", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    @Override
    public void onItemClick(int pos) {  //set current playlist to player fragment

    }

    @Override
    public void onAddClick(int pos) {   //add several song with a check-box dialog to the pos playlist

    }

    @Override
    public void onDeleteClick(int pos) { //delete pos playlist

    }
}
