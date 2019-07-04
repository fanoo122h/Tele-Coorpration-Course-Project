package com.example.laware;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.laware.com.example.laware.beans.User_Bean;
import com.example.laware.com.example.laware.beans.Venue_Bean;
import com.example.laware.com.example.laware.user.UserProfile;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Arslanyasinwattoo on 8/15/2017.
 */

public class Friends extends AppCompatActivity {
    List<User_Bean> list= new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        Bundle bundle = getIntent().getExtras();
        final String search_txt = bundle.getString("search");
        Log.d("loging search",search_txt);
        AsyncHttpClient client = new AsyncHttpClient();

       SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
        Log.d("Cookie",settings.getString("Set-Cookie",""));
        client.addHeader("Cookie",settings.getString("Set-Cookie",""));
        String url="https://laware.herokuapp.com/api/users/name/"+search_txt;
        client.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline

                JSONArray venue_data = timeline;
                for (int i = 0; i < venue_data.length(); i++) {
                    try {
                        JSONObject venueObject = venue_data.getJSONObject(i);
                        User_Bean bean = new User_Bean();
                            bean.setId(venueObject.getString("_id"));
                            bean.setFirstName(venueObject.getString("firstName"));
                            bean.setLastName(venueObject.getString("lastName"));
                            bean.setEmailId(venueObject.getString("emailId"));
                            bean.setUrl(venueObject.getString("url"));
                        Log.d("bean-<  ",bean.getEmailId());
                            list.add(bean);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("succoss"," aa");
                populateListView();
     }
        });

    }

    private void populateListView() {
        ArrayAdapter<User_Bean> adapter = new Friends.MyListAdapter();
        ListView list1 = (ListView) findViewById(R.id.usersListView);
        list1.setAdapter(adapter);
        list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent in = new Intent(Friends.this, UserProfile.class);
                //String item = list.get(i).getId();
                in.putExtra("userId",list.get(i).getId());
                startActivity(in);
            }
        });
    }
    private class MyListAdapter extends ArrayAdapter<User_Bean> {
        public MyListAdapter() {
            super(Friends.this, R.layout.list_user_items, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.list_user_items, parent, false);
            }
            User_Bean venue= list.get(position);
//for(Venue_Bean venue:list) {
            Log.d("in adding views","veiwinf");
            // Fill the view

            ImageView imageView = (ImageView) itemView.findViewById(R.id.item_icon_user);

            if(venue.getUrl() !=null && venue.getUrl().length()>0 ){
                Picasso.with(Friends.this).load(venue.getUrl())
                        .placeholder(imageView.getDrawable())
                        .fit()
                        .into(imageView);

            }

            // Make:
            TextView makeText = (TextView) itemView.findViewById(R.id.item_name_user);
            makeText.setText(venue.getFirstName()+","+venue.getLastName());
            // Year:
            TextView yearText = (TextView) itemView.findViewById(R.id.item_txtYear_user);
            yearText.setText(venue.getEmailId());
            // Condition:
            TextView condionText = (TextView) itemView.findViewById(R.id.item_txtCondition);
            condionText.setVisibility(View.INVISIBLE);
            //}
            return itemView;

        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        finish();

    }

}
