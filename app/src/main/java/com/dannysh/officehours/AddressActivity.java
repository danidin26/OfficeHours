package com.dannysh.officehours;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dannysh.officehours.Utils.Constants;
import com.dannysh.officehours.Utils.SharedPrefManager;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends AppCompatActivity implements OnCompleteListener<Void> {
    //geocode
    double longitude, latitude;
    private String formalAddress;
    //ui
    private ProgressBar spinner;
    Button button;
    //geofence
    GeocodeResultReceiver geocodeStatusReciever;
    GeofencingClient geofencingClient;
    PendingIntent geofencePendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        //reciever for geocod process result
        geocodeStatusReciever = new GeocodeResultReceiver();
        //button init for starting geocode intentservice
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userAdressInput = ((EditText) findViewById(R.id.adressTextBox)).getText().toString();
                Intent addressIntent = new Intent(getApplicationContext(), GeoCodingIntentService.class);
                addressIntent.putExtra(Constants.USER_ADDRESS_INPUT, userAdressInput);
                button.setClickable(false);
                //TODO limit service running time \ waiting for result
                spinner.setVisibility(View.VISIBLE);
                startService(addressIntent);

            }
        });

        spinner = findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (SharedPrefManager.getString(this, Constants.ADDRESS_NAME, Constants.DEF_ADDRESS_STRING).equals(Constants.DEF_ADDRESS_STRING)) {
            button.setText("Submit new Address");
        } else {
            button.setText("Update Current Address");
        }
        if (!checkPermissions()) {
            requestPermissions();
        }
        button.setVisibility(View.VISIBLE);
        button.setClickable(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter resultIntentFilter = new IntentFilter(Constants.BROADCAST_GEOCODE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(geocodeStatusReciever, resultIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(geocodeStatusReciever);
    }

    @SuppressLint("MissingPermission")
    private void startGeofenceProcess(double longitude, double latitude) {
        //working with 1 geofence means we should try to remove current geofence and add the new one (solving the problem of checking when recieving events)
        //adding the new geofence depends on the succession of the removal
        geofencingClient = LocationServices.getGeofencingClient(this.getApplicationContext());
        geofencingClient.removeGeofences(getGeofencePendingIntent());

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(createGeofence(latitude, longitude));
        geofencingClient.addGeofences(builder.build(), getGeofencePendingIntent()).addOnCompleteListener(this);


    }

    private List<Geofence> createGeofence(double lat, double lon) {
        List<Geofence> result = new ArrayList<>();
        Geofence temp = new Geofence.Builder()
                .setRequestId(Constants.GEOFENCE_BASE_ID + (lat + lon))
                .setCircularRegion(lat, lon, Constants.GEOFENCE_RADIUS_METERS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
        result.add(temp);
        return result;
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if (task.isSuccessful()) {
            //commit the new address and lon lat to SP
            SharedPrefManager.setDouble(this, Constants.LOC_LONG, longitude);
            SharedPrefManager.setDouble(this, Constants.LOC_LAT, latitude);
            SharedPrefManager.setString(this, Constants.ADDRESS_NAME, formalAddress);

            button.setClickable(true);
            spinner.setVisibility(View.GONE);
            Toast.makeText(this, "New Address Submitted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT);
        }
    }

    private class GeocodeResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra(Constants.GEOCODE_SUCCESS, true);
            if (success) {
                longitude = intent.getDoubleExtra(Constants.LOC_LONG, Double.MAX_VALUE);
                latitude = intent.getDoubleExtra(Constants.LOC_LAT, Double.MAX_VALUE);
                formalAddress = intent.getStringExtra(Constants.ADDRESS_NAME);
                startGeofenceProcess(longitude, latitude);
            } else {
                button.setClickable(true);
                spinner.setVisibility(View.GONE);
                Toast.makeText(context, intent.getStringExtra(Constants.GEO_RESULT_MESSAGE) != null ? intent.getStringExtra(Constants.GEO_RESULT_MESSAGE)
                        : " Internal Error, please try again later", Toast.LENGTH_LONG).show();
            }
        }
    }

    //Permission
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
            ActivityCompat.requestPermissions(AddressActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    34);
        }
}
