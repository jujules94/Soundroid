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
import com.example.soundroid.db.Tracklistable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    SimpleDateFormat formater = new SimpleDateFormat("'played 'h' hour(s) 'mm' min ago'");
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
            Log.d("History", track.toString());
            artistAndAlbumView.setText(track.getArtist() + ", " + track.getAlbum());
            titleView.setText(track.getName());
            Date now = new Date();
            long diffInMillies = Math.abs(now.getTime() - history.getDate());
            Date diff = new Date(diffInMillies);
            time.setText(formater.format(diff));

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

    public List<History> getHistorys() {
        return historys;
    }

    public void setHistorys(ArrayList<History> historys) {
        this.historys = historys;
    }

}
