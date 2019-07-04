package com.example.laware;

/**
 * Created by Arslanyasinwattoo on 8/16/2017.
 */

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.laware.com.example.laware.beans.Friend_Bean;
import com.example.laware.com.example.laware.beans.Location_bean;
import com.example.laware.com.example.laware.beans.Venue_Bean;
import com.example.laware.com.example.laware.user.UserProfile;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MapList extends AppCompatActivity {
        List<Venue_Bean> list= new ArrayList<>();
    List<Location_bean> list2= new ArrayList<>();
    //List<Venue_Bean> list3= new ArrayList<>();
    Venue_Bean b;
    Location_bean c;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.list_map);
            //Intent i = getIntent();
            Bundle bundle = getIntent().getExtras();
            final String search_txt = bundle.getString("search");
            final String type_txt= bundle.getString("type");
            final String liked_txt= bundle.getString("liked");
            final String friends_txt= bundle.getString("friends");

            String url="https://laware.herokuapp.com/api/venues/";
            BufferedReader in = null;
            String data = null;


              //  HttpClient httpclient = new DefaultHttpClient();

                //HttpGet request = new HttpGet();
                if(search_txt !=null&& search_txt.length()>0){
                    url+="name/"+search_txt;
                }else if(type_txt !=null && type_txt.length()>0) {
                    url += "type/" + type_txt;
                }else if(liked_txt !=null && liked_txt.length()>0) {
                  //  url += "type/" + liked_txt;
                    url="https://laware.herokuapp.com/api/rating/likedplaces/"+liked_txt;
                }else if(friends_txt !=null && friends_txt.length()>0){
                url = "https://laware.herokuapp.com/api/friend/location/"+friends_txt;
                }

            AsyncHttpClient client = new AsyncHttpClient();
            SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
            Log.d("Cookie",settings.getString("Set-Cookie",""));
            client.addHeader("Cookie",settings.getString("Set-Cookie",""));


            //client.get("https://laware.herokuapp.com/api/venues/name/" + search_txt, new JsonHttpResponseHandler() {

            client.get(url, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                            // Pull out the first event on the public timeline

                            JSONArray venue_data = timeline;
                            //String tweetText = firstEvent.getString("text");

                            for (int i = 0; i < venue_data.length(); i++) {
                                try {
                                    JSONObject venueObject = venue_data.getJSONObject(i);

                                    if(friends_txt !=null && friends_txt.length()>0){
                                       Location_bean bean = new Location_bean();
                                        bean.setId(venueObject.getString("_id"));
                                        bean.setUserID(venueObject.getString("userId"));
                                           bean.setFirstname(venueObject.getString("firstname"));
                                          bean.setLastname(venueObject.getString("lastname"));
                                        bean.setDate(venueObject.getString("date"));
                                        bean.setTime(venueObject.getString("time"));
                                        bean.setLat(venueObject.getString("lat"));
                                        bean.setLng(venueObject.getString("long"));

                                      //  bean.setPending(venueObject.getInt("pending"));
                                        list2.add(bean);
                                    }else{
                                        // Venue_Bean bean = new Venue_Bean(venueObject.getString("_id"),venueObject.getString("name"),venueObject.getString("address"),venueObject.getString("long"),venueObject.getString("lat"),venueObject.getString("category"));
                                        Venue_Bean bean = new Venue_Bean();
                                        bean.setId(venueObject.getString("_id"));
                                        bean.setName(venueObject.getString("name"));
                                        bean.setAddress(venueObject.getString("address"));
                                        bean.setLat(venueObject.getString("lat"));
                                        bean.setLng(venueObject.getString("long"));
                                        String categoryList = venueObject.getString("category");
                                        Log.d("categoryList", categoryList);
                                        // /bean.setCategory();

                                        list.add(bean);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
            //getting locations based on userIds
        //    client.get(url, new JsonHttpResponseHandler() {

          //      @Override
            //    public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                    // Pull out the first event on the public timeline
              //  }
            //});
                    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
        public void run() {

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map_view1);
           // googleMap=(SupportMapFragment)mapFragment;

            mapFragment.getMapAsync(new OnMapReadyCallback() {

                @Override
                public void onMapReady(GoogleMap googleMap) {
                    if (ContextCompat.checkSelfPermission(MapList.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        googleMap.setMyLocationEnabled(true);
                    } else {
                        Toast.makeText(com.example.laware.MapList.this, "You have to accept to enjoy all app's services!", Toast.LENGTH_LONG).show();
                        if (ContextCompat.checkSelfPermission(MapList.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            googleMap.setMyLocationEnabled(true);
                        }
                    }
                    if(friends_txt !=null && friends_txt.length()>0) {
                        for (Location_bean bean : list2) {
                            c = bean;
                            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                @Override
                                public void onInfoWindowClick(Marker marker) {
                                    Intent intent = new Intent(MapList.this, UserProfile.class);

                                    intent.putExtra("userId", c.getUserID());

                                    startActivity(intent);
                                }
                            });

                            LatLng location = new LatLng(Double.parseDouble(bean.getLat()), Double.parseDouble(bean.getLng()));
                            Log.d("in mapping ->", bean.getFirstname());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
                            googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(bean.getLat()), Double.parseDouble(bean.getLng()))).title(
                                    bean.getFirstname()+","+bean.getLastname()).snippet(bean.getFirstname()+","+bean.getLastname()));

                        }

                    }else{

                        for (Venue_Bean bean : list) {
                            b = bean;
                            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                @Override
                                public void onInfoWindowClick(Marker marker) {
                                    Intent intent = new Intent(MapList.this, Venue_Profile.class);

                                    intent.putExtra("venueId", b.getId());

                                    startActivity(intent);
                                }
                            });

                            LatLng location = new LatLng(Double.parseDouble(bean.getLat()), Double.parseDouble(bean.getLng()));
                            Log.d("in mapping ->", bean.getAddress());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
                            googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(bean.getLat()), Double.parseDouble(bean.getLng()))).title(
                                    bean.getName()).snippet(bean.getAddress()));

                        }
                    }

                }

            });
        }
    }, 3000);


        }

        @Override
        protected void onStop() {
            super.onStop();
            finish();

        }

    }



