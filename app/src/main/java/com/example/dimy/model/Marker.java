package com.example.dimy.model;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Marker {

    private LatLng latLng;
    private String title;
    private String snippet;

    public Marker(MarkerOptions options) {
        this.latLng=options.getPosition();
        this.title=options.getTitle();
        this.snippet=options.getTitle();
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }
}
