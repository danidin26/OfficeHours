package com.dannysh.officehours.stats;

import com.dannysh.officehours.model.Event;

import java.util.ArrayList;

/**
 * Created by Danny on 08-Apr-18.
 */

public interface StatsView {
    void showEvents(ArrayList<Event> events);
}
