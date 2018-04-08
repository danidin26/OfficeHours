package com.dannysh.officehours.stats;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.dannysh.officehours.model.Event;
import com.dannysh.officehours.model.EventDataSource;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Danny on 08-Apr-18.
 */

public class StatsPresenter {

    private ExecutorService ioExecutor = Executors.newSingleThreadExecutor();

    private Context context;
    private StatsView view;

    public StatsPresenter(Context context, StatsView view) {
        this.context = context;
        this.view = view;
    }

    public void loadEvents() {
        ioExecutor.submit(new Runnable() {
            @Override
            public void run() {
                EventDataSource eds = new EventDataSource(context);
                final ArrayList<Event> events = eds.getEvents();

                // Send to UI
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        view.showEvents(events);
                    }
                });
            }
        });
    }
}
