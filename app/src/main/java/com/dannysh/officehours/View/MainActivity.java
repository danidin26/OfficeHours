package com.dannysh.officehours.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.dannysh.officehours.Utils.Constants;
import com.dannysh.officehours.Utils.SharedPrefManager;

/**
 * Created by Danny on 01-Apr-18.
 */

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent activityIntent = new Intent();
        //check if there is location details in shared prefrences
        if(SharedPrefManager.getString(this, Constants.ADDRESS_NAME,Constants.DEF_ADDRESS_STRING).equals(Constants.DEF_ADDRESS_STRING)){
            //no address in app
            activityIntent.setClass(this,AddressActivity.class);
        }
        else{
            activityIntent.setClass(this,StatsActivity.class);
        }
        startActivity(activityIntent);
        finish();
    }
}
