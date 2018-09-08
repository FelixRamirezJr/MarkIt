package com.example.felix.locationmapper;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.*;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button save;
    private Button calenderButton;
    private MarkerLocation currLocation = null;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    ArrayList<MarkerLocation> markerLocations = new ArrayList<>();
    ArrayList<Marker> markers = new ArrayList<>();
    Marker currMarker = null;
    int i = 0;
    String timeNow;
    String dateSelected;
    final int PERMISSION_ALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        save = (Button) findViewById(R.id.search);

        //calenderButton = (Button) findViewById(R.id.calenderButton);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        setSearchAction();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        ((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setTextColor(Color.BLACK);
        ((View)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_fragment)).setBackgroundColor(Color.WHITE);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d("OnSelect","True");
                currLocation = new MarkerLocation();
                currLocation.setData(place.getName().toString(),place.getAddress().toString(),
                        place.getLatLng().latitude,place.getLatLng().longitude,dateSelected);
                LatLng newplace = new LatLng(place.getLatLng().latitude,place.getLatLng().longitude);
                //currMarker = mMap.addMarker(new MarkerOptions().position(newplace).title(place.getName().toString()));
                currMarker = mMap.addMarker(new MarkerOptions().position(newplace).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title(place.getName().toString()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(newplace));
            }

            @Override
            public void onError(Status status) {
                Log.d("ERRROR",status.getStatus().toString());

            }
        });

        load_saved_places();
        save_place();
        set_calender_button();
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
        marker_listeners();


        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ALL);

        } else {
            mMap.setMyLocationEnabled(true);
        }

    }

    void set_calender_button(){
//        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d");
//        timeNow = format.format(calendar.getTime());
//        dateSelected = timeNow;
//        calenderButton.setText(timeNow);
    }

    void marker_listeners(){
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final MarkerInfo popup = new MarkerInfo();
                MarkerLocation markerInfoToOpen = markerLocations.get(Integer.parseInt(marker.getTag().toString()));
                popup.markerLocation = markerInfoToOpen;
                popup.marker = marker;
                popup.show(getFragmentManager(), "fragment_edit_name");
                return false;
            }
        });
    }

    void setSearchAction()
    {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currLocation == null){
                    Toast.makeText(getBaseContext(),"Choose a place first...",Toast.LENGTH_LONG).show();
                }
                else{
                    markerLocations.add(currLocation);
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference db = database.push();
                    db.setValue(currLocation);
                    Toast.makeText(getBaseContext(),"Place saved!",Toast.LENGTH_LONG).show();
                    currLocation = null;
                }
            }
        });
    }

    void save_place(){
        mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                //MarkerLocation marker = new MarkerLocation();
                //markerLocations.add(marker);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            public void onCancelled(FirebaseDatabase firebaseError) {
            }
        });
    }


    void load_saved_places(){

        mRootRef.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                if(!dataSnapshot.getValue().toString().contains("null")) {
                    MarkerLocation toAdd  = dataSnapshot.getValue(MarkerLocation.class);
                    if(toAdd.address != null && toAdd.name != null) {
                        Log.d("Marker", toAdd.name + " " + toAdd.lat + toAdd.address);
                        markerLocations.add(toAdd);
                        LatLng newplace = new LatLng(toAdd.lat, toAdd.lon);
                        if(currMarker != null) {
                            currMarker.remove();
                        }
                        //Marker m = mMap.addMarker(new MarkerOptions().position(newplace).title(toAdd.name).alpha(BitmapDescriptorFactory.HUE_ORANGE));
                        Marker m = mMap.addMarker(new MarkerOptions().position(newplace).title(toAdd.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        m.setTag(i);
                        markers.add(m);
                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(newplace));
                        i++;
                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_ALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    mMap.setMyLocationEnabled(true);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



}
