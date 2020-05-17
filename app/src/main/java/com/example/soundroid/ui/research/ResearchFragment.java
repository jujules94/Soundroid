package com.example.soundroid.ui.research;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundroid.R;
import com.example.soundroid.db.TrackManager;
import com.example.soundroid.db.Tracklist;
import com.example.soundroid.db.TracklistManager;
import com.example.soundroid.db.Tracklistable;

import java.util.ArrayList;

public class ResearchFragment extends Fragment implements TrackAdapter.OnTrackListener {

    private ArrayList<Tracklistable> tracks = new ArrayList<>();
    private TrackAdapter trackAdapter;
    private RecyclerView recyclerView;
    private EditText searchTracksText;
    private Spinner searchTracksSpinner;
    private Button searchTracksButton;
    private LinearLayout createPlaylistLayout;
    private EditText createPlaylistText;
    private Button createPlaylistButton;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.searchTracksText = (EditText) view.findViewById(R.id.search_filter);
        this.searchTracksSpinner = (Spinner) view.findViewById(R.id.search_spinner);
        this.searchTracksButton = (Button) getView().findViewById(R.id.search_button);
        this.createPlaylistLayout = (LinearLayout) view.findViewById(R.id.create_tracklist_layout);
        this.createPlaylistText = (EditText) getView().findViewById(R.id.search_tracklist_name);
        this.createPlaylistButton = (Button) getView().findViewById(R.id.search_create_tracklist);
        /* initialize fields */
        createPlaylistLayout.setVisibility(TextView.INVISIBLE);
        searchTracksButton.setOnClickListener(new View.OnClickListener() {
            /** Search tracks */
            @Override
            public void onClick(View v) {
                String filter = searchTracksText.getText().toString();
                String spinner = searchTracksSpinner.getSelectedItem().toString();
                tracks = TrackManager.get(getContext(),
                        spinner.equals("artist") ? filter : null,
                        spinner.equals("album") ? filter : null,
                        spinner.equals("title") ? filter : null);
                Log.d("Search", tracks.toString());
                trackAdapter.setTracks(tracks);
                trackAdapter.notifyDataSetChanged();
                if (!tracks.isEmpty()) {
                    createPlaylistLayout.setVisibility(TextView.VISIBLE);
                } else {
                    createPlaylistLayout.setVisibility(TextView.INVISIBLE);
                    Toast.makeText(getContext(), "No track found.", Toast.LENGTH_LONG);
                }
            }
        });
        createPlaylistButton.setEnabled(false);
        createPlaylistButton.setOnClickListener(new View.OnClickListener() {
            /** Create playlist */
            @Override
            public void onClick(View v) {
                String name = ((EditText) getView().findViewById(R.id.search_tracklist_name)).getText().toString();
                Tracklist tracklist = new Tracklist(name, name, tracks);
                if (TracklistManager.create(getContext(), tracklist)) {
                    Toast.makeText(getContext(), "The tracklist has been created !", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "The tracklist can't be added. Name already taken or SQL error.", Toast.LENGTH_LONG).show();
                }
            }
        });
        /** Deactivate button if playlist name is empty */
        createPlaylistText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    createPlaylistButton.setEnabled(false);
                } else {
                    createPlaylistButton.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
        /* run RecyclerView */
        trackAdapter = new TrackAdapter(new ArrayList<>(), this);
        recyclerView = (RecyclerView) getView().findViewById(R.id.search_recyclerview);
        recyclerView.setAdapter(trackAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
    }

    @Override
    public void onTrackClick(int position) {
        // to do
        // L'utilisateur viens de cliquer sur une musique.
        // il faut que le player lance la musique et qu'on retourne sur l'affichage du player.
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_research, container, false);
        return root;
    }

}
