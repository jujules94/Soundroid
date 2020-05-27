package com.example.soundroid.ui.history;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.soundroid.R;
import com.example.soundroid.db.History;
import com.example.soundroid.db.Track;
import com.example.soundroid.db.TrackManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private ArrayList<History> historys;
    private final Context context;

    public HistoryAdapter(ArrayList<History> historys, Context context) {
        this.historys = historys;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView artistAndAlbumView;
        private TextView titleView;
        private TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.artistAndAlbumView = itemView.findViewById(R.id.track_artist_album);
            this.titleView = itemView.findViewById(R.id.track_title);
            this.time = itemView.findViewById(R.id.time);
        }

        private void update(History history) {
            Track track = TrackManager.get(context, history.getHash());
            artistAndAlbumView.setText(track.getArtist() + ", " + track.getAlbum());
            titleView.setText(track.getName());
            Date now = new Date();
            long diffInMillies = Math.abs(now.getTime() - history.getDate());
            time.setText(String.format("played %02d hour(s), %02d min ago",
                    TimeUnit.MILLISECONDS.toHours(diffInMillies),
                    TimeUnit.MILLISECONDS.toMinutes(diffInMillies) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(diffInMillies))
            ));

        }
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final HistoryAdapter.ViewHolder holder, final int position) {
        holder.update(historys.get(position));
    }

    @Override
    public int getItemCount() {
        return historys.size();
    }

}
