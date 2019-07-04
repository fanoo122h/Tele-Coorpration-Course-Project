package com.example.laware;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arslanyasinwattoo on 8/29/2017.
 */

public class TrackingService extends Service{


    private final static int SERVICE_NOTIFICATION_ID = 123456;

    public final static String PARAM_LOCATION       = "trackingLocation";
    public final static String PARAM_ACTIVITY       = "trackingActivities";
    public final static String PARAM_ACCELERATION   = "trackingAccelerometer";

    private Map<String,SensorTracker> trackers;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("tracking","in service");
        trackers = new HashMap<>();
        trackers.put(LocationTracker.class.getSimpleName(),new LocationTracker(this));
        trackers.put(MovementTracker.class.getSimpleName(),new MovementTracker(this));
        trackers.put(AccelerationTracker.class.getSimpleName(),new AccelerationTracker(this));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();

        boolean trackingLocation = extras.getBoolean(PARAM_LOCATION);
        boolean trackingActivities = extras.getBoolean(PARAM_ACTIVITY);
        boolean trackingAccelerometer = extras.getBoolean(PARAM_ACCELERATION);

        if(trackingLocation) trackers.get(LocationTracker.class.getSimpleName()).start();
        if(trackingActivities) trackers.get(MovementTracker.class.getSimpleName()).start();
        if(trackingAccelerometer) trackers.get(AccelerationTracker.class.getSimpleName()).start();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                        .setContentTitle(("notification"));

        Intent resultIntent = new Intent(this, Home.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Home.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        startForeground(SERVICE_NOTIFICATION_ID, mBuilder.build());

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // unregister listeners
        for (SensorTracker tracker : trackers.values()){
            tracker.stop();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {return null;}


}
