package com.example.soundroid.ui.playlists;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundroid.MainActivity;
import com.example.soundroid.R;
import com.example.soundroid.db.Track;
import com.example.soundroid.db.Tracklist;
import com.example.soundroid.db.TracklistManager;
import com.example.soundroid.db.Tracklistable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlaylistFragment extends Fragment {

    private List<Tracklist> tracklists;
    private PlaylistAdapter adapter;
    private MainActivity main;
    private List<Track> allTracks;
    private OnTracklistClickListener listenerPlayer;

    private final PlaylistAdapter.ClickListener listener = new PlaylistAdapter.ClickListener() {
        @Override
        public void onItemClick(int pos) {  //set current playlist to player fragment
            Log.d("Playlist", "item click : " + pos);
            listenerPlayer.playTracklistClicked(tracklists.get(pos));
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onAddClick(int pos) {   //add several song with a check-box dialog to the pos playlist
            Log.d("Playlist", "item add : " + pos);
            allTracks = main.getAllTracks();
            addSongsToPlaylist(tracklists.get(pos), pos);
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
        RecyclerView recyclerView = root.findViewById(R.id.recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listenerPlayer = (OnTracklistClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement the OnTrackClickListener interface");
        }
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
            .setIcon(R.drawable.ic_delete)

            .setPositiveButton("Delete", (dialog, whichButton) -> {
                tracklists.remove(tracklist);
                TracklistManager.delete(getContext(), tracklist.getHash());
                adapter.notifyDataSetChanged();
            })
            .setNegativeButton("Cancel", null)
            .create();
        myQuittingDialogBox.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addSongsToPlaylist(Tracklist tracklist, int pos) {
        String[] allTracksString = allTracks.stream().map(track -> track.getName() + " - " + track.getArtist()).toArray(String[]::new);
        ArrayList<Track> currentTracks = TracklistManager.getTracks(getContext(), tracklist);
        ArrayList<Track> newCurrentTracks = new ArrayList<>(currentTracks);
        boolean[] TracksChecked = getTracksChecked(currentTracks, allTracks);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
            .setTitle(tracklist.getName() + " : Choose some tracks to add")
            .setMultiChoiceItems(allTracksString, TracksChecked, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    Track t = allTracks.get(which);
                    if (isChecked) newCurrentTracks.add(t);
                    else newCurrentTracks.remove(t);
                    Log.d("Playlist", "toAdd : " + newCurrentTracks);
                }
            })

            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("Playlist", "adding songs : " + currentTracks + "\nto : " + tracklist.getName());
                    //check diff and put in two lists the songs to delete and the songs to add then proceed
                    List<Track> adding = newCurrentTracks.stream().filter(e -> !currentTracks.contains(e)).collect(Collectors.toList());
                    List<Track> deleting = currentTracks.stream().filter(e -> !newCurrentTracks.contains(e)).collect(Collectors.toList());
                    for (Track t : adding) {
                        TracklistManager.addTrack(getContext(), tracklist, t);
                    }
                    for (Track t : deleting) {
                        TracklistManager.deleteTrackFromTracklist(getContext(), tracklist, t);
                    }
                    Tracklist tmp = TracklistManager.get(getContext(), tracklist.getHash());
                    tracklists.set(pos, tmp);
                    adapter.notifyItemChanged(pos);
                }
            })
            .setNegativeButton("Cancel", null)
            .create();
        dialog.show();
    }

    private boolean[] getTracksChecked(List<Track> currentTracks, List<Track> allTracks) {
        Log.d("Playlist", "tracks : " + currentTracks);
        boolean[] checked = new boolean[allTracks.size()];
        for (int i = 0; i < allTracks.size(); i++) {
            Track t = allTracks.get(i);
            checked[i] = currentTracks.contains(t);
        }
        Log.d("Playlist", "checked : " + Arrays.toString(checked));
        return checked;
    }

    public interface OnTracklistClickListener {
        void playTracklistClicked(Tracklistable t);
    }
}
