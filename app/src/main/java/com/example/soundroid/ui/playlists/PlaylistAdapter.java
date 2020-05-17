package com.example.soundroid.ui.playlists;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundroid.R;
import com.example.soundroid.db.Tracklist;
import com.example.soundroid.db.Tracklistable;

import java.lang.ref.WeakReference;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private final ClickListener listener;
    private final List<Tracklistable> tracklistableList;

    public PlaylistAdapter(List<Tracklistable> tracklistableList, ClickListener listener) {
        this.listener = listener;
        this.tracklistableList = tracklistableList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tracklist, viewGroup, false), listener);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        holder.update(tracklistableList.get(position));
    }

    @Override public int getItemCount() {
        return tracklistableList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name, number;
        private ImageView add, delete;
        private WeakReference<ClickListener> listener;

        public ViewHolder(final View itemView, ClickListener listener) {
            super(itemView);

            this.listener = new WeakReference<>(listener);
            name = (TextView) itemView.findViewById(R.id.tracklist_name);
            number = (TextView) itemView.findViewById(R.id.tracklist_number_tracks);
            add = (ImageView) itemView.findViewById(R.id.add);
            delete = (ImageView) itemView.findViewById(R.id.delete);

            itemView.setOnClickListener(v -> {
                listener.onItemClick(getAdapterPosition());
            });
            add.setOnClickListener(v -> {
                listener.onAddClick(getAdapterPosition());
            });
            delete.setOnClickListener(v -> {
                listener.onDeleteClick(getAdapterPosition());
            });
        }

        private void update(Tracklistable tracklistable) {
            Tracklist list = (Tracklist) tracklistable;
            int size = list.getTracklistables().size();
            name.setText(list.getName());
            number.setText(size + " song" + (size > 1 ? "s" : ""));
        }
    }

    public interface ClickListener {
        void onItemClick(int pos);
        void onAddClick(int pos);
        void onDeleteClick(int pos);
    }
}