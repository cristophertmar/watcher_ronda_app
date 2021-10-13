package com.example.watcher.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GpsTrackerAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "GpsTrackerAlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            context.startService(new Intent(context, LocationService.class));
        } catch (Exception e) {
            e.getLocalizedMessage();
        }
    }
}