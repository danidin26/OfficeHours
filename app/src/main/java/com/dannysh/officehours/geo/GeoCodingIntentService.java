package com.dannysh.officehours.geo;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.dannysh.officehours.utils.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeoCodingIntentService extends IntentService {

    public GeoCodingIntentService() {
        super("GeoCodingIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String resultMessage = Constants.DEF_GEO_RESULT_MESSAGE;
        String addressName = intent.getStringExtra(Constants.USER_ADDRESS_INPUT);
        if (!addressName.isEmpty()) {
            try {
                Geocoder gc = new Geocoder(this, Locale.getDefault());
                if (gc.isPresent()) {
                    List<Address> lAddresses = gc.getFromLocationName(addressName, 1);
                    if (lAddresses == null || lAddresses.size() == 0) {
                        resultMessage = Constants.GEO_RESULT_INVALID_ADDRESS;
                        LocalBroadcastManager.getInstance(this.getApplicationContext()).sendBroadcast(new Intent(Constants.BROADCAST_GEOCODE_ACTION)
                                .putExtra(Constants.GEO_RESULT_MESSAGE, resultMessage)
                                .putExtra(Constants.GEO_RESULT_SUCCESS,false));
                    } else {
                        Address address = lAddresses.get(0);
                        resultMessage = Constants.GEO_RESULT_SUCCESS;
                        // notify activity of result
                        Intent resltintent = new Intent(Constants.BROADCAST_GEOCODE_ACTION)
                                .putExtra(Constants.GEO_RESULT_SUCCESS,true)
                                .putExtra(Constants.LOC_LAT, address.getLatitude())
                                .putExtra(Constants.LOC_LONG, address.getLongitude())
                                .putExtra(Constants.ADDRESS_NAME, address.getAddressLine(0));
                        LocalBroadcastManager.getInstance(this.getApplicationContext()).sendBroadcast(resltintent);
                    }
                }
            } catch (IOException ex) {
                resultMessage = Constants.GEO_IO_ERROR;
                LocalBroadcastManager.getInstance(this.getApplicationContext()).sendBroadcast(new Intent(Constants.BROADCAST_GEOCODE_ACTION)
                        .putExtra(Constants.GEO_RESULT_MESSAGE, resultMessage)
                        .putExtra(Constants.GEO_RESULT_SUCCESS,false));
            } catch (Exception ex) {
                LocalBroadcastManager.getInstance(this.getApplicationContext()).sendBroadcast(new Intent(Constants.BROADCAST_GEOCODE_ACTION)
                        .putExtra(Constants.GEO_RESULT_MESSAGE, resultMessage)
                        .putExtra(Constants.GEO_RESULT_SUCCESS,false));
            }
        }
    }
}
