package com.dannysh.officehours;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dannysh.officehours.Utils.Constants;
import com.dannysh.officehours.Utils.SharedPrefManager;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danny on 05-Apr-18.
 */

public class GeofenceInitService extends IntentService {

    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;

    public GeofenceInitService(){
        super("GeoenceTransitionService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // identify the request : add or remove
        //add
        geofencingClient = LocationServices.getGeofencingClient(this.getApplicationContext());
        double lat = intent.getDoubleExtra(Constants.LOC_LAT,Double.MAX_VALUE);
        double lon = intent.getDoubleExtra(Constants.LOC_LONG,Double.MAX_VALUE);
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(createGeofence(lat,lon));
        builder.build();
    }

    private List<Geofence> createGeofence(double lat, double lon) {
        List<Geofence> result = new ArrayList<>();
        Geofence temp = new Geofence.Builder()
                .setRequestId(Constants.GEOFENCE_BASE_ID+(lat+lon))
                .setCircularRegion(lat,lon,Constants.GEOFENCE_RADIUS_METERS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
        result.add(temp);
        return result;
    }
    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }
}
