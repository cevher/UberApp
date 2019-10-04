package com.back4app.quickstartexampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MainActivity extends AppCompatActivity {

    Switch rideOrDriverSwitch;

    public void getStarted(View view){

        String riderOrDriver ="rider";

        if(rideOrDriverSwitch.isChecked()){
            riderOrDriver = "driver";
        }

        ParseUser.getCurrentUser().put("riderOrDriver", riderOrDriver);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Log.i("AppInfo", "Logged in");
                    redirectUser();
                }
            }
        });

    }

    public void redirectUser(){
        if(ParseUser.getCurrentUser().get("riderOrDriver").equals("rider")){
            Intent i = new Intent(getApplicationContext(), YourLoaction.class);
            startActivity(i);
        } else {
            Intent i = new Intent(getApplicationContext(), ViewRequests.class);
            startActivity(i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseUser.getCurrentUser().put("riderOrDriver", "driver");
        rideOrDriverSwitch = findViewById(R.id.rideOrDriverSwitch);

        // Save the current Installation to Back4App
        ParseInstallation.getCurrentInstallation().saveInBackground();

        if(ParseUser.getCurrentUser() == null){

            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(e != null){
                       Log.i("MyApp", "Anonymous Login failed");
                    } else {
                        Log.i("MyApp", "Anonymous Login success");
                    }
                }
            });

        } else {
            if(ParseUser.getCurrentUser().get("riderOrDriver") != null){
                Log.i("AppInfo", "Null User");
                redirectUser();
            }
            }
        }
    }

