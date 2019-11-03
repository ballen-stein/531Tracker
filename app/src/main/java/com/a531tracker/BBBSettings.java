package com.a531tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import com.a531tracker.Database.DatabaseHelper;
import com.a531tracker.LiftBuilders.CompoundLifts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.a531tracker.HomeScreen.compoundLifts;

public class BBBSettings extends AppCompatActivity {

    private Button submitWeek;
    private Button submitOptions;
    private Button submitPercents;
    private Button homeButton;
    private Button settingsButton;
    private Button returnButton;

    private RadioGroup weekGroup;
    private RadioGroup bbbGroup;

    private CheckBox bbbEight;
    private CheckBox bbbFSL;
    private CheckBox bbbJoker;
    private CheckBox bbbDeload;
    private CheckBox bbbSwaps;

    private List<Integer> radioBbbChoices;

    private UserSettings userSettings = new UserSettings();
    private UserSettings newSettings = new UserSettings();

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bbb_settings_view);

        db = new DatabaseHelper(this);

        radioBbbChoices = new ArrayList<>();
        Collections.addAll(radioBbbChoices, R.id.radio_bbb_40, R.id.radio_bbb_45, R.id.radio_bbb_50, R.id.radio_bbb_55, R.id.radio_bbb_60, R.id.radio_bbb_65, R.id.radio_bbb_70, R.id.radio_bbb_75);

        setButtons();
        setListeners();
        setViews();
    }

    @Override
    protected void onStart(){
        super.onStart();
        getCurrentSettings();
        setSettingViews();
    }


    private void getCurrentSettings(){
        userSettings = db.getUserSettings();
    }


    private void setSettingViews(){
        weekGroup.check(setChosenWeek(userSettings.getWeekFormat()));
        bbbGroup.check(setChosenBbbPercent(String.valueOf(userSettings.getChosenBBBPercent())));
        setOptions(userSettings.getChosenBBBFormat());
        if(userSettings.getSwapBBBFormat() == 1)
            bbbSwaps.setChecked(true);
    }


    private int setChosenWeek(int week){
        int weekVal;
        if(week == 1){
            weekVal = R.id.radio_standard;
        } else {
            weekVal = R.id.radio_seven;
        }
        return weekVal;
    }


    private int setChosenBbbPercent(String bbbPercent){
        Log.d("PercentVal", bbbPercent);
        int bbbVal;
        switch (bbbPercent){
            case "0.4":
            case "0.40":
                bbbVal = radioBbbChoices.get(0);
                break;
            case "0.45":
                bbbVal = radioBbbChoices.get(1);
                break;
            case "0.5":
            case "0.50":
                bbbVal = radioBbbChoices.get(2);
                break;
            case "0.55":
                bbbVal = radioBbbChoices.get(3);
                break;
            case "0.6":
            case "0.60":
                bbbVal = radioBbbChoices.get(4);
                break;
            case "0.65":
                bbbVal = radioBbbChoices.get(5);
                break;
            case "0.7":
            case "0.70":
                bbbVal = radioBbbChoices.get(6);
                break;
            case "0.75":
                bbbVal = radioBbbChoices.get(7);
                break;
            default:
                bbbVal = 0;
                break;
        }
        return bbbVal;
    }


    private void setOptions(int chosenBBBFormat) {
        String bbbLength = String.valueOf(chosenBBBFormat);
        for(int i = 1; i < bbbLength.length(); i++){
            checkBoxes(bbbLength.substring(i));
        }
    }

    private void checkBoxes(String s) {
        Log.d("StringVal", s);
        switch(s){
            case "1111":
            case "1101":
            case "1011":
            case "1001":
            case "1000":
                bbbDeload.setChecked(true);
                break;
            case "111":
            case "101":
            case "100":
                bbbJoker.setChecked(true);
                break;
            case "11":
            case "10":
                bbbFSL.setChecked(true);
                break;
            case "1":
                bbbEight.setChecked(true);
                break;
            default:
                break;
        }
    }


    private void setViews(){
        weekGroup = findViewById(R.id.radio_cycle_type);
        bbbGroup = findViewById(R.id.bbb_radio_group);

        bbbEight = findViewById(R.id.bbb_eight_split);
        bbbFSL = findViewById(R.id.bbb_fsl);
        bbbJoker = findViewById(R.id.bbb_joker);
        bbbDeload = findViewById(R.id.bbb_deload);
        bbbSwaps = findViewById(R.id.bbb_swaps);
    }


    private void setButtons(){
        submitWeek = findViewById(R.id.submit_week_options);
        submitOptions = findViewById(R.id.submit_bbb_options);
        submitPercents = findViewById(R.id.submit_bbb_percent);
        navButtons();
    }


    private void navButtons(){
        homeButton = findViewById(R.id.home_button);
        settingsButton = findViewById(R.id.settings_button);
        returnButton = findViewById(R.id.upload_button);
    }


    private void setListeners(){
        submitWeekOptions();
        submitOptionsBBB();
        submitPercentsBBB();
        setNav();
    }


    private void setNav(){
        navHome();
        navSettings();
        navReturn();
    }


    private void navHome(){
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }


    private void navSettings(){
        settingsButton.setEnabled(false);
    }


    private void navReturn(){

    }


    private void submitWeekOptions(){
        submitWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCycleFormat();
            }
        });
    }


    private void updateCycleFormat(){
        String selectedFormat;
        if(weekGroup.getCheckedRadioButtonId() == R.id.radio_standard){
            newSettings.setWeekFormat(1);
            selectedFormat = "4 week cycle.";
        } else {
            newSettings.setWeekFormat(0);
            selectedFormat = "7 Week cycle.";
        }
        Log.d("UserSettings", newSettings.getWeekFormat()+"\t1 = 4 Week split \t0 = 7 Week split" + "\tOld setting: " + userSettings.getWeekFormat());
        int i = db.updateWeekSettings(newSettings, userSettings.getWeekFormat());

        if(i  == 1){
            clearSettings();
            getCurrentSettings();
            ErrorAlerts errorAlerts = new ErrorAlerts(this);
            errorAlerts.setErrorAlertsValues(
                    false,
                    true,
                    getResources().getString(R.string.settings_success_title),
                    "You've changed your cycle format to the " + selectedFormat,
                    "",
                    false
            );
            errorAlerts.preformattedAlert(this).show();
        } else {
            ErrorAlerts errorAlerts = new ErrorAlerts(this);
            errorAlerts.setErrorAlertsValues(
                    false,
                    true,
                    getResources().getString(R.string.settings_fail_title),
                    "Failed to update the cycle format; try again later.\n\nIf this issue persists, please contact the developer at 531developer@gmail.com.",
                    "",
                    false)
            ;
            errorAlerts.preformattedAlert(this).show();
        }
    }

    private void submitOptionsBBB(){
        submitOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBBBOptions();
            }
        });
    }


    private void updateBBBOptions(){
        newSettings.setSwapBBBFormat(swapCheck());
        newSettings.setChosenBBBFormat(getCheckboxValues());
        String message = "";
        if(changeSwap(newSettings.getSwapBBBFormat(), userSettings.getSwapBBBFormat())==1){
            if(changeFormat(newSettings, userSettings.getChosenBBBFormat()) == 1){
                String createText = "";
                if(bbbEight.isChecked())
                    createText += "Eight-Six-Three Split, ";
                if(bbbFSL.isChecked())
                    createText += "FSL, ";
                if(bbbJoker.isChecked())
                    createText += "Joker, ";
                if(bbbDeload.isChecked())
                    createText += "BBB During Deload, ";
                if(bbbSwaps.isChecked())
                    createText += "Swapped BBBs";
                clearSettings();
                getCurrentSettings();
                message = "You've changed your Boring But Big format to include the following: " + createText + ".";
                newAlert(true, message);
            } else {
                newAlert(false, message);
            }
        } else {
            message = getResources().getString(R.string.settings_fail_message);
            newAlert(false, message);
        }
    }


    private int swapCheck(){
        if(bbbSwaps.isChecked())
            return 1;
        else
            return 0;
    }


    private int getCheckboxValues(){
        int i = 900000;
        if(bbbEight.isChecked())
            i+=1;
        if(bbbFSL.isChecked())
            i+=10;
        if(bbbJoker.isChecked())
            i+=100;
        if(bbbDeload.isChecked())
            i+=1000;

        return i;
    }


    private int changeSwap(int newSwap, int oldSwap){
        return db.swapBBBWorkouts(oldSwap, newSwap);
    }


    private int changeFormat(UserSettings userSettings, int oldVal){
        return db.updateBbbFormat(userSettings, oldVal);
    }


    private void submitPercentsBBB(){
        submitPercents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBBBPercents(checkBBBPercent(bbbGroup.getCheckedRadioButtonId()));
            }
        });
    }


    private float checkBBBPercent(int id){
        float bbbVal = 0.40f;
        for(int i = 0; i < radioBbbChoices.size(); i++){
            if(id == radioBbbChoices.get(i)){
                break;
            } else {
                bbbVal += 0.05f;
            }
        }
        BigDecimal roundingVal = new BigDecimal(Float.toString(bbbVal)).setScale(2, BigDecimal.ROUND_DOWN);
        return roundingVal.floatValue();
    }


    private void updateBBBPercents(float bbbVal){
        boolean successful = false;
        for(String lifts : compoundLifts){
            CompoundLifts newLifts = new CompoundLifts();
            newLifts.setBig_but_boring_weight(bbbVal);
            newLifts.setCompound_movement(lifts);
            int i = db.updateBBBPercent(newLifts);
            if(i == 1){
                successful = true;
            }
        }
        String message;
        if(successful){
            clearSettings();
            getCurrentSettings();

            message = "You've changed your Boring But Big percents to " + bbbVal*100 + "%.";
            newAlert(true, message);
        } else {
            message = getResources().getString(R.string.settings_fail_message);
            newAlert(false, message);
        }
    }


    private void clearSettings(){
        userSettings = new UserSettings();
    }


    private void newAlert(boolean success, String message){
        String title;
        if(success)
            title = getResources().getString(R.string.settings_success_title);
        else
            title = getResources().getString(R.string.settings_fail_title);

        ErrorAlerts errorAlerts = new ErrorAlerts(this);
        errorAlerts.setErrorAlertsValues(
                false,
                true,
                title,
                message,
                "",
                false)
        ;
        errorAlerts.preformattedAlert(this).show();
    }
}
