package com.example.smartalarmclock;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AddEditAlarmActivity extends AppCompatActivity implements CircularTimePickerView.OnTimeChangedListener, DatePickerDialog.OnDateSetListener {

    private CircularTimePickerView circularTimePicker;
    private TextView hourDisplay, minuteDisplay, dateDisplay;
    private RadioButton amButton, pmButton;
    private EditText alarmNameEditText;
    private Button saveButton, deleteButton;

    private AlarmStorage alarmStorage;
    private Calendar selectedCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
    private Alarm existingAlarm = null;

    private TextView tvSound;
    private Uri selectedSoundUri;
    private String selectedSoundTitle;
    private MediaPlayer previewPlayer;

    private final ActivityResultLauncher<Intent> soundPickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri uri = result.getData()
                                    .getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                            if (uri != null) {
                                selectedSoundUri = uri;

                                Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
                                selectedSoundTitle = ringtone.getTitle(this);

                                tvSound.setText("Sound: " + selectedSoundTitle);
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_alarm);

        alarmStorage = new AlarmStorage(this);
        initializeViews();

        if (getIntent().hasExtra("alarm_data")) {
            existingAlarm = (Alarm) getIntent().getSerializableExtra("alarm_data");
        }

        if (existingAlarm != null) {
            selectedCalendar.setTimeInMillis(existingAlarm.triggerTime);
            alarmNameEditText.setText(existingAlarm.name);
            deleteButton.setVisibility(View.VISIBLE);
            if (existingAlarm.getSoundUri() != null) {
                selectedSoundUri = Uri.parse(existingAlarm.getSoundUri());
                selectedSoundTitle = existingAlarm.getSoundTitle();
                tvSound.setText("Sound: " + selectedSoundTitle);
            }
        } else {
            deleteButton.setVisibility(View.GONE);
            selectedSoundTitle = "Default";
        }

        setupListeners();
        updateUIFromCalendar();
    }

    private void initializeViews() {
        circularTimePicker = findViewById(R.id.circular_time_picker);
        hourDisplay = findViewById(R.id.hour_display);
        minuteDisplay = findViewById(R.id.minute_display);
        dateDisplay = findViewById(R.id.date_display);
        amButton = findViewById(R.id.am_button_dark);
        pmButton = findViewById(R.id.pm_button_dark);
        alarmNameEditText = findViewById(R.id.alarm_name_edit_text);
        saveButton = findViewById(R.id.save_button_dark);
        deleteButton = findViewById(R.id.delete_button);
        tvSound = findViewById(R.id.sound_selector);
        findViewById(R.id.choose_date_button).setOnClickListener(v -> showDatePickerDialog());
    }

    private void setupListeners() {
        circularTimePicker.setOnTimeChangedListener(this);
        hourDisplay.setOnClickListener(v -> circularTimePicker.setMode(CircularTimePickerView.Mode.HOUR));
        minuteDisplay.setOnClickListener(v -> circularTimePicker.setMode(CircularTimePickerView.Mode.MINUTE));
        saveButton.setOnClickListener(v -> saveAlarm());
        deleteButton.setOnClickListener(v -> deleteAlarm());

        tvSound.setOnClickListener(v -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
                    RingtoneManager.TYPE_ALARM);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                    selectedSoundUri);

            soundPickerLauncher.launch(intent);
        });

        tvSound.setOnLongClickListener(v -> {
            playPreview();
            return true;
        });
    }

    private void playPreview() {
        stopPreview();

        Uri uri = selectedSoundUri != null
                ? selectedSoundUri
                : RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        previewPlayer = MediaPlayer.create(this, uri);
        if (previewPlayer != null) {
            previewPlayer.start();
            previewPlayer.setOnCompletionListener(mp -> stopPreview());
        }
    }

    private void stopPreview() {
        if (previewPlayer != null) {
            previewPlayer.stop();
            previewPlayer.release();
            previewPlayer = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPreview();
    }

    private void updateUIFromCalendar() {
        int hour24 = selectedCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = selectedCalendar.get(Calendar.MINUTE);
        int amPm = selectedCalendar.get(Calendar.AM_PM);

        int hour12 = hour24 % 12;
        if (hour12 == 0) hour12 = 12;

        circularTimePicker.setSelectedHour(hour12);
        circularTimePicker.setSelectedMinute(minute);

        hourDisplay.setText(String.format(Locale.getDefault(), "%02d", hour12));
        minuteDisplay.setText(String.format(Locale.getDefault(), "%02d", minute));

        if (amPm == Calendar.AM) {
            amButton.setChecked(true);
        } else {
            pmButton.setChecked(true);
        }
        updateDateDisplay();
    }

    @Override
    public void onTimeChanged(int hour, int minute) {
        selectedCalendar.set(Calendar.HOUR, hour == 12 ? 0 : hour);
        selectedCalendar.set(Calendar.MINUTE, minute);
        updateDigitalDisplay();
    }

    private void updateDigitalDisplay() {
        int hour12 = selectedCalendar.get(Calendar.HOUR) == 0 ? 12 : selectedCalendar.get(Calendar.HOUR);
        hourDisplay.setText(String.format(Locale.getDefault(), "%02d", hour12));
        minuteDisplay.setText(String.format(Locale.getDefault(), "%02d", selectedCalendar.get(Calendar.MINUTE)));
    }

    private void showDatePickerDialog() {
        new DatePickerDialog(this, this,
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        selectedCalendar.set(Calendar.YEAR, year);
        selectedCalendar.set(Calendar.MONTH, month);
        selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateDateDisplay();
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
        dateDisplay.setText(sdf.format(selectedCalendar.getTime()));
    }

    private void saveAlarm() {
        List<Alarm> alarms = alarmStorage.getAlarms();
        String alarmName = alarmNameEditText.getText().toString();
        long triggerTime = selectedCalendar.getTimeInMillis();

        if (existingAlarm != null) {
            for (int i = 0; i < alarms.size(); i++) {
                if (alarms.get(i).id == existingAlarm.id) {
                    existingAlarm.name = alarmName;
                    existingAlarm.triggerTime = triggerTime;
                    existingAlarm.hour = selectedCalendar.get(Calendar.HOUR_OF_DAY);
                    existingAlarm.minute = selectedCalendar.get(Calendar.MINUTE);
                    existingAlarm.setSoundUri(selectedSoundUri != null ? selectedSoundUri.toString() : null);
                    existingAlarm.setSoundTitle(selectedSoundTitle);
                    alarms.set(i, existingAlarm);
                    break;
                }
            }
        } else {
            int hour = selectedCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = selectedCalendar.get(Calendar.MINUTE);
            existingAlarm = new Alarm(System.currentTimeMillis(), hour, minute, triggerTime, new HashSet<>(), "", true, false, true, alarmName);
            existingAlarm.setSoundUri(selectedSoundUri != null ? selectedSoundUri.toString() : null);
            existingAlarm.setSoundTitle(selectedSoundTitle);
            alarms.add(existingAlarm);
        }

        alarmStorage.saveAlarms(alarms);
        scheduleAlarm(existingAlarm);
        Toast.makeText(this, "Alarm Saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void deleteAlarm() {
        if (existingAlarm == null) return;

        List<Alarm> alarms = alarmStorage.getAlarms();
        for (Iterator<Alarm> iterator = alarms.iterator(); iterator.hasNext(); ) {
            Alarm a = iterator.next();
            if (a.id == existingAlarm.id) {
                iterator.remove();
                cancelAlarm(a);
                break;
            }
        }
        alarmStorage.saveAlarms(alarms);
        Toast.makeText(this, "Alarm Deleted", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void scheduleAlarm(Alarm alarm) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("ALARM_ID", alarm.id);
        intent.putExtra("ALARM_NAME", alarm.name);
        intent.putExtra("soundUri", alarm.getSoundUri());


        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) alarm.id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.triggerTime, pendingIntent);
            } else {
                // Inform the user that exact alarm permission is required.
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.triggerTime, pendingIntent);
        }
    }

    private void cancelAlarm(Alarm alarm) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) alarm.id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }
}
