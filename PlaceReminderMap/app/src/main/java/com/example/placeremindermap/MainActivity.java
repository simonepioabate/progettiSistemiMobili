package com.example.placeremindermap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;



/**
 * Created by Simone Pio Abate (simonepio.abate@studenti.unipr.it) 05/10/2023
 * Simple Activity and application to explore and save your special places.
 * Add custom placeholders and make the most of the Google Maps API and UI components
 */
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton addButton;
    private FloatingActionButton closeButton;

    private static final int REQUEST_PERMISSIONS = 10001;
    private static final int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;

    private int fineLocationPermission;
    private int backgroundLocationPermission;
    private int notificationPermission;

    SettingsData settingsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HandlerFile.createFile(getBaseContext(), getString(R.string.FILE_PLACEHOLDER));

        if(HandlerFile.createFile(getBaseContext(), getString(R.string.FILE_SETTINGS))){
            HandlerFile.writeFile(getBaseContext(), getString(R.string.FILE_SETTINGS),
                    "44.76516282282244\n" +    //lat
                          "10.311720371246338\n" +   //lng
                          "Normale\n" +              //type map
                          "Blu");                    //color placeholder
        }


        setContentView(R.layout.activity_main);

        settingsData = HandlerFileSettings.readFile(getBaseContext());


        fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        backgroundLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        notificationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS);


        //chiedi permessi se non ci sono
        if (fineLocationPermission != PackageManager.PERMISSION_GRANTED ||
               notificationPermission != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions( this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.POST_NOTIFICATIONS },
                    REQUEST_PERMISSIONS);
        }


       //controllo permessi e ti comporti di conseguenza
       if(fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
               notificationPermission == PackageManager.PERMISSION_GRANTED &&
               backgroundLocationPermission == PackageManager.PERMISSION_GRANTED){
           //open map
           replaceFragmentWithMapFragment(settingsData.getLatCenter(), settingsData.getLngCenter());
       } else if(fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
               notificationPermission == PackageManager.PERMISSION_GRANTED){
           ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);

       } else{

           getSupportFragmentManager().beginTransaction()
                   .replace(R.id.fragmentContainer, new PermissionFragment())
                   .commit();
       }

        //open map
        //replaceFragmentWithMapFragment(settingsData.getLatCenter(), settingsData.getLngCenter());


        addButton = findViewById(R.id.addPlaceholder);
        closeButton = findViewById(R.id.closeAddPlaceholder);

        handlerButtonMenu();

        //handler buttons
        addButton.setOnClickListener(this::addOnClick);
        closeButton.setOnClickListener(this::closeOnClick);

    }


    private void handlerButtonMenu() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener (item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {

                settingsData = HandlerFileSettings.readFile(getBaseContext());

                //open map
                replaceFragmentWithMapFragment(settingsData.getLatCenter(), settingsData.getLngCenter());

                findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);

                findViewById(R.id.addPlaceholder).setVisibility(View.VISIBLE);

            } else if (itemId == R.id.saved_placeholders) {

                replaceFragmentWithSavedPlaceholderFragment();

                findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);

                findViewById(R.id.addPlaceholder).setVisibility(View.INVISIBLE);

            } else if (itemId == R.id.settings) {

                replaceFragmentWithSettingsFragment();

                findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);

                findViewById(R.id.addPlaceholder).setVisibility(View.INVISIBLE);
            }

            return true;
        });

    }

    public void replaceFragmentWithMapFragment(double lat, double lng) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new MapFragment(lat, lng))
                .commit();
    }
    public void replaceFragmentWithSavedPlaceholderFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new SavedPlaceholdersFragment())
                .commit();
    }

    public void replaceFragmentWithSettingsFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new SettingsFragment())
                .commit();
    }


    public void addOnClick(View view) {
        bottomNavigationView.setVisibility(View.GONE);
        addButton.setVisibility(View.INVISIBLE);
        closeButton.setVisibility(View.VISIBLE);
        findViewById(R.id.filter).setVisibility(View.INVISIBLE);
    }

    public void closeOnClick(View view) {
        bottomNavigationView.setVisibility(View.VISIBLE);
        addButton.setVisibility(View.VISIBLE);
        closeButton.setVisibility(View.INVISIBLE);
        findViewById(R.id.filter).setVisibility(View.VISIBLE);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        backgroundLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        notificationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS);

        if(requestCode == REQUEST_PERMISSIONS) {
            if (fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                    notificationPermission == PackageManager.PERMISSION_GRANTED) {

                if (Build.VERSION.SDK_INT >= 29) {

                    if (backgroundLocationPermission == PackageManager.PERMISSION_GRANTED) {
                        settingsData = HandlerFileSettings.readFile(getBaseContext());

                        //open map
                        replaceFragmentWithMapFragment(settingsData.getLatCenter(), settingsData.getLngCenter());
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                            //We show a dialog and ask for permission
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                        } else {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                        }
                    }
                }

            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new PermissionFragment())
                        .commit();
            }
        }
        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
                settingsData = HandlerFileSettings.readFile(getBaseContext());

                //open map
                replaceFragmentWithMapFragment(settingsData.getLatCenter(), settingsData.getLngCenter());
            }
        }
    }


    public void openSettings(View view) {


        int fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int backgroundLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        int notificationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS);

        if (notificationPermission == PackageManager.PERMISSION_GRANTED &&
                fineLocationPermission == PackageManager.PERMISSION_GRANTED) {

            if (backgroundLocationPermission == PackageManager.PERMISSION_GRANTED) {
                settingsData = HandlerFileSettings.readFile(getBaseContext());

                //open map
                replaceFragmentWithMapFragment(settingsData.getLatCenter(), settingsData.getLngCenter());
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }
        } else {
            // The user has not granted permission to the location.
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
       }
    }
}