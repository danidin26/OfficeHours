package com.dannysh.officehours.stats;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.dannysh.officehours.R;
import com.dannysh.officehours.model.Event;
import com.dannysh.officehours.address.AddressActivity;

import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends AppCompatActivity implements StatsView {

    EventsAdapter adapter;
    Button newAddressButton;

    private StatsPresenter presenter;
    private List<Event> lEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        newAddressButton = findViewById(R.id.bNewAddress);
        newAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StatsActivity.this, AddressActivity.class));
            }
        });

        // List
        adapter = new EventsAdapter(this, lEvents);
        ListView listView = findViewById(R.id.eventsList);
        listView.setAdapter(adapter);

        // Presentation
        presenter = new StatsPresenter(getApplicationContext(), this);
        presenter.loadEvents();
    }

    @Override
    public void showEvents(ArrayList<Event> events) {
        adapter.addAll(events);
        adapter.notifyDataSetChanged();
    }
}
