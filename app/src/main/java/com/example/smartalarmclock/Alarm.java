package com.example.smartalarmclock;

import java.io.Serializable;
import java.util.Set;

// Implement Serializable to allow passing this object between activities
public class Alarm implements Serializable {
    public long id;
    public int hour;
    public int minute;
    public long triggerTime;
    public Set<String> repeatDays;
    public boolean vibration;
    public boolean snooze;
    public boolean enabled;
    public String name;
    private String soundUri;
    private String soundTitle;

    public Alarm(long id, int hour, int minute, long triggerTime, Set<String> repeatDays, String ringtoneUri, boolean vibration, boolean snooze, boolean enabled, String name) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.triggerTime = triggerTime;
        this.repeatDays = repeatDays;
        this.vibration = vibration;
        this.snooze = snooze;
        this.enabled = enabled;
        this.name = name;
    }

    public String getSoundUri() {
        return soundUri;
    }

    public void setSoundUri(String soundUri) {
        this.soundUri = soundUri;
    }

    public String getSoundTitle() {
        return soundTitle;
    }

    public void setSoundTitle(String soundTitle) {
        this.soundTitle = soundTitle;
    }
}
