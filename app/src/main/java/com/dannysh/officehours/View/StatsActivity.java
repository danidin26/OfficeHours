package com.dannysh.officehours.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.dannysh.officehours.Model.Event;
import com.dannysh.officehours.Model.EventDataSource;
import com.dannysh.officehours.R;

import java.util.List;

public class StatsActivity  extends AppCompatActivity {

    List<Event> lEvents;
    EventsAdapter adapter;
    Button newAddressButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        EventDataSource eds = new EventDataSource(this.getApplicationContext());
        lEvents = eds.getEvents();
        newAddressButton = findViewById(R.id.bNewAddress);
        newAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StatsActivity.this,AddressActivity.class));

            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter = new EventsAdapter(this,lEvents);
        ListView listView = findViewById(R.id.eventsList);
        listView.setAdapter(adapter);
    }
}
