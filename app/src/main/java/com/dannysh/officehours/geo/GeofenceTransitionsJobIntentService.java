/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dannysh.officehours.geo;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.JobIntentService;
import android.text.format.DateFormat;
import android.util.Log;

import com.dannysh.officehours.model.Event;
import com.dannysh.officehours.model.EventDataSource;
import com.dannysh.officehours.utils.Constants;
import com.dannysh.officehours.utils.SharedPrefManager;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;


public class GeofenceTransitionsJobIntentService extends JobIntentService {

    private static final int JOB_ID = 101;

    private static final String TAG = "GeofenceTransitionsIS";

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, GeofenceTransitionsJobIntentService.class, JOB_ID, intent);
    }


    @Override
    protected void onHandleWork(Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            //reset enterence TS in SP
            SharedPrefManager.setLong(getApplicationContext(), Constants.ENTRANCE_EVENT_TS, 0L);
            return;
        } else {
            //differ between enter and exist
            int geofenceTransition = geofencingEvent.getGeofenceTransition();
            long ts = System.currentTimeMillis();
            // Test that the reported transition was of interest.
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

                SharedPrefManager.setLong(getApplicationContext(), Constants.ENTRANCE_EVENT_TS, ts);
                return;
            }
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                String date = getDateStringFromTs(ts);
                double totalTime = getTsDifferences(getApplicationContext(), ts);
                addEvent(new Event(date, totalTime));
            }

        }
        Log.d(TAG, "GeofenceTransitionsJobIntentService has been triggered with event : " + geofencingEvent.getGeofenceTransition());
    }

    private double getTsDifferences(Context applicationContext, long exitTs) {
        long entranceTs = SharedPrefManager.getLong(applicationContext, Constants.ENTRANCE_EVENT_TS, 0L);
        LocalDateTime entrance = LocalDateTime.ofInstant(Instant.ofEpochMilli(entranceTs), TimeZone.getDefault().toZoneId());
        LocalDateTime exit = LocalDateTime.ofInstant(Instant.ofEpochMilli(exitTs), TimeZone.getDefault().toZoneId());
        long result = Duration.between(entrance, exit).toHours();
        return result;
    }

    private String getDateStringFromTs(long ts) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(ts);
        return DateFormat.format("dd/MM/yyyy", cal).toString();
    }

    private void addEvent(Event event) {
        EventDataSource eventDataSource = new EventDataSource(getApplicationContext());
        eventDataSource.addEvent(event);
    }
}
