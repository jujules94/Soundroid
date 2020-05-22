package com.example.soundroid;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.soundroid.db.Track;
import com.example.soundroid.db.TrackManager;
import com.example.soundroid.db.Tracklist;
import com.example.soundroid.db.TracklistManager;
import com.example.soundroid.db.Tracklistable;
import com.example.soundroid.ui.player.PlayerFragment;
import com.example.soundroid.ui.playlists.PlaylistFragment;
import com.example.soundroid.ui.research.ResearchFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PlaylistFragment.OnTracklistClickListener, ResearchFragment.OnTrackClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    private Handler handler = new Handler();
    private List<Track> selectedTracks = new ArrayList<>();
    private List<Track> allTracks;  //handler to reindex and set this variable

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkPermissionForReadExternalStorage()) requestPermissionForReadExternalStorage();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            switch(destination.getId()) {
                case R.id.nav_home :
                    NavArgument argument = new NavArgument.Builder().setDefaultValue(selectedTracks).build();
                    Log.d("Fragments", "set arguments to home fragment");
                    destination.addArgument("tracks", argument);
                    break;
            }
        });
        NavigationUI.setupWithNavController(navigationView, navController);

        indexerRunnable.run();
    }

    private boolean checkPermissionForReadExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    private void requestPermissionForReadExternalStorage() {
        try {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.import_bdd:
                //import
                return true;
            case R.id.export_bdd:
                //export
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public List<Track> getAllTracks() {
        return allTracks;
    }
    public List<Track> getSelectedTracks() {return selectedTracks;}

    private Runnable indexerRunnable = () -> {
        MusicIndexer.indexMusic(this);
        allTracks = TrackManager.getAll(getApplicationContext());
        handler.postDelayed(getIndexerRunnable(), 60000);
    };

    /** Required to allow recursive call from increment runnable */
    private Runnable getIndexerRunnable() {
        return indexerRunnable;
    }

    @Override
    public void playTracklistClicked(Tracklistable t) {
        Log.d("Fragments", "tracklist clicked : " + t.isTrack());
        if (!t.isTrack()) {
            Tracklist list = (Tracklist) t;
            ArrayList<Track> tmp = TracklistManager.getTracks(this, list);
            selectedTracks = tmp;
        }
    }

    @Override
    public void playTrackClicked(Tracklistable t) {
        Log.d("Fragments", "track clicked : " + t.isTrack());
        if (t.isTrack()) {
            ArrayList<Track> tmp = new ArrayList<>();
            tmp.add((Track) t);
            selectedTracks = tmp;
        }
    }
}
