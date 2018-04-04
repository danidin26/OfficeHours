package com.dannysh.officehours;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.dannysh.officehours.Utils.Constants;
import com.dannysh.officehours.Utils.SharedPrefManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeoCodingService extends IntentService {

    public GeoCodingService(){
        super("GeoCodingService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String resultMessage = Constants.DEF_GEO_RESULT_MESSAGE;
        String addressName = intent.getStringExtra(Constants.USER_ADDRESS_INPUT);
        if(!addressName.isEmpty()){
            try {
                Geocoder gc = new Geocoder(this, Locale.getDefault());
                if (gc.isPresent()) {
                   List<Address> lAddresses = gc.getFromLocationName(addressName,1);
                   if(lAddresses == null || lAddresses.size() == 0){
                       resultMessage = Constants.GEO_RESULT_INVALID_ADDRESS;
                   }
                   else {
                       Address address = lAddresses.get(0);
                       SharedPrefManager.setDouble(this,Constants.LOC_LAT , address.getLatitude());
                       SharedPrefManager.setDouble(this,Constants.LOC_LONG , address.getLongitude());
                       resultMessage = Constants.GEO_RESULT_SUCCESS;
                       //address is valid - update naming in sp
                       //TODO consider saving the formated address
                       SharedPrefManager.setString(this,Constants.ADDRESS_NAME,address.getAddressLine(0));
                   }
                }
            }
            catch(IOException ex){
                resultMessage = Constants.GEO_IO_ERROR;
            }
            catch(Exception ex){

            }
            finally {
                //send broadcast with the message that would be displayed in Toast in AddressActivity.
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.BROADCAST_GEOCODE_ACTION).putExtra(Constants.GEO_RESULT_MESSAGE,resultMessage));
            }
        }
    }
}
