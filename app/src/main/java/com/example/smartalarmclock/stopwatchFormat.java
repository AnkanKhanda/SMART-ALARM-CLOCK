package com.example.smartalarmclock;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class StopwatchFragment extends Fragment {

    private TextView stopwatchDisplay;
    private Button startButton, stopButton, resetButton;
    private Handler handler = new Handler();
    private long startTime, timeInMilliseconds, timeSwapBuff, updateTime = 0L;
    private boolean isRunning = false;

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updateTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updateTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updateTime % 1000);
            stopwatchDisplay.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", mins, secs, milliseconds / 10));
            handler.postDelayed(this, 0);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stopwatch, container, false);

        stopwatchDisplay = view.findViewById(R.id.stopwatch_display);
        startButton = view.findViewById(R.id.start_button);
        stopButton = view.findViewById(R.id.stop_button);
        resetButton = view.findViewById(R.id.reset_button);

        startButton.setOnClickListener(v -> {
            if (!isRunning) {
                startTime = SystemClock.uptimeMillis();
                handler.postDelayed(updateTimerThread, 0);
                isRunning = true;
            }
        });

        stopButton.setOnClickListener(v -> {
            if (isRunning) {
                timeSwapBuff += timeInMilliseconds;
                handler.removeCallbacks(updateTimerThread);
                isRunning = false;
            }
        });

        resetButton.setOnClickListener(v -> {
            startTime = 0L;
            timeInMilliseconds = 0L;
            timeSwapBuff = 0L;
            updateTime = 0L;
            stopwatchDisplay.setText("00:00:00");
            if (isRunning) {
                handler.removeCallbacks(updateTimerThread);
                isRunning = false;
            }
        });

        return view;
    }
}
