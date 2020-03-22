package com.example.dimy.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Markers {

    private ArrayList<Marker> markers;

    public Markers(ArrayList<Marker> markers) {
        this.markers = markers;
    }

    public Markers() {
         markers= new ArrayList<>();
    }

    public void addMarker(Marker marker){
        this.markers.add(marker);
    }

    public ArrayList<Marker> getMarkers() {
        return markers;
    }

    public boolean contains(LatLng position) {
        for (int i = 0; i < markers.size(); i++) {
            if(markers.get(i).getLatLng()==position){
                return true;
            }
        }

        return false;
    }

    public void clear() {
        this.markers.clear();
    }
}
