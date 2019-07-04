package com.example.laware.com.example.laware;

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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.laware.Home;
import com.example.laware.R;
import com.example.laware.Venue_Profile;
import com.example.laware.com.example.laware.beans.User_Bean;
import com.example.laware.com.example.laware.user.UserProfile;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class Create_Account extends AppCompatActivity {
    ImageButton Profile_image_upload;
    EditText firstname;
    EditText lastname;
    EditText emailaddress;
    EditText password;
    CheckBox checkBox2;
    Button create_page_save;
    Button create_page_cancel;
    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    String url;
    User_Bean bean;
     String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__account);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Bundle bundle = getIntent().getExtras();
         userId = bundle.getString("userId");
        //set cancel button invisible
        //and image button clickable = false
        create_page_cancel = (Button)findViewById(R.id.create_page_cancel);
        create_page_cancel.setClickable(false);
        create_page_cancel.setVisibility(View.INVISIBLE);
        Profile_image_upload = (ImageButton) findViewById(R.id.Profile_image_upload);
        checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
        checkBox2.setVisibility(View.INVISIBLE);
        checkBox2.setClickable(false);
        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastname);
        emailaddress = (EditText) findViewById(R.id.emailaddress);
        create_page_save = (Button) findViewById(R.id.button14);

        password = (EditText) findViewById(R.id.password);

        final AsyncHttpClient client = new AsyncHttpClient();


        Log.d("User id ",userId);
        if (userId!= null && userId.length()>0) {
            // work for editing profile
            //change
            //call data from server get
            Log.d("before calling  ",userId);
            String   urlUser="https://laware.herokuapp.com/api/users/"+userId;
            SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
            Log.d("Cookie",settings.getString("Set-Cookie",""));
            client.addHeader("Cookie",settings.getString("Set-Cookie",""));

            client.get(urlUser, new JsonHttpResponseHandler() {
                // client.get("http://10.0.2.2:3000/api/venues/search/", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                    // Pull out the first event on the public timeline
                    List<String> list = new ArrayList<>();
                    JSONObject userObject = timeline;
                    //Log.d("venue",Friends_data.toString());
                    try {
                        bean = new User_Bean();
                        bean.setId(userObject.getString("_id"));
                        bean.setFirstName(userObject.getString("firstName"));
                        bean.setLastName(userObject.getString("lastName"));
                        bean.setEmailId((userObject.getString("emailId")));
                        bean.setPassword(userObject.getString("password"));
                        bean.setUrl(userObject.getString("url"));
                        url=bean.getUrl();
                        emailaddress.setText(bean.getEmailId());
                        firstname.setText(bean.getFirstName());
                        lastname.setText(bean.getLastName());
                        password.setText(bean.getPassword());

                        //Toast.makeText(UserProfile.this,"in picasso-"+url,Toast.LENGTH_LONG).show();

                        Picasso.with(Create_Account.this).load(url)
                                .placeholder(Profile_image_upload.getDrawable())
                                .fit()
                                .into(Profile_image_upload);

                       // profileUser=bean;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

            create_page_cancel.setVisibility(View.VISIBLE);
            create_page_cancel.setClickable(true);
            create_page_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //return to userProfile
                    //send userId
                    finish();
                }
            });
            Profile_image_upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // select pic
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,
                            "Select Picture"), SELECT_PICTURE);


                }
            });


            create_page_save = (Button) findViewById(R.id.button14);
            create_page_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //save user but before that upload pic to cloudinary
                    //toast notification/'"
                    if (firstname.length() > 0 && lastname.length() > 0 && emailaddress.length() > 0 && password.length() > 0) {
                        String url = "https://laware.herokuapp.com/api/users/"+userId;
                        RequestParams params = new RequestParams();
                        params.put("id",userId);
                        params.put("firstName", firstname.getText());
                        params.put("lastName", lastname.getText());
                        params.put("emailId", emailaddress.getText());
                        params.put("password", password.getText());
                      //   Toast.makeText(Create_Account.this,"firstname "+firstname.getText(),Toast.LENGTH_LONG).show();

                       // Toast.makeText(Create_Account.this,"password"+ password.getText(),Toast.LENGTH_LONG).show();
                        //Toast.makeText(Create_Account.this,"lastname "+lastname.getText(),Toast.LENGTH_LONG).show();


                        if (selectedImagePath != null) {

                            // Toast.makeText(Create_Account.this,"in picasso",Toast.LENGTH_LONG).show();
                            Map config = ObjectUtils.asMap(
                                    "cloud_name", "dxlimxfwc",
                                    "api_key", "518816883576142",
                                    "api_secret", "vC0lWO6wTxXm3DnPFrzHrbOHLoM");
                            Cloudinary cloudinary = new Cloudinary(config);
                            try {
                                Map uploadResult = cloudinary.uploader().upload(selectedImagePath, ObjectUtils.emptyMap());
                                String c_url = (String) uploadResult.get("url");
                                Log.d("Cloundinary", c_url);
                                if (c_url != "") {
                      /*      Picasso.with(Create_Account.this).load(c_url)
                                    .placeholder(Profile_image_upload.getDrawable())
                                    .fit()
                                    .into(Profile_image_upload);
*/
                                    params.put("url", c_url);
                                } else {

                                    params.put("url",bean.getUrl());
                                }


                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            params.put("url",bean.getUrl());
                        }


                        client.put(url, params, new JsonHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                                Toast.makeText(Create_Account.this, "in on-success", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                //    fbDialogue.cancel();
                               // Toast.makeText(Create_Account.this, "in on-success-object", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(Create_Account.this, UserProfile.class);
                              //  SharedPreferences settings = getSharedPreferences("laware", MODE_PRIVATE);
                                i.putExtra("userId", userId);
                                startActivity(i);
                            }
                        });
                    } else {
                        Toast.makeText(Create_Account.this, "all fields must be ", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else {

            Profile_image_upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // select pic
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,
                            "Select Picture"), SELECT_PICTURE);


                }
            });
            firstname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firstname.setText("");
                }
            });
            lastname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //do nothing for now
                    lastname.setText("");
                }
            });
            emailaddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //for now do nothing
                    emailaddress.setText("");
                }
            });
        password.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //for now do nothing
                    password.setText("");
                }
            });
            checkBox2.setVisibility(View.VISIBLE);
            checkBox2.setClickable(true);
            checkBox2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //do nothing
                }
            });
            create_page_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //save user but before that upload pic to cloudinary
                    //toast notification/'"
                    if (checkBox2.isChecked() && firstname.length() > 0 && lastname.length() > 0 && emailaddress.length() > 0 && password.length() > 0) {
                        String url = "https://laware.herokuapp.com/api/users/";
                        RequestParams params = new RequestParams();

                        params.put("firstName", firstname.getText());
                        params.put("lastName", lastname.getText());
                        params.put("emailId", emailaddress.getText());
                        params.put("password", password.getText());


                        if (selectedImagePath != null) {

                            // Toast.makeText(Create_Account.this,"in picasso",Toast.LENGTH_LONG).show();
                            Map config = ObjectUtils.asMap(
                                    "cloud_name", "dxlimxfwc",
                                    "api_key", "518816883576142",
                                    "api_secret", "vC0lWO6wTxXm3DnPFrzHrbOHLoM");
                            Cloudinary cloudinary = new Cloudinary(config);
                            try {
                                Map uploadResult = cloudinary.uploader().upload(selectedImagePath, ObjectUtils.emptyMap());
                                String c_url = (String) uploadResult.get("url");
                                Log.d("Cloundinary", c_url);
                                if (c_url != "") {
                      /*      Picasso.with(Create_Account.this).load(c_url)
                                    .placeholder(Profile_image_upload.getDrawable())
                                    .fit()
                                    .into(Profile_image_upload);
*/
                                    params.put("url", c_url);
                                } else {
                                    params.put("url", c_url);
                                }


                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            params.put("url", "");
                        }


                        client.post(url, params, new JsonHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                                Toast.makeText(Create_Account.this, "in on-success", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                //    fbDialogue.cancel();
                                Toast.makeText(Create_Account.this, "in on-success-object", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(Create_Account.this, "all fields must be ", Toast.LENGTH_LONG).show();
                    }
                }
            });


        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData( );
            selectedImagePath = getPath( getApplicationContext( ), selectedImageUri );
            Profile_image_upload.setImageURI(Uri.parse(selectedImagePath));

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
    @Override
    protected void onStop() {
        super.onStop();


    }

}
