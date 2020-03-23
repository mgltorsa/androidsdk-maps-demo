package com.example.dimy.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dimy.R;
import com.example.dimy.model.Marker;
import com.example.dimy.model.Markers;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    private static final double MIN_DISTANCE = 8;
    private final float DEFAULT_ZOOM = 20;
    private final int PERMISSION_REQUEST_CODE = 11;
    private final float DEFAULT_DISTANCE = 20;

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Geocoder geocoder;

    private String yourPositionMessage;
    private boolean markerMode = false;
    private Markers markers;
    private double minDistance = Double.MAX_VALUE;
    private TextView distanceBox;
    private DecimalFormat formatter = new DecimalFormat("#0.00");

    private LatLng selectedPosition;
    private LatLng currentPosition;
    private AlertDialog markerDialog;
    private EditText markerInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        geocoder = new Geocoder(this, Locale.getDefault());
        markers = new Markers();

        yourPositionMessage = getString(R.string.your_position);

        Button markerButton = findViewById(R.id.markerButton);
        markerButton.setOnClickListener((view) -> {
            markerMode = true;
            Toast.makeText(this, "Seleccione el mapa para maracar", Toast.LENGTH_LONG).show();
        });

        Button resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener((view)->{
            markers.clear();
            this.distanceBox.setText("");
            minDistance=Double.MAX_VALUE;
        });


        markerInput = new EditText(this);
        markerInput.setInputType(InputType.TYPE_CLASS_TEXT);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Type a name for your marker").setTitle("Marker creation").setView(markerInput);
        builder.setPositiveButton("OK", (dialog, which) -> {
            String title = null;
            title = markerInput.getText().toString();
            title = !title.isEmpty() ? title : "No named";


            addMarker(selectedPosition, title, getSnippets(selectedPosition.latitude, selectedPosition.longitude));
            markerMode = false;

            dialog.dismiss();

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            mMap.clear();
            markerMode = false;
            dialog.cancel();

        });

        markerDialog = builder.create();

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET},
                PERMISSION_REQUEST_CODE);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        distanceBox = findViewById(R.id.distanceBox);

        mapFragment.getMapAsync(this);
    }

    private String getSnippets(double latitude, double longitude) {
        String snippet = "No snippet";
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            snippet = address + ", " + city;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return snippet;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centreMap(currentLocation);
            }
        } else {
            Toast.makeText(this, "no permmisions", Toast.LENGTH_SHORT);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.setMyLocationEnabled(true);

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(false);
        mMap.getUiSettings().setAllGesturesEnabled(false);

        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                centreMap(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

        };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, DEFAULT_DISTANCE, locationListener);
            Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            centreMap(currentLocation);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, PERMISSION_REQUEST_CODE);
        }


    }

    private void centreMap(Location location) {
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();

        mMap.addMarker(new MarkerOptions().position(position).title(yourPositionMessage).icon(BitmapDescriptorFactory.fromResource(R.drawable.user_position)).snippet(getSnippets(location.getLatitude(), location.getLongitude())));
        if (currentPosition == null) {
            currentPosition = position;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,DEFAULT_ZOOM));
        }else{
            currentPosition = position;
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mMap.getCameraPosition()));
        }

        for (Marker marker : markers.getMarkers()) {
            addMarker(marker.getLatLng(), marker.getTitle(), marker.getSnippet());
        }


    }

    private void addMarker(LatLng position, String title, String snippet) {

        addMarker(new MarkerOptions().position(position).title(title).snippet(snippet));

    }

    private void addMarker(LatLng position, String title) {

        addMarker(new MarkerOptions().position(position).title(title));

    }

    private void addMarker(MarkerOptions options) {
        mMap.addMarker(options);
        if (!markers.contains(options.getPosition())) {
            markers.addMarker(new Marker(options));
        }
        double distance = calculateMinimum(options.getPosition());

        if(distance < minDistance){
            minDistance=distance;
            if(minDistance<=MIN_DISTANCE){
                distanceBox.setText("Ud esta en :" + options.getTitle());
            }else {
                distanceBox.setText("El punto más cercano es :" + options.getTitle());
            }

        }


    }

    private double calculateMinimum(LatLng position) {

        double latitude = position.latitude;
        double longitude = position.longitude;

        Location loc1 = new Location("");
        loc1.setLatitude(latitude);
        loc1.setLongitude(longitude);

        Location currentLoc = new Location("");
        currentLoc.setLatitude(currentPosition.latitude);
        currentLoc.setLongitude(currentPosition.longitude);

        double distance = currentLoc.distanceTo(loc1);
        return distance;

    }

    @Override
    public void onMapClick(LatLng position) {
        if (markerMode) {
            selectedPosition = position;
            markerDialog.show();
        }


    }

    @Override
    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {

        marker.hideInfoWindow();
        if(marker.getTitle().equals(yourPositionMessage)){
            Toast.makeText(this, marker.getTitle()+"\n"+marker.getSnippet(),Toast.LENGTH_LONG).show();
        }else{
            double distance = calculateMinimum(marker.getPosition());
            Toast.makeText(this, "La distancia a "+marker.getTitle()+" es: "+formatter.format(distance), Toast.LENGTH_LONG).show();
        }

        return true;
    }

}
