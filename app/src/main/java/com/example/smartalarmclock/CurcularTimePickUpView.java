package com.example.smartalarmclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class CircularTimePickerView extends View {

    public enum Mode { HOUR, MINUTE }

    private Paint circlePaint, textPaint, selectedPaint, handPaint;
    private int selectedHour = 12;
    private int selectedMinute = 0;
    private Mode currentMode = Mode.HOUR;
    private OnTimeChangedListener listener;

    public interface OnTimeChangedListener {
        void onTimeChanged(int hour, int minute);
    }

    public CircularTimePickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.parseColor("#2C2B30"));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);

        selectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectedPaint.setColor(Color.parseColor("#E91E63"));

        handPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        handPaint.setColor(Color.parseColor("#E91E63"));
        handPaint.setStrokeWidth(8);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(centerX, centerY) - 80;

        canvas.drawCircle(centerX, centerY, radius, circlePaint);

        if (currentMode == Mode.HOUR) {
            drawHours(canvas, centerX, centerY, radius);
        } else {
            drawMinutes(canvas, centerX, centerY, radius);
        }

        drawHand(canvas, centerX, centerY, radius);
        canvas.drawCircle(centerX, centerY, 15, selectedPaint);
    }

    private void drawHours(Canvas canvas, int centerX, int centerY, int radius) {
        for (int i = 1; i <= 12; i++) {
            double angle = Math.PI / 6 * (i - 3);
            int x = (int) (centerX + Math.cos(angle) * (radius - 60));
            int y = (int) (centerY + Math.sin(angle) * (radius - 60));

            if (i == selectedHour) {
                canvas.drawCircle(x, y + 10, 40, selectedPaint);
            }
            canvas.drawText(String.valueOf(i), x, y + 15, textPaint);
        }
    }

    private void drawMinutes(Canvas canvas, int centerX, int centerY, int radius) {
        for (int i = 0; i < 60; i += 5) {
            double angle = Math.PI / 30 * (i - 15);
            int x = (int) (centerX + Math.cos(angle) * (radius - 60));
            int y = (int) (centerY + Math.sin(angle) * (radius - 60));

            if (i == selectedMinute) {
                canvas.drawCircle(x, y + 10, 40, selectedPaint);
            }
            canvas.drawText(String.format("%02d", i), x, y + 15, textPaint);
        }
    }

    private void drawHand(Canvas canvas, int centerX, int centerY, int radius) {
        double angle;
        if (currentMode == Mode.HOUR) {
            angle = Math.PI / 6 * (selectedHour - 3);
        } else {
            angle = Math.PI / 30 * (selectedMinute - 15);
        }
        int handEndX = (int) (centerX + Math.cos(angle) * (radius - 120));
        int handEndY = (int) (centerY + Math.sin(angle) * (radius - 120));
        canvas.drawLine(centerX, centerY, handEndX, handEndY, handPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX() - getWidth() / 2;
            float y = event.getY() - getHeight() / 2;
            double angle = Math.toDegrees(Math.atan2(y, x)) + 90;
            if (angle < 0) angle += 360;

            if (currentMode == Mode.HOUR) {
                int hour = (int) Math.round(angle / 30);
                if (hour == 0) hour = 12;
                if (hour != selectedHour) {
                    selectedHour = hour;
                    notifyTimeChanged();
                    invalidate();
                }
            } else {
                int minute = (int) Math.round(angle / 6);
                if (minute == 60) minute = 0;
                if (minute != selectedMinute) {
                    selectedMinute = minute;
                    notifyTimeChanged();
                    invalidate();
                }
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void notifyTimeChanged() {
        if (listener != null) {
            listener.onTimeChanged(selectedHour, selectedMinute);
        }
    }

    public void setMode(Mode mode) {
        if (currentMode != mode) {
            currentMode = mode;
            invalidate();
        }
    }

    public Mode getMode() {
        return currentMode;
    }

    public void setOnTimeChangedListener(OnTimeChangedListener listener) {
        this.listener = listener;
    }

    public void setSelectedHour(int hour) {
        selectedHour = (hour == 0) ? 12 : hour;
        invalidate();
    }

    public void setSelectedMinute(int minute) {
        selectedMinute = minute;
        invalidate();
    }

    public int getSelectedHour() { return selectedHour; }
    public int getSelectedMinute() { return selectedMinute; }
}
