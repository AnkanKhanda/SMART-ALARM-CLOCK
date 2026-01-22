package com.example.smartalarmclock;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsManager {

    private static final String PREFS_NAME = "AppSettings";
    private static final String KEY_THEME = "theme";
    private static final String KEY_VIBRATION = "vibration_enabled";
    private static final String KEY_ALARM_SOUND = "alarm_sound_uri";

    private SharedPreferences sharedPreferences;

    public SettingsManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveTheme(int themeMode) {
        sharedPreferences.edit().putInt(KEY_THEME, themeMode).apply();
    }

    public int getTheme() {
        return sharedPreferences.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public void setVibrationEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_VIBRATION, enabled).apply();
    }

    public boolean isVibrationEnabled() {
        return sharedPreferences.getBoolean(KEY_VIBRATION, true); // Default to true
    }

    public void setAlarmSoundUri(String uriString) {
        sharedPreferences.edit().putString(KEY_ALARM_SOUND, uriString).apply();
    }

    public String getAlarmSoundUri() {
        String defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
        return sharedPreferences.getString(KEY_ALARM_SOUND, defaultUri);
    }
}
