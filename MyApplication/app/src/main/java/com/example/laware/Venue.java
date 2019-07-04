package com.example.laware;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.Serializable;
import java.net.HttpURLConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

import com.example.laware.com.example.laware.beans.Comment_Bean;
import com.example.laware.com.example.laware.beans.Venue_Bean;
import com.example.laware.com.example.laware.user.UserProfile;
import com.loopj.android.http.*;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import cz.msebera.android.httpclient.Header;

import static com.example.laware.R.id.imageView;

public class Venue extends AppCompatActivity {
    List<Venue_Bean> list= new ArrayList<>();
    List<Comment_Bean> list2= new ArrayList<>();
    Button map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
        map=(Button) findViewById(R.id.button13);
        map.setVisibility(View.INVISIBLE);
        map.setClickable(false);

        final String id = settings.getString("id","");

        Bundle bundle = getIntent().getExtras();
        final String search_txt = bundle.getString("search");
        final String type_txt= bundle.getString("type");
        final String liked=bundle.getString("liked");
        final String userId=bundle.getString("userId");
      //String url="https://laware.herokuapp.com/api/venues/";
        //  String url="http://10.0.2.2:3000/api/venues/";
      String url="https://laware.herokuapp.com/api/venues/";
       // String url="https://laware.herokuapp.com/api/venues/";
        if(search_txt !=null&& search_txt.length()>0){
            url+="name/"+search_txt;
        }else if(type_txt !=null && type_txt.length()>0){
            url+="type/"+type_txt;
        }else if(liked!=null && liked.length()>0){
            //  String url="http://10.0.2.2:3000/api/venues/";
             url="https://laware.herokuapp.com/api/rating/likedplaces/"+userId;
           // url="https://laware.herokuapp.com/api/rating/likedplaces/"+userId;

        }
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Venue.this, MapList.class);

                if(search_txt !=null && search_txt.length()>0){
                    in.putExtra("search", search_txt);
                }else if(type_txt !=null && type_txt.length()>0){
                    in.putExtra("type", type_txt);
                }else if(liked!=null && liked.length()>0){
                    in.putExtra("liked", liked);
                }

                startActivity(in);





            }
        });




                    AsyncHttpClient client = new AsyncHttpClient();
//        SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
        Log.d("Cookie",settings.getString("Set-Cookie",""));
        client.addHeader("Cookie",settings.getString("Set-Cookie",""));

        //client.get("https://laware.herokuapp.com/api/venues/name/" + search_txt, new JsonHttpResponseHandler() {
                        client.get(url, new JsonHttpResponseHandler() {
                        // add data in listview and create the sample data
                        //current flow home -> search -> List of venues based on name
                        //other features will be added soon

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                            // Pull out the first event on the public timeline

                            JSONArray venue_data = timeline;
                            //String tweetText = firstEvent.getString("text");

                            for (int i = 0; i < venue_data.length(); i++) {
                                try {
                                    JSONObject venueObject = venue_data.getJSONObject(i);
                                    // Venue_Bean bean = new Venue_Bean(venueObject.getString("_id"),venueObject.getString("name"),venueObject.getString("address"),venueObject.getString("long"),venueObject.getString("lat"),venueObject.getString("category"));
                                    Venue_Bean bean = new Venue_Bean();
                                  if(liked!=null && liked.length()>0) {
                                      bean.setId(venueObject.getString("venueId"));
                                      bean.setName(venueObject.getString("venueName"));
                                      bean.setLat(venueObject.getString("lat"));
                                      bean.setLng(venueObject.getString("long"));
                                      bean.setAddress(venueObject.getString("address"));

                                      // /bean.setCategory();
                                  }else{
                                      bean.setId(venueObject.getString("_id"));
                                      bean.setName(venueObject.getString("name"));
                                      bean.setAddress(venueObject.getString("address"));
                                      bean.setLat(venueObject.getString("lat"));
                                      bean.setLng(venueObject.getString("long"));
                                      String categoryList = venueObject.getString("category");
                                      Log.d("categoryList", categoryList);

                                  }
                                    list.add(bean);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            map.setVisibility(View.VISIBLE);
                            map.setClickable(true);

                            populateListView();

                        }

                    });

    }
    private void populateListView() {
        ArrayAdapter<Venue_Bean> adapter = new MyListAdapter();
        ListView list1 = (ListView) findViewById(R.id.placesListView);
        list1.setAdapter(adapter);
        list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent in = new Intent(Venue.this, Venue_Profile.class);
                //String item = list.get(i).getId();
                in.putExtra("venueId",list.get(i).getId());
                startActivity(in);
            }
        });
    }
        private class MyListAdapter extends ArrayAdapter<Venue_Bean> {
        public MyListAdapter() {
            super(Venue.this, R.layout.list_view_venue, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.list_view_venue, parent, false);
            }
            Venue_Bean venue= list.get(position);

            String urlGetComments="https://laware.herokuapp.com/api/comment/"+venue.getId();
            //  String urlGetComments="https://laware.herokuapp.com/api/comment/venueId";
            SyncHttpClient myclient = new SyncHttpClient();
            SharedPreferences settingss = getSharedPreferences("laware", MODE_PRIVATE);
            Log.d("Cookie",settingss.getString("Set-Cookie",""));
            myclient.addHeader("Cookie",settingss.getString("Set-Cookie",""));
//
            myclient.get(urlGetComments, new JsonHttpResponseHandler() {

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
                            list2.add(bean);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }

            });

           String url="";
            List<String> urlList=new ArrayList<>();
            int check=0;
            for(Comment_Bean cb:list2){
                if(cb.getUrl()!=null && cb.getUrl().length()>0){
                    check=check+1;
                    url=cb.getUrl();
                    urlList.add(url);
                }
            }
           // int aNumber = (int) (check * Math.random()) + 1;

            Log.d("in adding views","veiwinf");
    // Fill the view

if(urlList.size()>0) {
    //  Random()).nextInt(urlList.size())
    ImageView imageView = (ImageView) itemView.findViewById(R.id.item_icon);
    Picasso.with(Venue.this).load(urlList.get((new Random()).nextInt(urlList.size())))
            .placeholder(imageView.getDrawable())
            .fit()
            .into(imageView);
}
    // Make:
    TextView makeText = (TextView) itemView.findViewById(R.id.item_txtMake);
    makeText.setText(venue.getName());
    // Year:
    TextView yearText = (TextView) itemView.findViewById(R.id.item_txtYear);
    yearText.setText("" + venue.getLng()+","+ venue.getLat());
    // Condition:
   TextView condionText = (TextView) itemView.findViewById(R.id.item_txtCondition);
    condionText.setText(venue.getAddress());
            return itemView;

        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        finish();

    }

}
