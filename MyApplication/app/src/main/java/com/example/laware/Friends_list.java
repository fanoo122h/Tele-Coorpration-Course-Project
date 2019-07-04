package com.example.laware;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laware.com.example.laware.beans.Friend_Bean;
import com.example.laware.com.example.laware.beans.User_Bean;
import com.example.laware.com.example.laware.beans.Venue_Bean;
import com.example.laware.com.example.laware.user.UserProfile;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.R.id.list;

public class Friends_list extends AppCompatActivity {
    List<Friend_Bean> list = new ArrayList<>();
    List<User_Bean> list2= new ArrayList<>();
    String id;
    String type="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Button go= (Button) findViewById(R.id.button_go);
        Button search_button1 = (Button) findViewById(R.id.search_button2);
        search_button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Friends_list.this, Home.class);
                startActivity(i);
            }
        });
        Button profilebutton = (Button) findViewById(R.id.profilebutton);
        profilebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Friends_list.this, UserProfile.class);
                SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
                i.putExtra("userId", settings.getString("id", ""));
                startActivity(i);
            }
        });
        Button friendbutton = (Button) findViewById(R.id.friends);
        friendbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Friends_list.this, Friends_list.class);
                i.putExtra("", "");
                startActivity(i);
            }
        });
        TextView pending= (TextView) findViewById(R.id.textView15);
        SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
        id= settings.getString("id", "");
        Bundle bundle = getIntent().getExtras();
        type= bundle.getString("myFriends");

        Button map= (Button) findViewById(R.id.button14);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Friends_list.this, MapList.class);

                in.putExtra("friends",id);
                startActivity(in);
            }
        });
        final AutoCompleteTextView search= (AutoCompleteTextView)
                findViewById(R.id.search_friend);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send data to list friend search
                //create a  activity for friend search similar to venue search
                Intent i = new Intent(Friends_list.this, Friends.class);
               // EditText search_text= (EditText) findViewById(R.id.search_friend);
                String txt= search.getText().toString();
                i.putExtra("search",txt);
                startActivity(i);
            }
        });

     //if(){

//}
      final  AsyncHttpClient client = new AsyncHttpClient();
        Log.d("Cookie",settings.getString("Set-Cookie",""));
        client.addHeader("Cookie",settings.getString("Set-Cookie",""));
//        String urlFriend="https://laware.herokuapp.com/api/friend/"+id;
        String urlFriend="https://laware.herokuapp.com/api/friend/"+id;
        client.get(urlFriend, new JsonHttpResponseHandler() {
            // client.get("http://10.0.2.2:3000/api/venues/search/", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                List<String> list= new ArrayList<>();
                JSONArray Friends_data = timeline;
                Log.d("venue",Friends_data.toString());
                for (int i = 0; i < Friends_data.length(); i++) {
                    try {

                        JSONObject userObject = Friends_data.getJSONObject(i);
                        // Venue_Bean bean = new Venue_Bean(venueObject.getString("_id"),venueObject.getString("name"),venueObject.getString("address"),venueObject.getString("long"),venueObject.getString("lat"),venueObject.getString("category"));
                        Friend_Bean bean = new Friend_Bean();
                        bean.setId(userObject.getString("_id"));
                        bean.setUserId(userObject.getString("id1"));
                        bean.setUserId2(userObject.getString("id2"));
                        bean.setFirstName(userObject.getString("firstname"));
                        bean.setLastName(userObject.getString("lastname"));
                        bean.setFirstname2(userObject.getString("firstname2"));
                        bean.setLastname2(userObject.getString("lastname2"));
                        bean.setPending(userObject.getInt("pending"));
                        if(id.equals(bean.getUserId())){
                            list.add(bean.getFirstname2());

                        }else{
                            list.add(bean.getFirstName());

                        }
                        Log.d("in andriod on success ",bean.getLastname2());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (Friends_list.this, android.R.layout.select_dialog_item,list);

              if(list.size()>0) {
                  search.setThreshold(2);
                  search.setAdapter(adapter);
              }
            }
        });

        final Button myFriends=(Button) findViewById(R.id.button6);
        myFriends.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i=new Intent(Friends_list.this, Friends_list.class);
               if(myFriends.getText().equals("My Friends")) {
                   i.putExtra("myFriends", "1");
               }else{
                   i.putExtra("", "");
               }
                   startActivity(i);
            }
        });

        //  AsyncHttpClient client = new AsyncHttpClient();
        //String url="https://laware.herokuapp.com/api/venues/";
        //  String url="http://10.0.2.2:3000/api/venues/";

//        String url = "http://10.0.2.2:3000/api/friend/pending/" + id;
       // String url = "http://192.168.0.103:3000/api/friend/pending/"+id;
String url;
        if(type!=null && type.length()>0){
            map.setVisibility(View.VISIBLE);
            map.setClickable(true);
            myFriends.setText("Pending friends");
            pending.setText("My Friends");
            url = "https://laware.herokuapp.com/api/friend/"+id;
        //    url="https://laware.herokuapp.com/api/friend/"+id;

}else{
            map.setVisibility(View.INVISIBLE);
            map.setClickable(false);
            myFriends.setText("My Friends");
            pending.setText("pending friends");
           url = "https://laware.herokuapp.com/api/friend/pending/"+id;
          //    url="https://laware.herokuapp.com/api/friend/pending/"+id;

}

        //client.get("https://laware.herokuapp.com/api/venues/name/" + search_txt, new JsonHttpResponseHandler() {
        client.get(url, new JsonHttpResponseHandler() {
            // add data in listview and create the sample data
            //current flow home -> search -> List of venues based on name
            //other features will be added soon

            // /  @Override
            // public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            // If the response is JSONObject instead of expected JSONArray
            //  }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline

                JSONArray user_data = timeline;
                //String tweetText = firstEvent.getString("text");

                for (int i = 0; i < user_data.length(); i++) {
                    try {
                        JSONObject userObject = user_data.getJSONObject(i);
                        // Venue_Bean bean = new Venue_Bean(venueObject.getString("_id"),venueObject.getString("name"),venueObject.getString("address"),venueObject.getString("long"),venueObject.getString("lat"),venueObject.getString("category"));
                        Friend_Bean bean = new Friend_Bean();
                        bean.setId(userObject.getString("_id"));
                        bean.setUserId(userObject.getString("id1"));
                        bean.setUserId2(userObject.getString("id2"));
                        bean.setFirstName(userObject.getString("firstname"));
                        bean.setLastName(userObject.getString("lastname"));
                        bean.setFirstname2(userObject.getString("firstname2"));
                        bean.setLastname2(userObject.getString("lastname2"));
                        bean.setPending(userObject.getInt("pending"));
                        list.add(bean);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
             //   call another http call and populate list on success
                for(Friend_Bean ss:list) {

                    String   urlUser="https://laware.herokuapp.com/api/users/";
                    if(!id.equals(ss.getUserId())){
                        urlUser+=ss.getUserId();
                    }else{
                        urlUser+=ss.getUserId2();
                    }
                    SyncHttpClient myclient = new SyncHttpClient();
                    SharedPreferences settingss = getSharedPreferences("laware", MODE_PRIVATE);
                    Log.d("Cookie",settingss.getString("Set-Cookie",""));
                   myclient.addHeader("Cookie",settingss.getString("Set-Cookie",""));
//
                    myclient.get(urlUser, new JsonHttpResponseHandler() {
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
                                        //   call another http call and populate list on success
                                   list2.add(bean);
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }

                        }

                    });
                }
                populateListView();


            }

        });

    }

    private void populateListView() {
        ArrayAdapter<Friend_Bean> adapter = new MyListAdapter();
        ListView list1 = (ListView) findViewById(R.id.ListView);
        list1.setAdapter(adapter);
        list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent in = new Intent(Friends_list.this, UserProfile.class);
                //String item = list.get(i).getId();
                String counter;
                if(!id.equals(list.get(i).getUserId())){
                counter=list.get(i).getUserId();
                }else{
                counter=list.get(i).getUserId2();
                }

                in.putExtra("userId",counter);
                in.putExtra("FriendId", list.get(i).getId());
                startActivity(in);
            }
        });
    }

    private class MyListAdapter extends ArrayAdapter<Friend_Bean> {
        public MyListAdapter() {
            super(Friends_list.this, R.layout.list_view_friends, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.list_view_venue, parent, false);
            }
            Friend_Bean friend = list.get(position);
            if(!id.equals(friend.getUserId())){

//call http asyn to fetch user details and add then in the list
            ImageView imageView = (ImageView) itemView.findViewById(R.id.item_icon);
            for(User_Bean aa:list2) {
                Toast.makeText(Friends_list.this,"in picasso-"+aa.getFirstName(), Toast.LENGTH_LONG).show();
                if (aa.getId().equals(friend.getUserId()) && aa.getUrl() != null && aa.getUrl().length() > 0) {
                    Picasso.with(Friends_list.this).load(aa.getUrl())
                            .placeholder(imageView.getDrawable())
                            .fit()
                            .into(imageView);
                }
            }
                // Make:
            TextView makeText = (TextView) itemView.findViewById(R.id.item_txtMake);
            makeText.setText(friend.getFirstname2());
            // Year:
            //   TextView yearText = (TextView) itemView.findViewById(R.id.item_txtYear);
            // yearText.setText("" +friend.getEmailId());
            // Condition:
            TextView condionText = (TextView) itemView.findViewById(R.id.item_txtCondition);
            condionText.setText(friend.getLastname2());
}else{


                ImageView imageView = (ImageView) itemView.findViewById(R.id.item_icon);
                for(User_Bean aa:list2) {
                  //  Toast.makeText(Friends_list.this,"in picasso-"+aa.getUrl(), Toast.LENGTH_LONG).show();
                    if (aa.getId().equals(friend.getUserId2()) && aa.getUrl() != null && aa.getUrl().length() > 0) {
                        Picasso.with(Friends_list.this).load(aa.getUrl())
                                .placeholder(imageView.getDrawable())
                                .fit()
                                .into(imageView);
                    }
                }
    TextView makeText = (TextView) itemView.findViewById(R.id.item_txtMake);
    makeText.setText(friend.getFirstName());
    // Year:
    //   TextView yearText = (TextView) itemView.findViewById(R.id.item_txtYear);
    // yearText.setText("" +friend.getEmailId());
    // Condition:
    TextView condionText = (TextView) itemView.findViewById(R.id.item_txtCondition);
    condionText.setText(friend.getLastName());

}

            return itemView;

        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        finish();

    }

}

