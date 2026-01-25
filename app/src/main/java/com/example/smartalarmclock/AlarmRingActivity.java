package com.example.smartalarmclock;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmRingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ring);

        // --- Show activity over lock screen and wake up device ---
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        TextView alarmTime = findViewById(R.id.ring_alarm_time);
        TextView alarmName = findViewById(R.id.ring_alarm_name);
        Button snoozeButton = findViewById(R.id.snooze_button);
        Button stopButton = findViewById(R.id.stop_button);

        // Set time and name (we'll pass these via intent later)
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.getDefault());
        alarmTime.setText(sdf.format(Calendar.getInstance().getTime()));
        alarmName.setText(getIntent().getStringExtra("ALARM_NAME"));

        stopButton.setOnClickListener(v -> {
            // Stop the alarm sound/vibration service (will create later)
            Intent stopIntent = new Intent(this, AlarmPlaybackService.class);
            stopService(stopIntent);
            finish();
        });

        snoozeButton.setOnClickListener(v -> {
            // Snooze logic will be added here
            Intent stopIntent = new Intent(this, AlarmPlaybackService.class);
            stopService(stopIntent);
            finish();
        });
    }
}