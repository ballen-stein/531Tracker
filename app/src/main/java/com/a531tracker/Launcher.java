package com.a531tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.a531tracker.Database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Objects;

import static com.a531tracker.HomeScreen.compoundLifts;

public class Launcher extends AppCompatActivity {
    final int SET_TRAINING_MAX_CODE = 1;
    private boolean dev;
    private boolean cycleStarted;
    private boolean settingsCreated;

    private ArrayList<Integer> liftMaxes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);

        dev = false;

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
        startDatabaseChecks();
    }


    private void startDatabaseChecks(){
        DatabaseHelper db = new DatabaseHelper(this);
        // Do we have to input values into User Settings? True = Yes, False = No
        settingsCreated = checkUserSettings(db);
        cycleStarted = startCycle(db);
        Log.d("CycleCheck", settingsCreated+"");
        Log.d("CycleCheck", cycleStarted+"");
        if(dev)
            continueToLaunch();
        else
            if(checkForLiftValues())
                continueToLaunch();
    }


    private boolean checkUserSettings(DatabaseHelper db){
        return db.createUserSettings();
    }


    private boolean startCycle(DatabaseHelper db) {
        try{
            return db.startCycle();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    private boolean checkForLiftValues() {
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        try{
            for(String lifts : compoundLifts){
                liftMaxes.add(db.getLifts(lifts).getTraining_max());
            }
            return (liftMaxes.size() == 4);
        } catch (Exception e){
            e.printStackTrace();
            ErrorAlerts errorAlerts =  new ErrorAlerts(this);
            errorAlerts.setErrorAlertsValues(false, false, getResources().getString(R.string.alert_no_lifts_found), getResources().getString(R.string.alert_no_lifts_message), "", false);
            errorAlerts.blankAlert(this).setPositiveButton(getResources().getString(R.string.ok_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    freshLiftsLaunch();
                }
            }).show();
            return false;
        }
    }


    private void freshLiftsLaunch() {
        Intent intent = new Intent(getApplicationContext(), SetMaxes.class);
        intent.putExtra("NewLifts", true);
        intent.putExtra("FirstLaunch", true);
        startActivityForResult(intent, SET_TRAINING_MAX_CODE);
        finish();
    }


    private void continueToLaunch(){
        Intent intent = new Intent(this, HomeScreen.class);
        startActivity(intent);
        finish();
    }
}
