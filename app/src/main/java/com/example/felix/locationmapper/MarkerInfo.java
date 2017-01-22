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
    Button sendMessage;
    EditText subject,message;
    MultiAutoCompleteTextView recepients;
    MarkerLocation markerLocation;
    TextView name;
    TextView address;
    TextView date;
    Button delete;
    int position;
    GoogleMap map;
    Marker marker;
    DialogFragment d;



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

        name.setText(markerLocation.name);
        address.setText(markerLocation.address);
        date.setText("Coming soon");
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click!","delete marker");
                final DatabaseReference dbf = FirebaseDatabase.getInstance().getReference();
                final String[] keyToDelete = new String[1];
                dbf.orderByKey().addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d(String.valueOf(dataSnapshot.getValue(MarkerLocation.class).id),String.valueOf(markerLocation.id));
                        if (dataSnapshot.getValue(MarkerLocation.class).id.equalsIgnoreCase(markerLocation.id)) {
                            keyToDelete[0] = dataSnapshot.getKey().toString();
                            Log.d("Key: ", keyToDelete[0]);
                            Log.d(markerLocation.name, "Is about to be deleted");
                            DatabaseReference delete = FirebaseDatabase.getInstance().getReference().child(keyToDelete[0]);
                            delete.removeValue();
                            // Removed...
                            marker.remove();
                            dbf.removeEventListener(this);
                            d.dismiss();
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
                // End of on action
            }
        });


        return rootView;
    }
}
