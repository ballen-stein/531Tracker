package com.a531tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.a531tracker.Database.DatabaseHelper;

import java.util.Objects;

public class Launcher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);
        try{
            Intent intent = getIntent();
            if(Objects.requireNonNull(intent.getStringExtra("Deletion")).equals("true")){
                // TODO Add functions to be used when Delete All Data is called
            }
        } catch (Exception e){
            Log.d("Launcher", "FAILED");
            e.getMessage();
        }

    }

    @Override
    protected void onStart(){
        super.onStart();
        DatabaseHelper db = new DatabaseHelper(this);
        //If user settings doesn't exist...
        if(!db.createUserSettings()){
            Log.d("UserSettings", "Input default user settings");
            continueToLaunch();
        //If user settings do exist...
        } else {
            Log.d("UserSettings", "Found user settings, continuing to launch...");
            continueToLaunch();
        }
    }

    private void continueToLaunch(){
        Intent intent = new Intent(this, HomeScreen.class);
        startActivity(intent);
        finish();
    }
}
