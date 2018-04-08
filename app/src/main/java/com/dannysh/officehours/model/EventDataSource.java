package com.dannysh.officehours.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class EventDataSource {

	private String[] allColumns = { SqliteHelper.COLUMN_ID,
			SqliteHelper.COLUMN_EVENT_DATE,
			SqliteHelper.COLUMN_EVENT_TOTAL_TIME};

	private Context context;
	protected SQLiteDatabase database;
	protected SqliteHelper dbHelper;

	public EventDataSource(Context context) {
		initSqliteHelper(context);
		open();
	}

	protected void initSqliteHelper(Context context) {
		dbHelper = new SqliteHelper(context);
	}

	protected void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	private void close() {
		database.close();
		dbHelper.close();
	}


	public void addEvent(Event event) {
		ContentValues values = new ContentValues();
		values.put(SqliteHelper.COLUMN_EVENT_DATE, event.getDate());
		values.put(SqliteHelper.COLUMN_EVENT_TOTAL_TIME, event.getTotalTime());
		database.insert(SqliteHelper.TABLE_EVENTS, null, values);
		close();
	}

	/**
	 * retreive data for activity
	 *
	 * @return
	 */
	public ArrayList<Event> getEvents() {
		ArrayList<Event> list = new ArrayList<Event>();
		//latest events
		String orderBy = SqliteHelper.COLUMN_ID + " DESC";
		Cursor cursor = database.query(SqliteHelper.TABLE_EVENTS, allColumns,
				null, null, null, null, orderBy);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			list.add(Event.fromCursor(cursor));
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		close();
		return list;
	}
}
