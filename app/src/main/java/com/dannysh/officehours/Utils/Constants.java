package com.dannysh.officehours.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

/**
 * Created by Danny on 01-Apr-18.
 */

public class Constants {
    public static final String LOC_LONG = "locationLongitude" ;
    public static final String LOC_LAT = "locationLatitude" ;
    public static final String ADDRESS_NAME = "addressName";
    public static final String DEF_ADDRESS_STRING = "";
    public static final String SHARED_PREF_NAME  = "settings";
    public static final String USER_ADDRESS_INPUT = "usersAddressInput";
    public static final String GEO_RESULT_MESSAGE = "geocodeResultMessage";
    public static final String BROADCAST_GEOCODE_ACTION = "geoCodeAction";
    public static final String GEOFENCE_BASE_ID = "dannysh.officehours";
    public static final float GEOFENCE_RADIUS_METERS = 300;
    public static final String DEF_GEO_RESULT_MESSAGE = "Internal Error, please try again later";
    public static final String GEO_IO_ERROR = "Network Error, please try again later" ;
    public static final String GEO_RESULT_INVALID_ADDRESS = "Invalid Address, please try again";
    public static final String GEO_RESULT_SUCCESS = "Address Updated";
    public static final String GEOCODE_SUCCESS = "geocodeSuccess";
}
