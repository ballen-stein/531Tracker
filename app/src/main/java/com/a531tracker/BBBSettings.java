package com.a531tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.a531tracker.Database.DatabaseHelper;
import com.a531tracker.DetailFragments.InformationFragment;
import com.a531tracker.ObjectBuilders.CompoundLifts;
import com.a531tracker.ObjectBuilders.UserSettings;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.a531tracker.HomeScreen.compoundLifts;

public class BBBSettings extends AppCompatActivity implements InformationFragment.InformationFragmentListener{

    private InformationFragment informationFragment;

    private Button submitWeek;
    private Button submitOptions;
    private Button submitPercents;
    private Button homeButton;
    private Button settingsButton;

    private LinearLayout bbbWeekInfoButton;
    private LinearLayout bbbPercentInfoButton;
    private LinearLayout bbbFormatInfoButton;

    private RadioGroup weekGroup;
    private RadioGroup bbbGroup;

    private CheckBox bbbEight;
    private CheckBox bbbFSL;
    private CheckBox bbbJoker;
    private CheckBox bbbDeload;
    private CheckBox bbbSwaps;
    private CheckBox bbbRemove;

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
        Collections.addAll(radioBbbChoices, R.id.radio_bbb_30, R.id.radio_bbb_35, R.id.radio_bbb_40, R.id.radio_bbb_45, R.id.radio_bbb_50,
                R.id.radio_bbb_55, R.id.radio_bbb_60, R.id.radio_bbb_65, R.id.radio_bbb_70, R.id.radio_bbb_75, R.id.radio_bbb_80, R.id.radio_bbb_85, R.id.radio_bbb_90);

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
        int bbbVal;
        switch (bbbPercent){
            case "0.30":
            case "0.3":
                bbbVal = radioBbbChoices.get(0);
                break;
            case "0.35":
                bbbVal = radioBbbChoices.get(1);
                break;
            case "0.4":
            case "0.40":
                bbbVal = radioBbbChoices.get(2);
                break;
            case "0.45":
                bbbVal = radioBbbChoices.get(3);
                break;
            case "0.5":
            case "0.50":
                bbbVal = radioBbbChoices.get(4);
                break;
            case "0.55":
                bbbVal = radioBbbChoices.get(5);
                break;
            case "0.6":
            case "0.60":
                bbbVal = radioBbbChoices.get(6);
                break;
            case "0.65":
                bbbVal = radioBbbChoices.get(7);
                break;
            case "0.7":
            case "0.70":
                bbbVal = radioBbbChoices.get(8);
                break;
            case "0.75":
                bbbVal = radioBbbChoices.get(9);
                break;
            case "0.80":
            case "0.8":
                bbbVal = radioBbbChoices.get(10);
                break;
            case "0.85":
                bbbVal = radioBbbChoices.get(11);
                break;
            case "0.90":
            case "0.9":
                bbbVal = radioBbbChoices.get(12);
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
            checkSettings(bbbLength.charAt(i), i);
        }
    }


    private void checkSettings(char c, int i){
        switch(i){
            case 1:
                if(c == '1') {
                    bbbRemove.setChecked(true);
                    break;
                }
            case 2:
                if(c == '1') {
                    bbbDeload.setChecked(true);
                    break;
                }
            case 3:
                if(c == '1') {
                    bbbJoker.setChecked(true);
                    break;
                }
            case 4:
                if(c == '1') {
                    bbbFSL.setChecked(true);
                    break;
                }
            case 5:
                if(c == '1') {
                    bbbEight.setChecked(true);
                    break;
                }
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
        bbbRemove = findViewById(R.id.bbb_remove);
    }


    private void setButtons(){
        submitWeek = findViewById(R.id.submit_week_options);
        submitOptions = findViewById(R.id.submit_bbb_options);
        submitPercents = findViewById(R.id.submit_bbb_percent);

        bbbWeekInfoButton = findViewById(R.id.week_options_information);
        bbbFormatInfoButton = findViewById(R.id.bbb_options_information);
        bbbPercentInfoButton = findViewById(R.id.bbb_percent_information);
        navButtons();
    }


    private void navButtons(){
        homeButton = findViewById(R.id.home_button);
        settingsButton = findViewById(R.id.settings_button);
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
                    createText += "Swapped BBBs, ";
                clearSettings();
                getCurrentSettings();
                if(createText.length() > 0)
                    message = "You've changed your Boring But Big format to include the following: " + createText.substring(0, createText.length()-2) + ".";
                else
                    message = "You've removed all Boring But Big options.";
                if(bbbRemove.isChecked())
                    message += "\n\nYou've selected to remove the Boring But Big option. All prior BBB settings will be ignored.";
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
        if(bbbRemove.isChecked())
            i+=10000;
        return i;
    }


    private int changeSwap(int newSwap, int oldSwap){
        return db.swapBBBWorkouts(oldSwap, newSwap);
    }


    private int changeFormat(UserSettings userSettings, int oldVal){
        return db.updateBbbFormat(userSettings, oldVal);
    }


    private float checkBBBPercent(int id){
        float bbbVal = 0.30f;
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


    // ---------- Fragment Values ----------

    private void displayInformation(String header, String desc){
        if(informationFragment != null){
            closeInformation();
        }
        informationFragment = InformationFragment.newInstance(header, desc);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_bottom, R.anim.exit_bottom, R.anim.enter_bottom, R.anim.exit_bottom)
                .addToBackStack("@null")
                .replace(R.id.fragment_holder, informationFragment)
                .commit();
    }

    @Override
    public void closeInformation(){
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_bottom, R.anim.exit_bottom, R.anim.enter_bottom, R.anim.exit_bottom)
                .remove(informationFragment)
                .commit();
        informationFragment = null;
    }


    // ---------- Buttons and Listeners ----------

    private void setListeners(){
        submitWeekOptions();
        submitOptionsBBB();
        submitPercentsBBB();
        viewInformation();
        setNav();
    }


    private void viewInformation(){
        weekInformation();
        percentInformation();
        formatInformation();
    }


    private void setNav(){
        navHome();
        navSettings();
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
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
    }


    private void submitWeekOptions(){
        submitWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCycleFormat();
            }
        });
    }


    private void submitPercentsBBB(){
        submitPercents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBBBPercents(checkBBBPercent(bbbGroup.getCheckedRadioButtonId()));
            }
        });
    }


    private void submitOptionsBBB(){
        submitOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBBBOptions();
            }
        });
    }


    private void weekInformation(){
        bbbWeekInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayInformation(getString(R.string.settings_cycle_header), getString(R.string.settings_week_info));
            }
        });
    }


    private void formatInformation(){
        bbbFormatInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayInformation(getResources().getString(R.string.settings_bbb_options), getString(R.string.settings_option_info));
            }
        });
    }


    private void percentInformation(){
        bbbPercentInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayInformation(getString(R.string.settings_bbb_percents), getString(R.string.settings_percent_info));
            }
        });
    }


    // ---------- Misc. ----------

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
