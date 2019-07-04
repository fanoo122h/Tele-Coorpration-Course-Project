package com.example.laware;

/**
 * Created by Arslanyasinwattoo on 8/29/2017.
 */

        import android.Manifest;
        import android.content.Context;
        import android.content.SharedPreferences;
        import android.content.pm.PackageManager;
        import android.location.Location;
        import android.os.Bundle;
        import android.support.v4.app.ActivityCompat;
        import android.util.Log;
        import android.widget.ArrayAdapter;
        import android.widget.Toast;

        import com.example.laware.com.example.laware.beans.Venue_Bean;
        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.GooglePlayServicesUtil;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.common.api.PendingResult;
        import com.google.android.gms.common.api.Status;
        import com.google.android.gms.location.LocationListener;
        import com.google.android.gms.location.LocationRequest;
        import com.google.android.gms.location.LocationServices;
        import com.loopj.android.http.AsyncHttpClient;
        import com.loopj.android.http.JsonHttpResponseHandler;
        import com.loopj.android.http.RequestParams;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.Date;
        import java.util.List;

        import cz.msebera.android.httpclient.Header;

        import static android.content.Context.MODE_PRIVATE;


/**
 * EXAMPLE: Location tracker
 * https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderApi
 * http://javapapers.com/android/android-location-fused-provider
 */
public class LocationTracker implements
        SensorTracker,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final static String TAG = LocationTracker.class.getSimpleName();
    private static final long INTERVAL = 1000 * 45;
    private static final long FASTEST_INTERVAL = 1000 * 30;
    private Context context;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;

    public LocationTracker(Context context) {
        this.context = context;

        init();
    }

    private void init() {
        if (!isGooglePlayServicesAvailable(context)) return;

        this.mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void start() {
        if (mGoogleApiClient != null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(INTERVAL);
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

            mGoogleApiClient.connect();
        }
    }

    public void stop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
            mGoogleApiClient.disconnect();
            Log.d(TAG, "Location::stop");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location tracking::Permission is missing");
            Log.i(TAG, "Pls grant location permission: Settings > Apps > <This App> > Location");
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location::start");
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.d(TAG, "Location("+location.getLatitude()+","+location.getLongitude()+")");
        AsyncHttpClient client = new AsyncHttpClient();
        //  client.get("https://laware.herokuapp.com/api/venues/search/", new JsonHttpResponseHandler() {

        SharedPreferences settings = context.getSharedPreferences("laware", MODE_PRIVATE);
        String userId=settings.getString("id","");
        String lastname=settings.getString("lastName","");
        String firstname=settings.getString("firstName","");
        Log.d("Cookie", settings.getString("Set-Cookie", ""));
        client.addHeader("Cookie", settings.getString("Set-Cookie", ""));
        RequestParams params = new RequestParams();
        params.put("userId",userId);
        params.put("firstname",firstname);
        params.put("lastname", lastname);
        params.put("long",location.getLongitude());
        params.put("lat",location.getLatitude() );
        Date date= new Date();
        params.put("date", date);
        params.put("time",date.getTime() );
        client.post("https://laware.herokuapp.com/api/location/",params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                // Pull out the first event on the public timeline
                        Log.d("in andriod on success ","");


        }
        });


//        Toast.makeText(this,"location").show();


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
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
