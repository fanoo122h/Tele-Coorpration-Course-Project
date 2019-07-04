package com.example.laware;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.android.payload.FileNotFoundException;
import com.cloudinary.utils.ObjectUtils;
import com.example.laware.com.example.laware.beans.Checkin_bean;
import com.example.laware.com.example.laware.beans.Comment_Bean;
import com.example.laware.com.example.laware.beans.Rating_bean;
import com.example.laware.com.example.laware.beans.User_Bean;
import com.example.laware.com.example.laware.beans.Venue_Bean;
import com.example.laware.com.example.laware.user.UserProfile;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

import static android.R.attr.data;

public class Venue_Profile extends AppCompatActivity {
    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    List<Comment_Bean> comment_list= new ArrayList<>();
    int checkins;
    int ratings;
    int comments;
    String pathurl="";

    List<Checkin_bean> vistor_list= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_venue__profile);
        Bundle bundle = getIntent().getExtras();
        final Venue_Bean bean = new Venue_Bean();
        final String venueId = bundle.getString("venueId");

        final AsyncHttpClient client = new AsyncHttpClient();
        SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
        Log.d("Cookie",settings.getString("Set-Cookie",""));
        client.addHeader("Cookie",settings.getString("Set-Cookie",""));
        final String firstname = settings.getString("firstName","");
        final String lastname = settings.getString("lastName","");
        //for gamification of user
        ratingGamigication();
        checkinGamigication();
        CommentGamification();
        final Button checkin = (Button) findViewById(R.id.button2);
        //if(checkin.getVisibility()!=View.INVISIBLE){
        //  }
        Button rating = (Button) findViewById(R.id.button3);
        rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog fbDialogue = new Dialog(Venue_Profile.this, android.R.style.Theme_Black_NoTitleBar);
                fbDialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
                fbDialogue.setContentView(R.layout.activity_rating);
                fbDialogue.setCancelable(true);
                fbDialogue.show();
                Button cancel = (Button) fbDialogue.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fbDialogue.cancel();
                    }
                });
                Button submit = (Button) fbDialogue.findViewById(R.id.submit);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //String ratingnurl = "http://10.0.2.2:3000/api/rating/";
                        String ratingnurl = "https://laware.herokuapp.com/api/rating/";
                      // String ratingnurl="https://laware.herokuapp.com/api/rating/";
                        RequestParams params = new RequestParams();
                        SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
                        String id = settings.getString("id", "");
                        String firstname = settings.getString("firstname", "");
                        String lastname = settings.getString("lastname", "");
                        RatingBar ratingBar = (RatingBar) fbDialogue.findViewById(R.id.ratingBar);
                        params.put("venueId", venueId);
                        params.put("userId", id);
                        params.put("venueName", bean.getName());
                        params.put("firstName", firstname);
                        params.put("lastName", lastname);
                        params.put("address", bean.getAddress());
                        params.put("long", bean.getLng());
                        params.put("lat", bean.getLat());
                        params.put("rating", ratingBar.getRating());
                        client.post(ratingnurl, params, new JsonHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                                fbDialogue.cancel();
                                final Dialog fbDialogue1 = new Dialog(Venue_Profile.this, android.R.style.Theme_Black_NoTitleBar);
                                fbDialogue1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
                                fbDialogue1.setContentView(R.layout.activity_popup);
                                fbDialogue1.setCancelable(true);
                                TextView message = (TextView) fbDialogue1.findViewById(R.id.msg);
                                message.setText("Success");
                                fbDialogue1.show();
                                final Timer timer2 = new Timer();
                                ratings=ratings+1;
                                Gamification();
                                timer2.schedule(new TimerTask() {
                                    public void run() {
                                        fbDialogue1.cancel();
                                        timer2.cancel(); //this will cancel the timer of the system
                                    }
                                }, 3000); // the timer will count 5 seconds....

                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                fbDialogue.cancel();
                            }
                        });
                    }
                });
            }
        });

        //  String url="http://10.0.2.2:3000/api/venues/"+venueId;
        String url = "https://laware.herokuapp.com/api/venues/" + venueId;
       //  String url="https://laware.herokuapp.com/api/venues/"+venueId;

        client.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                JSONObject venueObject = response;
                try {
                    bean.setId(venueObject.getString("_id"));
                    bean.setName(venueObject.getString("name"));
                    bean.setAddress(venueObject.getString("address"));
                    bean.setLat(venueObject.getString("lat"));
                    bean.setLng(venueObject.getString("long"));
                    String categoryList = venueObject.getString("category");
                    Log.d("categoryList", categoryList);
                    TextView name = (TextView) findViewById(R.id.textView11);
                    name.setText(bean.getName());
                    TextView address = (TextView) findViewById(R.id.textView12);
                    address.setText(bean.getAddress());
                    // final String venueName=bean.getName();

                    checkin.setVisibility(View.VISIBLE);
                    checkin.setClickable(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
       // String urlrating="https://laware.herokuapp.com/api/rating/"+venueId;
        String urlrating = "https://laware.herokuapp.com/api/rating/" + venueId;

        client.get(urlrating, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Rating_bean> ratingList = new ArrayList<>();
                JSONArray rating = response;
                for (int i = 0; i < rating.length(); i++) {
                    try {
                        JSONObject ratingObject = rating.getJSONObject(i);
                        Rating_bean rb = new Rating_bean();
                        rb.setId(ratingObject.getString("_id"));
                        rb.setVenueId(ratingObject.getString("venueId"));
                        rb.setUserId(ratingObject.getString("userId"));
                        rb.setVenueName(ratingObject.getString("venueName"));
                        rb.setLastName(ratingObject.getString("lastName"));
                        rb.setFirstName(ratingObject.getString("firstName"));
                        rb.setAddress(ratingObject.getString("address"));
                        rb.setLng(ratingObject.getString("long"));
                        rb.setLat(ratingObject.getString("lat"));
                        rb.setRating(ratingObject.getDouble("rating"));
                        ratingList.add(rb);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Double counter = 0.0;
                for (int i = 0; i < ratingList.size(); i++) {
                    counter = counter + ratingList.get(i).getRating();

                }
                if (ratingList.size() != 0) {
                    counter = counter / ratingList.size();
                } else {
                    counter = 0.0;
                }
                TextView rating_text = (TextView) findViewById(R.id.textView13);
                rating_text.setText(counter + "");
            }

        });
        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String checkinurl = "https://laware.herokuapp.com/api/checkin/";
                String checkinurl = "https://laware.herokuapp.com/api/checkin/";

                RequestParams params = new RequestParams();
                SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
                String id = settings.getString("id", "");

                params.put("venueId", venueId);
                params.put("userId", id);
                params.put("venueName", bean.getName());
                params.put("firstName", firstname);
                params.put("lastName", lastname);
                client.post(checkinurl, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        final Dialog fbDialogue2 = new Dialog(Venue_Profile.this, android.R.style.Theme_Black_NoTitleBar);
                        fbDialogue2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
                        fbDialogue2.setContentView(R.layout.activity_popup);
                        fbDialogue2.setCancelable(true);
                        TextView message = (TextView) fbDialogue2.findViewById(R.id.msg);
                        message.setText("Success");
                        fbDialogue2.show();
                        final Timer timer2 = new Timer();
                        checkins=checkins+1;
                        Gamification();
                        timer2.schedule(new TimerTask() {
                            public void run() {
                                fbDialogue2.cancel();
                                timer2.cancel(); //this will cancel the timer of the system
                            }
                        }, 3000); // the timer will count 5 seconds....


                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    }
                });
            }
        });


                String urlcheckin="https://laware.herokuapp.com/api/checkin/"+venueId;
      //  String urlcheckin = "http://192.168.0.103:3000/api/checkin/" + venueId;

        client.get(urlcheckin, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Checkin_bean> checkinList = new ArrayList<>();
                JSONArray checkin = response;
                for (int i = 0; i < checkin.length(); i++) {
                    try {
                        JSONObject checkinObject = checkin.getJSONObject(i);
                        Checkin_bean cb = new Checkin_bean();
                        cb.setId(checkinObject.getString("_id"));
                        cb.setVenueId(checkinObject.getString("venueId"));
                        cb.setUserId(checkinObject.getString("userId"));
                        cb.setVenueName(checkinObject.getString("venueName"));
                        cb.setLastName(checkinObject.getString("lastName"));
                        cb.setFirstName(checkinObject.getString("firstName"));
                        checkinList.add(cb);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                TextView checkin_text = (TextView) findViewById(R.id.textView14);
                if (checkinList.size() != 0) {
                    checkin_text.setText(checkinList.size() + "");
                } else {
                    checkin_text.setText("No Visits Yet");
                }
            }

        });
        Button mapbt = (Button) findViewById(R.id.button5);
        mapbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Venue_Profile.this, Map_Page.class);
                //String item = list.get(i).getId();
                Log.d("Long and Lat", bean.getLat() + ":" + bean.getLng());
                in.putExtra("Long", bean.getLng() + "");
                in.putExtra("Lat", bean.getLat() + "");
                in.putExtra("name", bean.getName() + "");
                in.putExtra("address", bean.getAddress() + "");
                startActivity(in);

            }
        });
        Button comment = (Button) findViewById(R.id.button4);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog fbDialogue = new Dialog(Venue_Profile.this, android.R.style.Theme_Black_NoTitleBar);
                fbDialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
                fbDialogue.setContentView(R.layout.activity_comment);
                fbDialogue.setCancelable(true);
                fbDialogue.show();
                Button cancel = (Button) fbDialogue.findViewById(R.id.cancel_comment);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fbDialogue.cancel();
                    }
                });

                Button img = (Button) fbDialogue.findViewById(R.id.button_img);
                img.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        //   fbDialogue.cancel();
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent,
                                "Select Picture"), SELECT_PICTURE);
                    }


                });


                Button submitt = (Button) fbDialogue.findViewById(R.id.submit_comment);
                submitt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       // String commenturl = "https://laware.herokuapp.com/api/comment/";
                        String commenturl = "https://laware.herokuapp.com/api/comment/";
                        RequestParams params = new RequestParams();
                        SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
                        String id = settings.getString("id", "");


                        EditText comment = (EditText) fbDialogue.findViewById(R.id.comment_tv);
                        String text = comment.getText().toString();
                        params.put("venueId", venueId);
                        params.put("userId", id);
                        Log.d("firstname",firstname);
                        Log.d("lastname",lastname);
                        params.put("firstname",firstname);
                        params.put("lastname",lastname);
                        params.put("comment", text);
                        if(selectedImagePath!=null){
                            Map config = ObjectUtils.asMap(
                                    "cloud_name", "dxlimxfwc",
                                    "api_key", "518816883576142",
                                    "api_secret", "vC0lWO6wTxXm3DnPFrzHrbOHLoM");
                            Cloudinary cloudinary = new Cloudinary(config);
                            try {
                                Map uploadResult=cloudinary.uploader().upload(selectedImagePath, ObjectUtils.emptyMap() );
                                String c_url= (String) uploadResult.get("url");
                              Log.d("Cloundinary",c_url);
                               if(c_url!=""){

                                   params.put("url",c_url);
                               }else{
                                   params.put("url", c_url);
                               }



                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }else{
                            params.put("url", "");
                        }

                        client.post(commenturl, params, new JsonHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                                fbDialogue.cancel();
                                final Dialog fbDialogue1 = new Dialog(Venue_Profile.this, android.R.style.Theme_Black_NoTitleBar);
                                fbDialogue1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
                                fbDialogue1.setContentView(R.layout.activity_popup);
                                fbDialogue1.setCancelable(true);
                                TextView message = (TextView) fbDialogue1.findViewById(R.id.msg);
                                message.setText("Success");
                                fbDialogue1.show();
                                final Timer timer2 = new Timer();
                                comments=comments+1;
                                Gamification();
                                timer2.schedule(new TimerTask() {
                                    public void run() {
                                        fbDialogue1.cancel();
                                        timer2.cancel(); //this will cancel the timer of the system
                                    }
                                }, 3000); // the timer will count 5 seconds....

                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                fbDialogue.cancel();
                            }
                        });


                    }
                });
            }


        });

        //client.get("https://laware.herokuapp.com/api/venues/name/" + search_txt, new JsonHttpResponseHandler() {
        String urlGetComments="https://laware.herokuapp.com/api/comment/"+venueId;
      //  String urlGetComments="https://laware.herokuapp.com/api/comment/venueId";
        client.get(urlGetComments, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline

                JSONArray comment_data = timeline;
                //String tweetText = firstEvent.getString("text");

                for (int i = 0; i < comment_data.length(); i++) {
                    try {
                        JSONObject commentObject = comment_data.getJSONObject(i);
                        // Venue_Bean bean = new Venue_Bean(venueObject.getString("_id"),venueObject.getString("name"),venueObject.getString("address"),venueObject.getString("long"),venueObject.getString("lat"),venueObject.getString("category"));
                        Comment_Bean bean = new Comment_Bean();
                        bean.setId(commentObject.getString("_id"));
                        bean.setVenueId(commentObject.getString("venueId"));
                        bean.setUserId(commentObject.getString("userId"));
                        bean.setFirstname(commentObject.getString("firstname"));
                        bean.setLastname(commentObject.getString("lastname"));
                        bean.setComment(commentObject.getString("comment"));
                        bean.setUrl(commentObject.getString("url"));
                       comment_list.add(bean);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //adding venue profile image
                String url="";
                ImageButton imageView = (ImageButton) findViewById(R.id.imageButton);
                for(Comment_Bean ss:comment_list){
                 if(ss.getUrl()!=null && ss.getUrl().length()>0){
                     url=ss.getUrl();
                 }
                }
                if(url!=null&&url.length()>0) {
                    Picasso.with(Venue_Profile.this).load(url)
                            .placeholder(imageView.getDrawable())
                            .fit()
                            .into(imageView);
                }
                    populateListView();
            }

        });


        //client.get("https://laware.herokuapp.com/api/venues/name/" + search_txt, new JsonHttpResponseHandler() {
        String urlGetTopVistors="https://laware.herokuapp.com/api/checkin/"+venueId;
       // String urlGetTopVistors="https://laware.herokuapp.com/api/checkin/"+venueId;
        client.get(urlGetTopVistors, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline

                JSONArray checkin_data = timeline;
                //String tweetText = firstEvent.getString("text");

                for (int i = 0; i < checkin_data.length(); i++) {
                    try {
                        JSONObject commentObject = checkin_data.getJSONObject(i);
                        // Venue_Bean bean = new Venue_Bean(venueObject.getString("_id"),venueObject.getString("name"),venueObject.getString("address"),venueObject.getString("long"),venueObject.getString("lat"),venueObject.getString("category"));
                        Checkin_bean bean = new Checkin_bean();
                        bean.setId(commentObject.getString("_id"));
                        bean.setVenueId(commentObject.getString("venueId"));
                        bean.setUserId(commentObject.getString("userId"));
                        bean.setFirstName(commentObject.getString("firstName"));
                        bean.setLastName(commentObject.getString("lastName"));

                        vistor_list.add(bean);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                populateListViewTopVistors();
            }

        });


    }

    private void populateListView() {
        ArrayAdapter<Comment_Bean> adapter = new Venue_Profile.MyListAdapter();
        ListView list1 = (ListView) findViewById(R.id.comments);
        list1.setAdapter(adapter);
        list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent in = new Intent(Venue_Profile.this, UserProfile.class);
                //String item = list.get(i).getId();
                in.putExtra("userId",comment_list.get(i).getUserId());
                startActivity(in);
            }
        });
       }
    private class MyListAdapter extends ArrayAdapter<Comment_Bean> {
        public MyListAdapter() {
            super(Venue_Profile.this, R.layout.list_view_comment, comment_list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.list_view_comment, parent, false);
            }
            Comment_Bean comment = comment_list.get(position);
            Log.d("in adding views", "veiwinf");
            // Fill the view
            ImageView imageView = (ImageView) itemView.findViewById(R.id.item_p_img);
            if(comment.getUrl()!=null&&comment.getUrl().length()>0) {
                Picasso.with(Venue_Profile.this).load(comment.getUrl())
                        .placeholder(imageView.getDrawable())
                        .fit()
                        .into(imageView);
            }

            TextView makeText = (TextView) itemView.findViewById(R.id.item_comment);
            makeText.setText(comment.getComment()+"");
            // Year:
            TextView yearText = (TextView) itemView.findViewById(R.id.item_lastname);
            yearText.setText(comment.getLastname());
            // Condition:
            TextView condionText = (TextView) itemView.findViewById(R.id.item_firstname);
            condionText.setText(comment.getFirstname());
            return itemView;
        }
    }

    private void populateListViewTopVistors() {
        ArrayAdapter<Comment_Bean> adapter = new Venue_Profile.MyListAdapterTopVistors();
        ListView list1 = (ListView) findViewById(R.id.top_visitors);
        list1.setAdapter(adapter);
        list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent in = new Intent(Venue_Profile.this, UserProfile.class);
                //String item = list.get(i).getId();
                in.putExtra("userId",vistor_list.get(i).getUserId());
                startActivity(in);
            }
        });
    }
    private class MyListAdapterTopVistors extends ArrayAdapter<Comment_Bean> {
        public MyListAdapterTopVistors() {
            super(Venue_Profile.this, R.layout.list_view_comment, comment_list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.list_view_vistors, parent, false);
            }
            Checkin_bean comment = vistor_list.get(position);
            Log.d("in adding views", "veiwinf");

            //call userId object for image url.
            String   urlUser="https://laware.herokuapp.com/api/users/"+comment.getUserId();

            SyncHttpClient myclient=new SyncHttpClient();
            SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
            Log.d("Cookie",settings.getString("Set-Cookie",""));
            myclient.addHeader("Cookie",settings.getString("Set-Cookie",""));
            myclient.get(urlUser, new JsonHttpResponseHandler() {
                // client.get("http://10.0.2.2:3000/api/venues/search/", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                    // Pull out the first event on the public timeline
                    List<String> list = new ArrayList<>();
                    JSONObject userObject = timeline;
                    //Log.d("venue",Friends_data.toString());
                    try {
                        User_Bean bean = new User_Bean();
                        bean.setId(userObject.getString("_id"));
                        bean.setFirstName(userObject.getString("firstName"));
                        bean.setLastName(userObject.getString("lastName"));
                        bean.setEmailId((userObject.getString("emailId")));
                        bean.setUrl(userObject.getString("url"));
                        pathurl=bean.getUrl();
                        //Toast.makeText(UserProfile.this,"in picasso-"+url,Toast.LENGTH_LONG).show()

                       // profileUser=bean;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });


            // Fill the view
            ImageView imageView = (ImageView) itemView.findViewById(R.id.item_p_img);
            //imageView.setImageResource(1);
            // Make:
            if(pathurl !=null &&pathurl.length()>0){
                Picasso.with(Venue_Profile.this).load(pathurl)
                        .placeholder(imageView.getDrawable())
                        .fit()
                        .into(imageView);

            }

            TextView makeText = (TextView) itemView.findViewById(R.id.item_vistor_firstname);
            makeText.setText(comment.getFirstName()+"");

            // Year:
            TextView yearText = (TextView) itemView.findViewById(R.id.item_vistor_lastname);
            yearText.setText(comment.getLastName());
            // Condition:
            return itemView;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData( );
            selectedImagePath = getPath( getApplicationContext( ), selectedImageUri );
            Log.d("Picture Path", selectedImagePath);
        }
    }

    public static String getPath(Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }

    public void Gamification (){
        SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
        String id = settings.getString("id","");
        Log.d("Cookie",settings.getString("Set-Cookie",""));
        AsyncHttpClient myclient = new AsyncHttpClient();
        myclient.addHeader("Cookie",settings.getString("Set-Cookie",""));
        RequestParams params = new RequestParams();

        //general calculations based on other methods
        //this is called in onsuccess methods of
        //checkin
        //rating
        //comment
        params.put("userId",id);
        Log.d("ratings",""+ ratings);
        Log.d("checkins",""+ checkins);
        Log.d("comments",""+comments);
        if(checkins<5 || ratings<5 || comments<5){
            params.put("badgeName","beginner");
            Log.d("params","beginner");
        }
        if(checkins>5 && checkins<10 || ratings>5 && ratings<10 || comments>5 && comments<10 ){
            params.put("badgeName","Adventure");
            Log.d("params","Adventure");
        }
        if(checkins>4 || ratings>5 || comments >5 ){
            params.put("badgeName","Pro");
            Log.d("params","pro");
        }
        if(ratings>5 && ratings <10 && comments >5 ){
            params.put("badgeName","Picky");
            Log.d("params","picky");
        }  if(ratings >9 &&  comments>9){
            params.put("badgeName","Critic");
            Log.d("params","critic");
        } if(checkins>10 && ratings>10 && comments>10){
            params.put("badgeName","Legend");
            Log.d("params","legend");
        }
        Log.d("params","");
        String url="https://laware.herokuapp.com/api/gamification/";
        myclient.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            // notification on new badge
            Log.d("in gamification success","i am at pro level");
            } });




    }
    public void CommentGamification(){
        SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
        String id = settings.getString("id","");
        Log.d("Cookie",settings.getString("Set-Cookie",""));
        AsyncHttpClient myclient = new AsyncHttpClient();
        myclient.addHeader("Cookie",settings.getString("Set-Cookie",""));
        String url="https://laware.herokuapp.com/api/comment/user/"+id;
        myclient.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Checkin_bean> checkinList = new ArrayList<>();
                JSONArray object = response;

                comments=object.length();

            }

        });

    }
    public void checkinGamigication(){
        SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
        String id = settings.getString("id","");
        Log.d("Cookie",settings.getString("Set-Cookie",""));
        AsyncHttpClient myclient = new AsyncHttpClient();
        myclient.addHeader("Cookie",settings.getString("Set-Cookie",""));
        String url="https://laware.herokuapp.com/api/checkin/user/"+id;
        myclient.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Checkin_bean> checkinList = new ArrayList<>();
                JSONArray object = response;

                checkins=object.length();

            }

        });

    }

    public void ratingGamigication(){
        SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
        String id = settings.getString("id","");
        Log.d("Cookie",settings.getString("Set-Cookie",""));
        AsyncHttpClient myclient = new AsyncHttpClient();
        myclient.addHeader("Cookie",settings.getString("Set-Cookie",""));
        String url="https://laware.herokuapp.com/api/rating/user/"+id;
        myclient.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Checkin_bean> checkinList = new ArrayList<>();
                JSONArray object = response;

               ratings=object.length();

            }

        });


    }
    @Override
    protected void onStop() {
        super.onStop();


    }


}




