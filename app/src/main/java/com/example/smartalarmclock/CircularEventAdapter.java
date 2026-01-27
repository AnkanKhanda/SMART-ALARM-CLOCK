package com.example.smartalarmclock;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CalendarEventAdapter extends RecyclerView.Adapter<CalendarEventAdapter.EventViewHolder> {

    private List<Alarm> alarms;
    private Context context;
    private OnAlarmEditListener listener;

    public interface OnAlarmEditListener {
        void onAlarmEdit(Alarm alarm);
    }

    public CalendarEventAdapter(Context context, List<Alarm> alarms, OnAlarmEditListener listener) {
        this.context = context;
        this.alarms = alarms;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.calendar_event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Alarm alarm = alarms.get(position);

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        holder.eventTime.setText(timeFormat.format(alarm.triggerTime));

        if (alarm.name != null && !alarm.name.isEmpty()) {
            holder.eventName.setText(alarm.name);
            holder.eventName.setVisibility(View.VISIBLE);
        } else {
            holder.eventName.setVisibility(View.GONE);
        }

        holder.editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAlarmEdit(alarm); // Send the whole alarm object
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTime, eventName;
        ImageButton editButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTime = itemView.findViewById(R.id.event_time);
            eventName = itemView.findViewById(R.id.event_name);
            editButton = itemView.findViewById(R.id.edit_event_button);
        }
    }
}
