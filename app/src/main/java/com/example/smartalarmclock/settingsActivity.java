package com.example.smartalarmclock;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_RINGTONE = 1;
    private SettingsManager settingsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        settingsManager = new SettingsManager(this);

        setupThemeToggle();
        setupVibrationToggle();
        setupAlarmSoundPicker();
        setupAlarmVolumeControl(); // Added this call
        setupAppInfo();
    }

    private void setupThemeToggle() {
        SwitchMaterial themeSwitch = findViewById(R.id.theme_switch);
        themeSwitch.setChecked(settingsManager.getTheme() == AppCompatDelegate.MODE_NIGHT_YES);
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int mode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            AppCompatDelegate.setDefaultNightMode(mode);
            settingsManager.saveTheme(mode);
        });
    }

    private void setupVibrationToggle() {
        SwitchMaterial vibrationSwitch = findViewById(R.id.vibration_switch);
        vibrationSwitch.setChecked(settingsManager.isVibrationEnabled());
        vibrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsManager.setVibrationEnabled(isChecked);
        });
    }

    private void setupAlarmSoundPicker() {
        TextView alarmSound = findViewById(R.id.alarm_sound);
        alarmSound.setOnClickListener(v -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
            startActivityForResult(intent, REQUEST_CODE_RINGTONE);
        });
    }

    private void setupAlarmVolumeControl() {
        TextView alarmVolume = findViewById(R.id.alarm_volume);
        alarmVolume.setOnClickListener(v -> {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            // This shows the system UI for the alarm volume stream
            audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
        });
    }

    private void setupAppInfo() {
        TextView appInfo = findViewById(R.id.app_info);
        appInfo.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_RINGTONE) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                settingsManager.setAlarmSoundUri(uri.toString());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}