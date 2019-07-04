package com.example.laware.com.example.laware.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.laware.Friends_list;
import com.example.laware.Home;
import com.example.laware.R;
import com.example.laware.com.example.laware.Create_Account;
import com.example.laware.com.example.laware.Password_recovery;
import com.example.laware.com.example.laware.beans.Friend_Bean;
import com.example.laware.com.example.laware.beans.User_Bean;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by muhammad-usman on 30.07.17.
 */


public class login extends AppCompatActivity {
    EditText email;
    EditText password;
    AsyncHttpClient client;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        client = new AsyncHttpClient();
      //  PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
       // client.setCookieStore(myCookieStore);
        SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
        Log.d("Cookie",settings.getString("Set-Cookie",""));
        String Cookie=settings.getString("Set-Cookie","");
       // Toast.makeText(login.this,"in loin creat-"+Cookie,Toast.LENGTH_LONG).show();

        if(Cookie!=null && Cookie.length()>0) {
        client.addHeader("Cookie", settings.getString("Set-Cookie", ""));
            Intent i = new Intent(login.this, Home.class);
            startActivity(i);
        }
        email = (EditText) findViewById(R.id.editText2);
        password = (EditText) findViewById(R.id.password);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email.setText("");
            }
        });
        Button sign_button = (Button) findViewById(R.id.sign_button);
        sign_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //       String urlFriend="https://laware.herokuapp.com/login";
                if (email.getText().length()>0 && password.getText().length()>0) {
                    String urlFriend = "https://laware.herokuapp.com/login";

                    RequestParams params = new RequestParams();
                    params.put("emailId", email.getText());
                    params.put("password", password.getText());

                    client.post(urlFriend, params, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                            SharedPreferences.Editor editor = getSharedPreferences("laware", MODE_PRIVATE).edit();

                            JSONObject object = timeline;
                            Log.d("venue", object.toString());
                            try {
                                //  String error=object.getString("error");
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

                                    User_Bean bean = new User_Bean();
                                    bean.setId(userObject.getString("_id"));
                                    bean.setFirstName(userObject.getString("firstName"));
                                    bean.setLastName(userObject.getString("lastName"));
                                    bean.setEmailId(userObject.getString("emailId"));
                                    bean.setPassword(userObject.getString("password"));
                                    bean.setUrl(userObject.getString("url"));
                                    Log.d("in andriod on success ", bean.getLastName());
                                    editor.putString("id", bean.getId());
                                    editor.putString("firstName", bean.getFirstName());
                                    editor.putString("lastName", bean.getLastName());
                                    editor.putString("email", bean.getEmailId());
                                    editor.putString("password", bean.getPassword());
                                    editor.putString("url", bean.getUrl());
                                    editor.apply();
                                    Intent i = new Intent(login.this, Home.class);
                                    startActivity(i);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    });
                }else{
               //     Toast.makeText(login.this,"Enter password and emailId-",Toast.LENGTH_LONG).show();

                }
            }
        });
        Button forgotpassword = (Button) findViewById(R.id.forgotpassword);
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(login.this, Password_recovery.class);
                startActivity(i);
            }
        });
        Button new_account = (Button) findViewById(R.id.new_account);
        new_account.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent i = new Intent(login.this, Create_Account.class);
                i.putExtra("userId", "");
                startActivity(i);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();


    }
    /*@Override
    protected void onResume() {
        SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
        Log.d("Cookie",settings.getString("Set-Cookie",""));
        String Cookie=settings.getString("Set-Cookie","");
       // Toast.makeText(login.this,"in login -"+Cookie,Toast.LENGTH_LONG).show();
Log.d("in login resume",Cookie);
        if(Cookie!=null ) {
            client.addHeader("Cookie", settings.getString("Set-Cookie", ""));
            Intent i = new Intent(login.this, Home.class);
            startActivity(i);
        }else {
            super.onResume();
        }

    }*/
}
