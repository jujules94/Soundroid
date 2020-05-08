package com.example.soundroid;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;

import com.example.soundroid.db.Track;
import com.example.soundroid.db.TrackManager;
import com.example.soundroid.db.Tracklist;
import com.example.soundroid.db.TracklistManager;
import com.example.soundroid.db.Tracklistable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "SHARE !", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // test database
        new Thread(new Runnable() {
            @Override
            public void run() {
                Context context = getApplicationContext();
                /* Track Tests*/
                Track rapeMe = new Track("Nirvana","In Utero","Rape Me",1,1, 320, System.currentTimeMillis() ,2, 49);
                Log.d("A001", String.valueOf(TrackManager.add(context, rapeMe)));
                Log.d("A002", TrackManager.get(context, rapeMe.getHash()).toString());
                /* Tracklist Tests*/
                ArrayList<Tracklistable> TL1 = new ArrayList<>(); TL1.add(rapeMe);
                Tracklist T1 = new Tracklist("Sample", "Sample", TL1);
                ArrayList<Tracklistable> TL2 = new ArrayList<>(); TL2.add(rapeMe); TL2.add(T1);
                TracklistManager.delete(context, "In Utero");
                TracklistManager.delete(context, "Sample");
                Tracklist T2 = new Tracklist("In Utero", "In Utero", TL2);
                Log.d("A003", String.valueOf(TracklistManager.create(context, T2)));
                Log.d("A005", String.valueOf(TracklistManager.create(context, T1)));
                Log.d("A004", TracklistManager.get(context, "In Utero").toString());
            }
        }).start();

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
}
