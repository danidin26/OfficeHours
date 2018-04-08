package com.dannysh.officehours.address;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dannysh.officehours.R;
import com.dannysh.officehours.utils.Constants;
import com.dannysh.officehours.utils.SharedPrefManager;

public class AddressActivity extends AppCompatActivity implements AddressView {
    //ui
    private ProgressBar spinner;
    private Button button;
    private AddressPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        presenter = new AddressPresenter(this, this);

        //reciever for geocod process result
        //button init for starting geocode intentservice
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userAdressInput = ((EditText) findViewById(R.id.adressTextBox)).getText().toString();
                presenter.start(userAdressInput);

                button.setClickable(false);
                spinner.setVisibility(View.VISIBLE);
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
        presenter.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.pause();
    }

    @Override
    public void showStatus(String status) {
        button.setClickable(true);
        spinner.setVisibility(View.GONE);
        Toast.makeText(this, status, Toast.LENGTH_LONG).show();
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
