package com.example.soundroid.ui.history;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundroid.R;
import com.example.soundroid.db.History;
import com.example.soundroid.db.HistoryManager;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    private Handler handler = new Handler();
    private HistoryAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        ArrayList<History> historys = HistoryManager.getRows(getContext());
        adapter = new HistoryAdapter(historys, getContext());
        RecyclerView recyclerView = root.findViewById(R.id.recycler_hystory);
        TextView number = root.findViewById(R.id.nb);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        number.setText("Tracks played : " + historys.size());
        updateTimeRunnable.run();
        return root;
    }

    private Runnable updateTimeRunnable = () -> {
        adapter.notifyDataSetChanged();
        handler.postDelayed(getUpdateTimeRunnable(), 60000);
    };

    private Runnable getUpdateTimeRunnable() {
        return updateTimeRunnable;
    }
}
