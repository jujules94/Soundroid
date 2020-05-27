package com.example.soundroid.ui.playlists;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundroid.R;
import com.example.soundroid.db.Tracklist;

import java.lang.ref.WeakReference;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private final ClickListener listener;
    private List<Tracklist> tracklists;
    private static Bitmap bitmapTracklist = null;

    public PlaylistAdapter(Context context, List<Tracklist> tracklistableList, ClickListener listener) {
        this.listener = listener;
        this.tracklists = tracklistableList;
        bitmapTracklist = loadBitMap(context);
    }

    public void setTracklists(List<Tracklist> tracklists) {
        this.tracklists = tracklists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tracklist, viewGroup, false), listener);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        holder.update(tracklists.get(position));
    }

    @Override public int getItemCount() {
        return tracklists.size();
    }

    private Bitmap loadBitMap(Context context) {
        Resources res = context.getResources();
        int id = R.mipmap.tracklist;
        return BitmapFactory.decodeResource(res, id);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name, number;
        private ImageView add, delete, music;
        private WeakReference<ClickListener> listener;

        public ViewHolder(final View itemView, ClickListener listener) {
            super(itemView);

            this.listener = new WeakReference<>(listener);
            name = itemView.findViewById(R.id.tracklist_name);
            number = itemView.findViewById(R.id.tracklist_number_tracks);
            add = itemView.findViewById(R.id.add);
            delete = itemView.findViewById(R.id.delete);
            music = itemView.findViewById(R.id.image);

            itemView.setOnClickListener(v -> listener.onItemClick(getAdapterPosition()));
            add.setOnClickListener(v -> listener.onAddClick(getAdapterPosition()));
            delete.setOnClickListener(v -> listener.onDeleteClick(getAdapterPosition()));
        }

        private void update(Tracklist list) {
            int size = list.getTracklistables().size();
            Log.d("AdapterPlaylist", "update size : " + size);
            name.setText(list.getName());
            number.setText(size + " song" + (size > 1 ? "s" : ""));
            music.setImageBitmap(bitmapTracklist);
        }
    }

    public interface ClickListener {
        void onItemClick(int pos);
        void onAddClick(int pos);
        void onDeleteClick(int pos);
    }
}