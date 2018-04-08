package com.dannysh.officehours.Model;

import android.database.Cursor;

public class Event {
    protected int id;
    protected String date;

    protected double totalTime;

    public Event(String date, double totalTime) {
        this.date = date;
        this.totalTime = totalTime;
    }

    public Event() {
    }

    public double getTotalTime() { return totalTime; }

    public void setTotalTime(double totalTime) { this.totalTime = totalTime; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    static public Event fromCursor(Cursor cursor) {
        if (cursor != null && cursor.getCount() != 0) {
            Event e = new Event();
            e.setId(cursor.getInt(cursor.getColumnIndex(SqliteHelper.COLUMN_ID)));
            e.setDate(cursor.getString(cursor
                    .getColumnIndex(SqliteHelper.COLUMN_EVENT_DATE)));
            e.setTotalTime(cursor.getDouble(cursor
                    .getColumnIndex(SqliteHelper.COLUMN_EVENT_TOTAL_TIME)));
            return e;
        }
        return null;
    }
}