package com.dannysh.officehours;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

public class AddressActivity extends AppCompatActivity {

    private ProgressBar spinner;
    Button button;
    GeocodeStatusReciever geocodeStatusReciever;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        //reciever for geocod process result
        geocodeStatusReciever = new GeocodeStatusReciever();
        IntentFilter resultIntentFilter = new IntentFilter(Constants.BROADCAST_GEOCODE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(geocodeStatusReciever,resultIntentFilter);

        //button init for starting geocode intentservice
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String userAdressInput = ((EditText)findViewById(R.id.adressTextBox)).getText().toString();
               Intent addressIntent = new Intent(getApplicationContext(),GeoCodingService.class);
               addressIntent.putExtra(Constants.USER_ADDRESS_INPUT,userAdressInput);
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
        if(SharedPrefManager.getString(this, Constants.ADDRESS_NAME,Constants.DEF_ADDRESS_STRING).equals(Constants.DEF_ADDRESS_STRING)){
            button.setText("Submit new Address");
        }
        else{
            button.setText("Update Current Address");
        }
        button.setVisibility(View.VISIBLE);
        button.setClickable(true);
    }




    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(geocodeStatusReciever);
    }

    public class GeocodeStatusReciever extends BroadcastReceiver{
        public GeocodeStatusReciever(){}

        @Override
        public void onReceive(Context context, Intent intent) {
            button.setClickable(true);
            spinner.setVisibility(View.GONE);
            Toast.makeText(context , intent.getStringExtra(Constants.GEO_RESULT_MESSAGE)!=null ? intent.getStringExtra(Constants.GEO_RESULT_MESSAGE)
                    :" Internal Error, please try again later",Toast.LENGTH_LONG).show();
        }

    }
}
