package com.example.laware;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.laware.com.example.laware.beans.Gamification_bean;
import com.example.laware.com.example.laware.beans.User_Bean;
import com.example.laware.com.example.laware.user.login;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class Gamification extends AppCompatActivity {
    ImageView level1;
    ImageView level2;
    ImageView level3;
    ImageView level4;
    ImageView level5;
    ImageView level6;
    TextView level1_txt;
    TextView level2_txt;
    TextView level3_txt;
    TextView level4_txt;
    TextView level5_txt;
    TextView level6_txt;
    List<Gamification_bean> list= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamification);
        SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
       // String userId=settings.getString("id","");
        Bundle bundle = getIntent().getExtras();
        final String userId = bundle.getString("userId");


        Log.d("Cookie",settings.getString("Set-Cookie",""));
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Cookie",settings.getString("Set-Cookie",""));
         level1=(ImageView) findViewById(R.id.imageView3);
         level1_txt=(TextView) findViewById(R.id.Amethyst);
         level2=(ImageView) findViewById(R.id.imageView5);
         level2_txt=(TextView) findViewById(R.id.Citrine);
         level3=(ImageView) findViewById(R.id.imageView6);
         level3_txt=(TextView) findViewById(R.id.Emerald);
         level4=(ImageView) findViewById(R.id.imageView7);
         level4_txt=(TextView) findViewById(R.id.Gemstone);
         level5=(ImageView) findViewById(R.id.imageView9);
         level5_txt=(TextView) findViewById(R.id.Peridot);
         level6=(ImageView) findViewById(R.id.imageView10);
         level6_txt=(TextView) findViewById(R.id.Zircon);
//get list for db and high lighte the badges with names
        String urlGamification="https://laware.herokuapp.com/api/gamification/"+userId;
        client.get(urlGamification, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                SharedPreferences.Editor editor = getSharedPreferences("laware", MODE_PRIVATE).edit();

                JSONArray object = timeline;
                Log.d("venue", object.toString());
                try {
                    //  String error=object.getString("error");


                        for (int i = 0; i < object.length(); i++) {
                            JSONObject userObject = object.getJSONObject(i);
                            Gamification_bean bean= new Gamification_bean();
                             bean.setId(userObject.getString("_id"));
                            bean.setUserId(userObject.getString("userId"));
                            bean.setBadgeName(userObject.getString("badgeName"));
                            list.add(bean);
                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                    for(int i=0;i<list.size();i++){
                        Gamification_bean bean= list.get(i);
                        if(bean.getBadgeName().equals("beginner")){
                            level1.setAlpha(1.0f);
                            level1_txt.setAlpha(1.0f);
                            level1_txt.setText(bean.getBadgeName());

                        }
                        if(bean.getBadgeName().equals("Adventure")){
                            level2.setAlpha(1.0f);
                            level2_txt.setAlpha(1.0f);
                            level2_txt.setText(bean.getBadgeName());

                        }
                        if(bean.getBadgeName().equals("Pro")){
                            level3.setAlpha(1.0f);
                            level3_txt.setAlpha(1.0f);
                            level3_txt.setText(bean.getBadgeName());

                        }
                        if(bean.getBadgeName().equals("Picky")){
                            level4.setAlpha(1.0f);
                            level4_txt.setAlpha(1.0f);
                            level4_txt.setText(bean.getBadgeName());

                        }
                        if(bean.getBadgeName().equals("Critic")){
                            level5.setAlpha(1.0f);
                            level5_txt.setAlpha(1.0f);
                            level5_txt.setText(bean.getBadgeName());

                        }
                        if(bean.getBadgeName().equals("Legend")){
                            level6.setAlpha(1.0f);
                            level6_txt.setAlpha(1.0f);
                            level6_txt.setText(bean.getBadgeName());

                        }


                        }

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();

    }
}
