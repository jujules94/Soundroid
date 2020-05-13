package com.example.soundroid.ui.gallery;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.soundroid.R;
import com.example.soundroid.db.Track;
import com.example.soundroid.db.Tracklistable;

import java.util.ArrayList;
import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {

    private ArrayList<Tracklistable> tracks;
    private OnTrackListener onTrackListener;

    public TrackAdapter(ArrayList<Tracklistable> tracks, OnTrackListener onTrackListener) {
        this.tracks = tracks;
        this.onTrackListener = onTrackListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView artistAndAlbumView;
        private TextView titleView;
        private OnTrackListener onTrackListener;

        public ViewHolder(@NonNull View itemView, OnTrackListener onTrackListener) {
            super(itemView);
            this.artistAndAlbumView = itemView.findViewById(R.id.track_artist_album);
            this.titleView = itemView.findViewById(R.id.track_title);
            this.onTrackListener = onTrackListener;
            itemView.setOnClickListener(this);
        }

        private void update(Tracklistable tracklistable) {
            Track track = (Track) tracklistable;
            artistAndAlbumView.setText(track.getArtist() + ", " + track.getAlbum());
            titleView.setText(track.getName());
        }

        @Override
        public void onClick(View v) {
            onTrackListener.onTrackClick(getAdapterPosition());
        }

    }

    @NonNull
    @Override
    public TrackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.track, viewGroup, false), onTrackListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final TrackAdapter.ViewHolder holder, final int position) {
        holder.update(tracks.get(position));
    }

    public interface OnTrackListener {
        void onTrackClick(int position);
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public List<Tracklistable> getTracks() {
        return tracks;
    }

    public void setTracks(ArrayList<Tracklistable> tracks) {
        this.tracks = tracks;
    }

}
