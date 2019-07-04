package com.example.laware;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;

/**
 * EXAMPLE: Physical activity tracking
 * https://developers.google.com/android/reference/com/google/android/gms/location/DetectedActivity
 * https://developers.google.com/android/reference/com/google/android/gms/location/ActivityRecognitionApi
 * https://code.tutsplus.com/tutorials/how-to-recognize-user-activity-with-activity-recognition--cms-25851
 */
public class MovementTracker implements
        SensorTracker,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MovementTracker.class.getSimpleName();

    private static final long INTERVAL = 1000 * 5;

    private Context context;
    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mActivityRecongPendingIntent;

    public MovementTracker(Context context) {

        this.context = context;
        init();
    }

    private void init() {
        if (!isGooglePlayServicesAvailable(context)) return;

        this.mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void start() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public void stop(){
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient,mActivityRecongPendingIntent);
            mGoogleApiClient.disconnect();
            Log.d(TAG, "Activity::stop");
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        Intent i = new Intent(context, MovementIntentService.class);
        mActivityRecongPendingIntent = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, INTERVAL, mActivityRecongPendingIntent);

        Log.d(TAG, "Activity::start");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Not connected to ActivityRecognition");
    }

    public static boolean isGooglePlayServicesAvailable(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            return false;
        }
    }
}
