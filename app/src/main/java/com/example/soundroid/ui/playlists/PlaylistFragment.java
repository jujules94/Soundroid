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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.example.soundroid.MainActivity;
import com.example.soundroid.R;
import com.example.soundroid.db.Track;
import com.example.soundroid.db.Tracklist;
import com.example.soundroid.db.TracklistManager;
import com.example.soundroid.db.Tracklistable;
import com.example.soundroid.ui.research.TrackAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class PlaylistFragment extends Fragment {

    private List<Tracklist> tracklists;
    private PlaylistAdapter adapter;
    private RecyclerView recyclerView;
    private MainActivity main;
    private List<Track> allTracks;

    private final PlaylistAdapter.ClickListener listener = new PlaylistAdapter.ClickListener() {
        @Override
        public void onItemClick(int pos) {  //set current playlist to player fragment
            Log.d("Playlist", "item click : " + pos);
        }

        @Override
        public void onAddClick(int pos) {   //add several song with a check-box dialog to the pos playlist
            Log.d("Playlist", "item add : " + pos);
            allTracks = main.getAllTracks();
            addSongsToPlaylist(tracklists.get(pos));
        }

        @Override
        public void onDeleteClick(int pos) { //delete pos playlist
            Log.d("Playlist", "item delete : " + pos);
            removePlaylist(tracklists.get(pos));
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_playlist, container, false);
        main = (MainActivity) getActivity();
        FloatingActionButton fab = root.findViewById(R.id.add_playlist);
        fab.setOnClickListener(view -> addPlaylist());

        tracklists = TracklistManager.getAll(getContext());
        Log.d("Playlist", "playlists : " + tracklists);
        adapter = new PlaylistAdapter(getContext(), tracklists, listener);
        recyclerView = root.findViewById(R.id.recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        return root;
    }

    private void addPlaylist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add a new playlist :");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        input.setHint("type the name of the new playlist");
        builder.setView(input);
        builder.setPositiveButton("CREATE", (dialog, which) -> {
            String name = input.getText().toString();
            Tracklist tracklist = new Tracklist(name);
            if (TracklistManager.add(getContext(), tracklist)) {
                Toast.makeText(getContext(), "The tracklist has been created !", Toast.LENGTH_LONG).show();
                tracklists.add(tracklist);
                Log.d("Playlist", "playlists : " + tracklists);
                adapter.setTracklists(tracklists);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "The tracklist can't be added. Name already taken or SQL error.", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    private void removePlaylist(Tracklist tracklist) {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(getContext())
            // set message, title, and icon
            .setTitle("Delete " + tracklist.getName())
            .setMessage("Do you really want to delete this playlist ?")
            .setIcon(R.drawable.ic_shuffle)

            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //your deleting code
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            })
            .create();
        myQuittingDialogBox.show();
    }

    private void addSongsToPlaylist(Tracklist tracklist) {
        String[] allTracksString = allTracks.stream().map(track -> track.getName() + " - " + track.getArtist()).toArray(String[]::new);
        ArrayList<Track> currentTracks = new ArrayList<>();
        boolean[] TracksChecked = getTracksChecked(tracklist, currentTracks);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
            .setTitle(tracklist.getName() + " : Choose some tracks to add")
            .setMultiChoiceItems(allTracksString, TracksChecked, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    Track t = allTracks.get(which);
                    if (isChecked)currentTracks.add(t);
                    else currentTracks.remove(t);
                    Log.d("Playlist", "toAdd : " + currentTracks);
                }
            })

            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("Playlist", "adding songs : " + currentTracks + "\nto : " + tracklist.getName());
                }
            })
            .setNegativeButton("Cancel", null)
            .create();
        dialog.show();
    }

    private boolean[] getTracksChecked(Tracklist tracklist, ArrayList<Track> currentTracks) {

        return new boolean[0];
    }
}
