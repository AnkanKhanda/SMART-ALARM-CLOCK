package com.example.smartalarmclock;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlarmStorage {

    private static final String PREFS_NAME = "AlarmClock";
    private static final String ALARMS_KEY = "Alarms";

    private SharedPreferences preferences;
    private Gson gson;

    public AlarmStorage(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public List<Alarm> getAlarms() {
        String json = preferences.getString(ALARMS_KEY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<Alarm>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveAlarms(List<Alarm> alarms) {
        SharedPreferences.Editor editor = preferences.edit();
        String json = gson.toJson(alarms);
        editor.putString(ALARMS_KEY, json);
        editor.apply();
    }
}