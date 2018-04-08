package com.dannysh.officehours.address;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.dannysh.officehours.geo.GeoCodingIntentService;
import com.dannysh.officehours.geo.GeofenceBroadcastReceiver;
import com.dannysh.officehours.utils.Constants;
import com.dannysh.officehours.utils.SharedPrefManager;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danny on 08-Apr-18.
 */

public class AddressPresenter implements OnCompleteListener<Void> {
    private Context context;
    private AddressView view;

    //geofence
    private GeocodeResultReceiver geocodeStatusReciever = new GeocodeResultReceiver();
    private GeofencingClient geofencingClient = null;

    //geocode
    private double longitude, latitude;
    private String formalAddress;

    public AddressPresenter(Context context, AddressView view) {
        this.context = context;
        this.view = view;
    }

    public void pause() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(geocodeStatusReciever);
    }

    public void resume() {
        IntentFilter resultIntentFilter = new IntentFilter(Constants.BROADCAST_GEOCODE_ACTION);
        LocalBroadcastManager.getInstance(context).registerReceiver(geocodeStatusReciever, resultIntentFilter);
    }

    public void start(String userAdressInput) {
        Intent addressIntent = new Intent(context, GeoCodingIntentService.class);
        addressIntent.putExtra(Constants.USER_ADDRESS_INPUT, userAdressInput);

        //TODO limit service running time \ waiting for result
        context.startService(addressIntent);
    }

    @SuppressLint("MissingPermission")
    private void startGeofenceProcess(double longitude, double latitude) {
        //working with 1 geofence means we should try to remove current geofence and add the new one (solving the problem of checking when recieving events)
        //adding the new geofence depends on the succession of the removal
        geofencingClient = LocationServices.getGeofencingClient(context);
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
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if (task.isSuccessful()) {
            //commit the new address and lon lat to SP
            SharedPrefManager.setDouble(context, Constants.LOC_LONG, longitude);
            SharedPrefManager.setDouble(context, Constants.LOC_LAT, latitude);
            SharedPrefManager.setString(context, Constants.ADDRESS_NAME, formalAddress);

            view.showStatus("New Address Submitted");
        } else {
            view.showStatus(task.getException().getMessage());
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
                String status = intent.getStringExtra(Constants.GEO_RESULT_MESSAGE) != null ? intent.getStringExtra(Constants.GEO_RESULT_MESSAGE)
                        : " Internal Error, please try again later";
                view.showStatus(status);
            }
        }
    }
}
