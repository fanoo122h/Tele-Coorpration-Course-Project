
package com.example.laware;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.laware.com.example.laware.beans.Venue_Bean;
import com.example.laware.com.example.laware.user.UserProfile;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class Home extends AppCompatActivity {
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String id = "id";
    public static final String firstName = "firstname";
    public static final String lastName = "lastname";
    public static final String email = "email";
    public static final String password = "password";
    public static final String url = "url";
   // private Switch swLocation;
   // private Switch swActivities;
    //private Switch swAccelerometer;

    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search__page2);
        startService();


        Button search_bt = (Button) findViewById(R.id.button9);
        search_bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Home.this, Venue.class);
                EditText search_text= (EditText) findViewById(R.id.Search_bar);
                String txt= search_text.getText().toString();
                i.putExtra("search",txt);
                startActivity(i);
            }
        });

        final AutoCompleteTextView search = (AutoCompleteTextView)
                findViewById(R.id.Search_bar);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setText("");
            }
        });

        AsyncHttpClient client = new AsyncHttpClient();
        //  client.get("https://laware.herokuapp.com/api/venues/search/", new JsonHttpResponseHandler() {

        SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
        Log.d("Cookie", settings.getString("Set-Cookie", ""));
        client.addHeader("Cookie", settings.getString("Set-Cookie", ""));
        client.get("https://laware.herokuapp.com/api/venues/search/", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                List<String> list= new ArrayList<>();
                JSONArray venue_data = timeline;
                Log.d("venue",venue_data.toString());
                for (int i = 0; i < venue_data.length(); i++) {
                    try {
                        String venueObject = venue_data.getString(i);
                        // Venue_Bean bean = new Venue_Bean(venueObject.getString("_id"),venueObject.getString("name"),venueObject.getString("address"),venueObject.getString("long"),venueObject.getString("lat"),venueObject.getString("category"));
                        Venue_Bean bean = new Venue_Bean();
                        bean.setName(venueObject);
                        list.add(venueObject);
                        Log.d("in andriod on success ",venueObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (Home.this, android.R.layout.select_dialog_item,list);

                search.setThreshold(2);
                search.setAdapter(adapter);

            }
        });


        Button breakfast = (Button) findViewById(R.id.breakfast_button);
        breakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Home.this, Venue.class);
                i.putExtra("type", "food");
                startActivity(i);
            }
        });
        Button lunch = (Button) findViewById(R.id.lunch_button);
        lunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Home.this, Venue.class);
                i.putExtra("type", "restaurant");
                startActivity(i);
            }
        });

        Button dinner = (Button) findViewById(R.id.dinner_button);
        dinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Home.this, Venue.class);
                i.putExtra("type", "point_of_interest");
                startActivity(i);
            }
        });
        Button coffee = (Button) findViewById(R.id.button10);
        coffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Home.this, Venue.class);
                i.putExtra("type", "cafe");
                startActivity(i);
            }
        });
        Button night = (Button) findViewById(R.id.button11);
        night.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Home.this, Venue.class);
                i.putExtra("type", "club");
                startActivity(i);
            }
        });
        Button things = (Button) findViewById(R.id.button8);
        things.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Home.this, Venue.class);
                i.putExtra("type", "establishment");
                startActivity(i);

            }
        });
        Button search_button1 = (Button) findViewById(R.id.search_button2);
        search_button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Home.this, Home.class);
                startActivity(i);
            }
        });
        Button profilebutton = (Button) findViewById(R.id.profilebutton);
        profilebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Home.this, UserProfile.class);
                SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
                i.putExtra("userId", settings.getString("id", ""));
                startActivity(i);
            }
        });
        Button friendbutton = (Button) findViewById(R.id.friends);
        friendbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Home.this, Friends_list.class);
                i.putExtra("", "");
                startActivity(i);
            }
        });

    }

        public void startService(){
          boolean trackingLocation = true;
           // boolean trackingActivities = swActivities.isChecked();
           // boolean trackingAccelerometer = swAccelerometer.isChecked();
            Log.d("in - service","");
            Intent mIntent = new Intent(this, TrackingService.class);
            Bundle extras = new Bundle();
            extras.putBoolean(TrackingService.PARAM_LOCATION,trackingLocation);
           // extras.putBoolean(TrackingService.PARAM_ACTIVITY,trackingActivities);
           // extras.putBoolean(TrackingService.PARAM_ACCELERATION,trackingAccelerometer);
            mIntent.putExtras(extras);

            startService(mIntent); // start service with passing data

            Log.i("Service","started");
            Toast.makeText(this,"service started", Toast.LENGTH_SHORT).show();

            updateUI();
        }
    /**
     * Stop tracking service
     * @param view
     */
    public void stopService(View view){
        Intent mIntent = new Intent(this, TrackingService.class);
        stopService(mIntent); // stop service

       // Log.i("Service",getString(R.string.service_stopped));
        Toast.makeText(this,"stopped",Toast.LENGTH_SHORT).show();

        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI(){
        boolean isRunning = false;
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TrackingService.class.getName().equals(service.service.getClassName())) {
                isRunning = true;
            }
        }




    }
    @Override
    protected void onStop() {
        super.onStop();


    }

}
