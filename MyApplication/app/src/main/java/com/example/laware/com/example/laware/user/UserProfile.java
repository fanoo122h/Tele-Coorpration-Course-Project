package com.example.laware.com.example.laware.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.android.MediaManager;
import com.example.laware.Friends;
import com.example.laware.Friends_list;
import com.example.laware.Gamification;
import com.example.laware.Home;
import com.example.laware.R;
import com.example.laware.Venue;
import com.example.laware.chat;
import com.example.laware.com.example.laware.Create_Account;
import com.example.laware.com.example.laware.beans.Friend_Bean;
import com.example.laware.com.example.laware.beans.Rating_bean;
import com.example.laware.com.example.laware.beans.User_Bean;
import com.example.laware.com.example.laware.beans.Venue_Bean;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;




    public class UserProfile extends AppCompatActivity {
    String id;
    String firstname;
    String lastname;
    String FriendId;
        String sysUrl;
    //String email;
    String url;
    AsyncHttpClient client;
        ImageButton profilePic;
    User_Bean profileUser= new User_Bean();
    Friend_Bean friend_bean= new Friend_Bean();
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        profilePic=(ImageButton) findViewById(R.id.imageView);
        profilePic.setClickable(false);
        Button search_button2 = (Button) findViewById(R.id.search_button2);
        search_button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(UserProfile.this, Home.class);
                startActivity(i);
            }
        });
        Button profilebutton = (Button) findViewById(R.id.profilebutton);
        profilebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(UserProfile.this, UserProfile.class);
                SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
                i.putExtra("userId", settings.getString("id", ""));
                startActivity(i);
            }
        });
        Button friendbutton = (Button) findViewById(R.id.friends);
        friendbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(UserProfile.this, Friends_list.class);
                i.putExtra("", "");
                startActivity(i);
            }
        });
       client = new AsyncHttpClient();
        SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
        Log.d("Cookie",settings.getString("Set-Cookie",""));
        client.addHeader("Cookie",settings.getString("Set-Cookie",""));
        final Button button_chat=(Button) findViewById(R.id.button_chat);
       button_chat.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent i = new Intent(UserProfile.this, chat.class);
               i.putExtra("friendId",FriendId);
               startActivity(i);

           }
       });

        id = settings.getString("id","");
        firstname=settings.getString("firstName","");
        lastname=settings.getString("lastName","");
        sysUrl=settings.getString("url","");
/*        Toast.makeText(UserProfile.this,"in picasso-"+sysUrl,Toast.LENGTH_LONG).show();

        Picasso.with(UserProfile.this).load(sysUrl)
                .placeholder(profilePic.getDrawable())
                .fit()
                .into(profilePic);
*/
        Bundle bundle = getIntent().getExtras();
        final String userId = bundle.getString("userId");

        Button badges=(Button) findViewById(R.id.button12);
        badges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserProfile.this, Gamification.class);
                i.putExtra("userId",userId);
                startActivity(i);
            }
        });
        final Button button_logout= (Button) findViewById(R.id.log_out);
        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
                Log.d("Cookie",settings.getString("Set-Cookie",""));
                String Cookie=settings.getString("Set-Cookie","");


                    client.addHeader("Cookie", settings.getString("Set-Cookie", ""));

                client.get("https://laware.herokuapp.com/logout", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                        //Toast.makeText(UserProfile.this,"in picasso-"+url,Toast.LENGTH_LONG).show();

                        settings.edit().clear().commit();
                        Intent i = new Intent(UserProfile.this, login.class);

                        startActivity(i);
                    }
                });



            }
        });

        final Button button_edit= (Button) findViewById(R.id.edit_profile);

        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        //enable fields for editing
                Intent i = new Intent(UserProfile.this, Create_Account.class);
                SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
                i.putExtra("userId", settings.getString("id", ""));
                startActivity(i);
                // profilePic.setClickable(true);
                //add pic uploading code in user profile
                //enable all fields for editing
            }
        });
        final Button button_follow= (Button) findViewById(R.id.button_follow);
            button_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(button_follow.getText().equals("Follow")||button_follow.getText().equals("follow")) {
                        String urladdFriends = "https://laware.herokuapp.com/api/friend/";
                        RequestParams params = new RequestParams();
                        params.put("id1", id);
                        params.put("id2", userId);
                        params.put("firstname", firstname);
                        params.put("lastname", lastname);
                        params.put("firstname2", profileUser.getFirstName());
                        params.put("lastname2", profileUser.getLastName());

                        client.post(urladdFriends, params, new JsonHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                                SharedPreferences.Editor editor = getSharedPreferences("laware", MODE_PRIVATE).edit();

                                JSONObject object = timeline;
                                Log.d("venue", object.toString());
                                try {
                                    if (!object.has("error")) {

                                        JSONObject userObject = object.getJSONObject("user");

                                        for (int i = 0; i < headers.length; i++) {
                                            Header hh = headers[i];
                                            if (hh.getName().equals("Set-Cookie")) {

                                                Log.d("header name", hh.getName());
                                                Log.d("header value = ", hh.getValue());
                                                editor.putString("Set-Cookie", hh.getValue());

                                            }
                                        }
                                        friend_bean.setId(userObject.getString("_id"));
                                        friend_bean.setUserId(userObject.getString("id1"));
                                        friend_bean.setUserId2(userObject.getString("id2"));
                                        friend_bean.setFirstName(userObject.getString("firstname"));
                                        friend_bean.setLastName(userObject.getString("lastname"));
                                        friend_bean.setFirstname2(userObject.getString("firstname2"));
                                        friend_bean.setLastname2(userObject.getString("lastname2"));
                                        friend_bean.setPending(Integer.parseInt(userObject.getString("pending")));
                                        button_follow.setText("Cancel");

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        });
                    }else if(button_follow.getText().equals("pending")) {
//open pop to see if user wants to  delete the request
                        //for now do nothing


                    }else if(button_follow.getText().equals("unFollow") || button_follow.getText().equals("cancel")){
// delete the friendship
                        //RequestParams params = new RequestParams();
                        //params.put("_id",friend_bean.getId());
                        Log.d("friend - id  ",friend_bean.getId());
                        String urlUnfollow="https://laware.herokuapp.com/api/friend/"+friend_bean.getId();
                        client.delete(urlUnfollow, new JsonHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                                SharedPreferences.Editor editor = getSharedPreferences("laware", MODE_PRIVATE).edit();

                                JSONObject object = timeline;
                                Log.d("venue", object.toString());
                                    if (!object.has("error")) {
                                      button_follow.setText("Follow");

                                    }
                            }
                        });

                    }else if(button_follow.getText().equals("Accept")){
//change pending to 0
                        RequestParams params = new RequestParams();
                       Log.d("id ",friend_bean.getId());
                        Log.d("id -id-> ",id);
                        Log.d("id- user id -> ",userId);
                        params.put("id",friend_bean.getId());
                        if(userId !=friend_bean.getUserId()) {
                            params.put("id1", id);
                            params.put("id2", userId);
                            params.put("firstname", friend_bean.getFirstName());
                            params.put("lastname", friend_bean.getLastName());
                            params.put("firstname2", friend_bean.getFirstname2());
                            params.put("lastname2", friend_bean.getLastname2());

                        }else{
                            params.put("id1",userId);
                            params.put("id2",  id);
                            params.put("firstname", friend_bean.getFirstname2());
                            params.put("lastname", friend_bean.getLastname2());
                            params.put("firstname2", friend_bean.getFirstName());
                            params.put("lastname2", friend_bean.getLastName());

                        }
                        params.put("pending", 0);


                        String urlAccept="https://laware.herokuapp.com/api/friend/"+friend_bean.getId();
                        client.put(urlAccept, params, new JsonHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                                SharedPreferences.Editor editor = getSharedPreferences("laware", MODE_PRIVATE).edit();

                                JSONObject object = timeline;
                                Log.d("venue", object.toString());
                                try {


                                        JSONObject userObject = timeline;
                                        friend_bean.setId(userObject.getString("_id"));
                                        friend_bean.setUserId(userObject.getString("id1"));
                                        friend_bean.setUserId2(userObject.getString("id2"));
                                        friend_bean.setFirstName(userObject.getString("firstname"));
                                        friend_bean.setLastName(userObject.getString("lastname"));
                                        friend_bean.setFirstname2(userObject.getString("firstname2"));
                                        friend_bean.setLastname2(userObject.getString("lastname2"));
                                        friend_bean.setPending(Integer.parseInt(userObject.getString("pending")));
                                        button_follow.setText("unFollow");


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        });


                    }
                }
            });


        if(id.equals(userId)){
            button_chat.setVisibility(View.INVISIBLE);
            button_chat.setClickable(false);
            button_follow.setVisibility(View.INVISIBLE);
            button_follow.setClickable(false);
            button_edit.setVisibility(View.VISIBLE);
            button_edit.setClickable(true);
            button_logout.setVisibility(View.VISIBLE);
            button_logout.setClickable(true);
        }else{
            button_chat.setVisibility(View.INVISIBLE);
            button_chat.setClickable(false);
            button_edit.setVisibility(View.INVISIBLE);
            button_edit.setClickable(false);
            button_logout.setVisibility(View.INVISIBLE);
            button_logout.setClickable(false);

        }

                Log.d("in side the loop", userId);
              String   urlUser="https://laware.herokuapp.com/api/users/"+userId;
            client.get(urlUser, new JsonHttpResponseHandler() {
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
                        url=bean.getUrl();
                        //Toast.makeText(UserProfile.this,"in picasso-"+url,Toast.LENGTH_LONG).show();
if(url !=null &&url.length()>0){
    Picasso.with(UserProfile.this).load(url)
            .placeholder(profilePic.getDrawable())
            .fit()
            .into(profilePic);

}
                        TextView profileName= (TextView) findViewById(R.id.profilename);
                        profileName.setText(bean.getFirstName()+" ,"+bean.getLastName());
                        TextView firstName= (TextView) findViewById(R.id.textView7);
                        firstName.setText(bean.getFirstName()+".");
                        TextView lastName= (TextView) findViewById(R.id.location);
                        lastName.setText(bean.getLastName()+".");
                        TextView email= (TextView) findViewById(R.id.email);
                        email.setText(bean.getEmailId()+".");
                        profileUser=bean;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });






        TextView places= (TextView) findViewById(R.id.Liked_Places);
        places.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserProfile.this, Venue.class);

                String txt="";
                   txt=userId;
                i.putExtra("liked",txt);
                i.putExtra("userId",userId);
                startActivity(i);
            }

        });
        //String urlRating="http://192.168.0.103:3000/api/rating/user/";
        //String urlRating="https://laware.herokuapp.com/api/venues/search/";
   //     if(userId!=null && userId.length()>0) {
     //       if (!userId.equals(id)) {

          String  urlRating = "https://laware.herokuapp.com/api/rating/user/"+userId;

       // String  urlRating = "https://laware.herokuapp.com/api/rating/user/"+userId;

       //     } else {
         //       urlRating = "http://192.168.0.103:3000/api/rating/user/" + id;

//            }
  //      }
//        SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
       // Log.d("Cookie",settings.getString("Set-Cookie",""));
      //  client.addHeader("Cookie",settings.getString("Set-Cookie",""));

        client.get(urlRating, new JsonHttpResponseHandler() {

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
                        rb.setRating(ratingObject.getDouble("rating"));
                        ratingList.add(rb);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                TextView rating_text = (TextView) findViewById(R.id.textView4);
                rating_text.setText(ratingList.size() + "");
            }

        });
        String urlfriends="https://laware.herokuapp.com/api/friend/"+userId;

       // String urlfriends = "https://laware.herokuapp.com/api/friend/" + userId;

        client.get(urlfriends, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<User_Bean> userList = new ArrayList<>();
                JSONArray rating = response;
                for (int i = 0; i < rating.length(); i++) {
                    try {
                        JSONObject ratingObject = rating.getJSONObject(i);
                        User_Bean rb = new User_Bean();
                        if(userId!=ratingObject.getString("id1")){
                            rb.setId(ratingObject.getString("id1"));
                            //    rb.getEmailId();
                            rb.setFirstName(ratingObject.getString("firstname"));
                            rb.setLastName(ratingObject.getString("lastname"));
                        }
                        else{
                            rb.setId(ratingObject.getString("id2"));
                            //    rb.getEmailId();
                            rb.setFirstName(ratingObject.getString("firstname2"));
                            rb.setLastName(ratingObject.getString("lastname2"));

                        }
                        friend_bean.setId(ratingObject.getString("_id"));
                        userList.add(rb);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                TextView rating_text = (TextView) findViewById(R.id.textView5);
                rating_text.setText(userList.size() + "");
            }

        });
        String urlfriendship="https://laware.herokuapp.com/api/friend/"+id+"/id/"+userId;

      //  String urlfriendship="https://laware.herokuapp.com/api/friend/"+id+"/id/"+userId;

        client.get(urlfriendship, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Friend_Bean> userList = new ArrayList<>();
                JSONArray rating = response;
                for (int i = 0; i < rating.length(); i++) {
                    try {
                        JSONObject ratingObject = rating.getJSONObject(i);
                        Friend_Bean rb = new Friend_Bean();
                        rb.setId(ratingObject.getString("_id"));
                        rb.setUserId(ratingObject.getString("id1"));
                        rb.setUserId2(ratingObject.getString("id2"));
                        rb.setFirstName(ratingObject.getString("firstname"));
                        rb.setLastName(ratingObject.getString("lastname"));
                        rb.setFirstname2(ratingObject.getString("firstname2"));
                        rb.setLastname2(ratingObject.getString("lastname2"));
                        rb.setPending(Integer.parseInt(ratingObject.getString("pending")));
friend_bean=rb;

                        if(id.equals(rb.getUserId())){
                            if(rb.getPending()==0){
                                button_follow.setText("unFollow");
                                button_chat.setVisibility(View.VISIBLE);
                                button_chat.setClickable(true);
                                FriendId=rb.getId();
                            }else{
                                button_follow.setText("cancel");

                            }
                        }else if(id.equals(rb.getUserId2())){
                            if(rb.getPending()==0){
                                button_chat.setVisibility(View.VISIBLE);
                                button_chat.setClickable(true);
                                button_follow.setText("unFollow");
                            }else{
                                button_follow.setText("Accept");
                            }

                        }
                        userList.add(rb);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });



    }
    @Override
    protected void onStop() {
        super.onStop();
     //   MediaManager.
        finish();

    }
    }

