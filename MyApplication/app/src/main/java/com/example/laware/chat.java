package com.example.laware;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laware.com.example.laware.beans.Messages;
import com.example.laware.com.example.laware.beans.User_Bean;
import com.example.laware.com.example.laware.beans.Venue_Bean;
import com.example.laware.com.example.laware.user.login;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class chat extends AppCompatActivity {
    List<Messages> list= new ArrayList<>();
    TextView messages;
    EditText text;
    Button send;
    String id;
    String fname;
    String lname;
    String friendId;
    Timer  myTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_chat);
        SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);

        id=settings.getString("id","");
        fname=settings.getString("firstName","");
        lname=settings.getString("lastName","");

        //friendid in bundle
        //load messages based on friend id
        //inseart messages
        //update messages
        Bundle bundle = getIntent().getExtras();
        friendId = bundle.getString("friendId");
        messages=(TextView) findViewById(R.id.textView16);


        update();

       myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }

        }, 0, 5000);
        messages=(TextView) findViewById(R.id.textView16);
        text= (EditText) findViewById(R.id.editText);

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text.setText("");
            }
        });
         send=(Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send messages post command
                RequestParams params = new RequestParams();
                params.put("friendId",friendId);
                params.put("userId",id);
                params.put("firstname", fname);
                params.put("lastname",lname);
                params.put("message", text.getText());
                text.setText("Message");
                Date date= new Date();
                params.put("date",date);
                params.put("time", date.getTime());
                AsyncHttpClient client = new AsyncHttpClient();

                SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
                Log.d("Cookie", settings.getString("Set-Cookie", ""));
                client.addHeader("Cookie", settings.getString("Set-Cookie", ""));


                client.post("https://laware.herokuapp.com/api/messages/", params, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {

                        JSONObject object = timeline;
                        Log.d("messages", object.toString());
                     //   Intent i= new Intent(chat.this,chat.class);
                      //  i.putExtra("friendId",friendId);
                       // startActivity(i);

                    }

                });


            }
        });

    }

    public void update(){
        messages.setText("");
        AsyncHttpClient client = new AsyncHttpClient();
        //  client.get("https://laware.herokuapp.com/api/venues/search/", new JsonHttpResponseHandler() {

        SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
        Log.d("Cookie", settings.getString("Set-Cookie", ""));
        client.addHeader("Cookie", settings.getString("Set-Cookie", ""));
        client.get("https://laware.herokuapp.com/api/messages/"+friendId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                //   List<String> list = new ArrayList<>();
                JSONArray venue_data = timeline;
                messages.setText("");
                Log.d("venue", venue_data.toString());
                for (int i = 0; i < venue_data.length(); i++) {
                    try {
                        //String venueObject = venue_data.getString(i);
                        // Venue_Bean bean = new Venue_Bean(venueObject.getString("_id"),venueObject.getString("name"),venueObject.getString("address"),venueObject.getString("long"),venueObject.getString("lat"),venueObject.getString("category"));
                        //                Venue_Bean bean = new Venue_Bean();
                        JSONObject venueObject = venue_data.getJSONObject(i);

                        Messages bean= new Messages();
                        bean.setId(venueObject.getString("_id"));
                        bean.setFriendId(venueObject.getString("friendsId"));
                        bean.setFirstname(venueObject.getString("firstname"));
                        bean.setLastname(venueObject.getString("lastname"));
                        bean.setUserId(venueObject.getString("userId"));
                        bean.setDate(venueObject.getString("date"));
                        bean.setTime(venueObject.getString("time"));
                        bean.setMessage(venueObject.getString("message"));
                        list.add(bean);
                        Log.d("in andriod on success ", bean.getMessage());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                String data="";
                for (Messages bean: list){
                    Log.d("bean",bean.getMessage());
                    if(bean.getUserId().equals(id)){
                        data +=""+bean.getMessage()+"<br><ul>"+fname+","+lname+"<ul>";

                    }else{
                        data +=""+bean.getMessage()+"<br><ul>"+bean.getFirstname()+","+bean.getLastname()+"</ul>";

                    }
                    data+="<br>---------------<br>";
                    //messages.setText("");

                }
                list.clear();

                // data = data.replace("\\\n",System.getProperty("line.separator"));
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    messages.setText(Html.fromHtml(data,Html.FROM_HTML_MODE_LEGACY));
                } else {
                    messages.setText(Html.fromHtml(data));
                }
                messages.setMovementMethod(new ScrollingMovementMethod());



            }
        });



    }
    private void TimerMethod()
    {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(Timer_Tick);
    }


    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            update();
            //This method runs in the same thread as the UI.

            //Do something to the UI thread here

        }
    };
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();

    }

}
