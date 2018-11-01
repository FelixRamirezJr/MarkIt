package com.example.felix.locationmapper;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by felix on 12/23/16.
 */

public class MarkerInfo extends DialogFragment {
    MarkerLocation markerLocation;
    TextView name;
    TextView address;
    TextView date;
    Button delete;
    Marker marker;
    DialogFragment d;

    MarkerInfo(MarkerLocation ml, Marker m){
     this.markerLocation = ml;
     this.marker = m;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View rootView = inflater.inflate(R.layout.markerinfo, container, false);
        d  = this;
        // Set up for larger Dialog


        // Getting rid of the divider
        int divierId = getDialog().getContext().getResources()
                .getIdentifier("android:id/titleDivider", null, null);
        View divider = getDialog().findViewById(divierId);
        if (divider != null) {
            divider.setVisibility(View.GONE);
        }

        // Setting Spinner Color

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        Log.d("Width", Integer.toString(width));
        Log.d("Height", Integer.toString(height));
        getDialog().getWindow().setLayout(width - 100, ViewGroup.LayoutParams.WRAP_CONTENT);

        name = (TextView) rootView.findViewById(R.id.name);
        address = (TextView) rootView.findViewById(R.id.address);
        date = (TextView) rootView.findViewById(R.id.date);
        delete = (Button) rootView.findViewById(R.id.delete);

        if(markerLocation != null) {
            name.setText(markerLocation.name);
            address.setText(markerLocation.address);
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MapsActivity)getActivity()).remove_marker_location(markerLocation);
                marker.remove();
                d.dismiss();
                // End of on action
            }
        });


        return rootView;
    }
}
