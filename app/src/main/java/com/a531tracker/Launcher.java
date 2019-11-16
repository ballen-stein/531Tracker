package com.a531tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.a531tracker.Database.DatabaseHelper;

import java.util.ArrayList;

import static com.a531tracker.HomeScreen.compoundLifts;

public class Launcher extends AppCompatActivity {
    final int SET_TRAINING_MAX_CODE = 1;
    private boolean cycleStarted;
    private boolean settingsCreated;

    private ArrayList<Integer> liftMaxes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);


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
        if(!settingsCreated)
            createAlert();
        else
            launchApp();
    }

    private void createAlert(){
        ErrorAlerts newAlert = new ErrorAlerts(this);
        newAlert.setErrorAlertsValues(false, false, "New Feature: Settings", "A new feature has been added: Settings. You are now able to customize your program with " +
                "more Boring But Big percents, Joker Sets, First Sets Last, Swap the accessory lifts or remove them entirely! \n\nAdditionally, there are a few 5/3/1 related settings, such as the 8/6/3 split " +
                "and 7 week cycle that can be enabled. \n\nDefault settings that are enabled are: 7 Week Cycle and Deload Week. You can find out more about all these options by pressing the icon next to the home button and clicking 'ALL Lift Settings'.\n\n" +
                "If you've reset your values at all and are NOT on the first cycle, you will need to press the DELETE ALL DATA button first to properly utilize the As Many Reps As Possible graphs.", "", false);
        newAlert.blankAlert(this).setPositiveButton(getString(R.string.ok_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                launchApp();
            }
        }).show();
    }

    private void launchApp(){
        Intent intent = new Intent(this, HomeScreen.class);
        startActivity(intent);
        finish();
    }
}
