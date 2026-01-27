package com.example.smartalarmclock;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.annotation.Nullable;

public class AlarmPlaybackService extends Service {

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SettingsManager settingsManager = new SettingsManager(this);

        String uriString = intent.getStringExtra("soundUri");

        Uri soundUri = uriString != null
                ? Uri.parse(uriString)
                : RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        mediaPlayer = MediaPlayer.create(this, soundUri);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // Vibrate if enabled
        if (settingsManager.isVibrationEnabled()) {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {0, 1000, 1000};
            vibrator.vibrate(pattern, 0);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}