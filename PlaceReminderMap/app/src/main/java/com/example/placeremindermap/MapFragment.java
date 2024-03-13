package com.example.placeremindermap;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener {


    public final static String TAG = "MapFragment";
    private List<PoiDescriptor> poiDescriptorList = new ArrayList<>();
    private GoogleMap mMap;
    private Map<Marker, PoiDescriptor> poiMap;
    private AutoCompleteTextView autoCompleteTextView;

    private double lat;
    private double lng;

    private int mapType;
    private int colorPlaceholder;

    SettingsData settingsData;

    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    private static final float GEOFENCE_RADIUS = 200;

    private static final int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;

    public MapFragment() {
    }

    public MapFragment(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geofencingClient = LocationServices.getGeofencingClient(requireActivity());
        geofenceHelper = new GeofenceHelper(requireContext());
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        this.poiMap = new HashMap<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map); //find the map

        assert mapFragment != null;
        mapFragment.getMapAsync(this); //start asynchronous process to get map from Google Maps

        poiDescriptorList = HandlerFilePlaceholder.readFile(requireContext());


        //for filter
        List<String> suggestionsList = HandlerFilePlaceholder.readOnlyNameFile(requireContext());

        // Create the adapter using requireContext()
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, suggestionsList);

        autoCompleteTextView = view.findViewById(R.id.filter);

        autoCompleteTextView.setAdapter(adapter);

        //handling click on filter
        autoCompleteTextView.setOnItemClickListener((adapterView, v, position, id) -> {

            String namePlaceholder = (String) adapterView.getItemAtPosition(position);
            takePlaceholder(namePlaceholder);
        });

        //handling on the send button of the filter
        autoCompleteTextView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                String namePlaceholder = autoCompleteTextView.getText().toString();
                takePlaceholder(namePlaceholder);
                return true;
            }
            return false;
        });

        settingsData = HandlerFileSettings.readFile(requireContext());

        setMapType();
        setColorPlaceholder();

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        initMap(lat, lng);

        if (mMap != null) {
            addPoiListToMap(poiDescriptorList);
        }
    }


    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        //Listener method to receive the LatLng object associated to the clicked point on the map

        //control if the add button was pressed
        if (autoCompleteTextView.getVisibility() == View.INVISIBLE) {
            AddPlaceholderDialogFragment dialogFragment = new AddPlaceholderDialogFragment(
                    latLng.latitude, latLng.longitude);
            dialogFragment.show(requireActivity().getSupportFragmentManager(), TAG);
        }
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        PoiDescriptor myPoiDescriptor = poiMap.get(marker);

        // Pressing on the window of a marker gives you the possibility to modify it
        AddOrEditPlaceholderReverseGeocodingDialogFragment dialogFragment =
                new AddOrEditPlaceholderReverseGeocodingDialogFragment(myPoiDescriptor);

        dialogFragment.show(requireActivity().getSupportFragmentManager(), "MapFragment");
    }

    private void initMap(double lat, double lng) {
        if (mMap != null) {
            //Create a geographic point starting from latitude and longitude coordinate
            LatLng mapCenter = new LatLng(lat, lng);

            //Move the map to a specific point
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 16));

            //Enable or disable user location layer
            enableUserLocation();

            //Change map Type
            mMap.setMapType(mapType);

            mMap.setOnMapClickListener(this);

            mMap.setOnInfoWindowClickListener(this);
        }
    }

    private void enableUserLocation() {

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void takePlaceholder(String namePlaceholder) {
        PoiDescriptor poiDescriptor = HandlerFilePlaceholder.recoveryPlaceholder(requireContext(), namePlaceholder);
        if (poiDescriptor != null) {
            autoCompleteTextView.setText("");
            initMap(poiDescriptor.getLat(), poiDescriptor.getLng());
        } else {
            Toast.makeText(requireContext(), requireActivity().getString(
                    R.string.ERROR_DONT_EXIST_PLACEHOLDER, namePlaceholder), Toast.LENGTH_SHORT).show();
            getExitTransition();
        }

        //close keyboard
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
    }

    private void addGeofence(PoiDescriptor poiDescriptor) {

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Geofence geofence = geofenceHelper.getGeofence(poiDescriptor, GEOFENCE_RADIUS,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        geofencingClient.addGeofences(geofencingRequest, pendingIntent);

    }

    private void addCircle(LatLng latLng) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(GEOFENCE_RADIUS);
        circleOptions.strokeColor(Color.argb(255, 176, 177, 183));
        circleOptions.fillColor(Color.argb(64, 176, 177, 183));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }

    private void addPoiListToMap(List<PoiDescriptor> poiDescriptorList) {
        for (PoiDescriptor poiDescriptor : poiDescriptorList) {
            addPoiToMap(poiDescriptor);
        }
    }

    private void addPoiToMap(PoiDescriptor poiDescriptor) {

        if (poiDescriptor != null) {

            LatLng latLng = new LatLng(poiDescriptor.getLat(), poiDescriptor.getLng());

            //Create new marker options

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(poiDescriptor.getName());
            markerOptions.snippet(poiDescriptor.getDescription());
            markerOptions.position(latLng);

            //Change the default icon using a drawable resource
            markerOptions.icon(BitmapDescriptorFactory.fromResource(colorPlaceholder));

            //Add the marker to the Map and return the created marker object
            Marker marker = mMap.addMarker(markerOptions);
            this.poiMap.put(marker, poiDescriptor);


            addCircle(latLng);
            addGeofence(poiDescriptor);

            if (Build.VERSION.SDK_INT >= 29) {
                //We need background permission
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    addCircle(latLng);
                    addGeofence(poiDescriptor);
                } else {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            } else {
                addCircle(latLng);
                addGeofence(poiDescriptor);
            }
        }
    }



    private void setMapType() {
        switch (settingsData.getTypeMap()) {
            case "Ibrida":
                mapType = GoogleMap.MAP_TYPE_HYBRID;
                break;
            case "Satellite":
                mapType = GoogleMap.MAP_TYPE_SATELLITE;
                break;
            case "Terrestre":
                mapType = GoogleMap.MAP_TYPE_TERRAIN;
                break;
            default:  //case "Normale"
                mapType = GoogleMap.MAP_TYPE_NORMAL;
                break;
        }
    }

    private void setColorPlaceholder() {
        switch (settingsData.getColorPlaceholder()) {
            case "Verde":
                colorPlaceholder = R.drawable.ic_location_place_green;
                break;
            case "Arancione":
                colorPlaceholder = R.drawable.ic_location_place_orange;
                break;
            case "Rosa":
                colorPlaceholder = R.drawable.ic_location_place_pink;
                break;
            case "Viola":
                colorPlaceholder = R.drawable.ic_location_place_purple;
                break;
            case "Rosso":
                colorPlaceholder = R.drawable.ic_location_place_red;
                break;
            case "Giallo":
                colorPlaceholder = R.drawable.ic_location_place_yellow;
                break;
            default:  //case "Blu"
                colorPlaceholder = R.drawable.ic_location_place_blue;
                break;
        }
    }



}