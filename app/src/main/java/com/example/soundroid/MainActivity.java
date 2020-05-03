package com.example.soundroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private SharedPreferences sp;
    private final String[] methods = new String[]{"GET", "POST"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sp = getSharedPreferences("PREFS", MODE_PRIVATE);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            //Snackbar.make(view, "SHARE !", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            shareTrack();
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
    }

    private void shareTrack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Partage ta musique :");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        Log.d("SHARE", sp.getString("PREFS_URL", "https://"));
        if (sp.contains("PREFS_URL")) input.setText(sp.getString("PREFS_URL", null));
        else input.setText("https://");
        builder.setView(input);
        builder.setSingleChoiceItems(methods, 0, null);
        builder.setPositiveButton("ENVOI", (dialog, which) -> {
            String URL = input.getText().toString();
            int pos = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
            Log.d("SHARE", methods[pos] + " " + URL);
            sp.edit()
                .putString("PREFS_URL", URL)
                .apply();
            input.setText(URL);
            request(URL, methods[pos].equals("GET") ? Request.Method.GET : Request.Method.POST);
        });

        builder.show();
    }

    public void request(String url, int request)  {
        if (request != Request.Method.GET && request != Request.Method.POST) return;

        RequestQueue queue = Volley.newRequestQueue(this);
        if (request == Request.Method.GET) {
            //add args to url;
        }
        StringRequest stringRequest = new StringRequest(request, url, response -> {
            Toast.makeText(this, "musique partagée !", Toast.LENGTH_LONG).show();
            Log.d("SHARE", response);
        }, error -> {
            Log.d("SHARE", "error found ! " + error.toString() + " : " + error.networkResponse.statusCode);
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<>();
                //add here the fields of the track
                return params;
            }
        };
        queue.add(stringRequest);
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
