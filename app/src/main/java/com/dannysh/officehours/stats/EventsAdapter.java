package com.dannysh.officehours.stats;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dannysh.officehours.model.Event;
import com.dannysh.officehours.R;

import java.util.List;

/**
 * Created by Danny on 08-Apr-18.
 */

public class EventsAdapter extends ArrayAdapter<Event> {

    private static class ViewHolder {
        TextView date;
        TextView hours;
    }

    public EventsAdapter(@NonNull Context context, List<Event> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Event event = (Event) getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_office_hours, parent, false);
            viewHolder.date = convertView.findViewById(R.id.tvDate);
            viewHolder.hours = convertView.findViewById(R.id.tvHours);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.hours.setText(Double.toString(event.getTotalTime()));
        viewHolder.date.setText(event.getDate());
        return convertView;
    }
}
