package com.a531tracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.a531tracker.Database.DatabaseHelper;
import com.a531tracker.LiftBuilders.AsManyRepsAsPossible;
import com.a531tracker.LiftBuilders.CompoundLifts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.a531tracker.HomeScreen.compoundLifts;

public class UpdateValues extends AppCompatActivity {
    private Button updateAll;
    private Button homeButton;
    private Button settingsButton;
    private Button uploadButton;

    private TextView benchCurrent;
    private TextView squatCurrent;
    private TextView deadliftCurrent;
    private TextView pressCurrent;


    private DatabaseHelper db;

    private ArrayList<CompoundLifts> liftValues = new ArrayList<>();
    private Map<String, Button> disableButtons = new HashMap<>();
    private TextView[] currentViews;
    private Spinner[] spinnerArray;

    private int cycleValue;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_cycle);

        db = new DatabaseHelper(this);

        cycleValue = db.getCycle();

        startupFunctions();
    }


    public void startupFunctions(){
        setButtons();
        setListeners();
        setTextViews();
        getCurrentLiftValues();
        displayLiftViews();
        navCheck();
        //buttonDisables();
    }

    private void setTextViews(){
        benchCurrent = findViewById(R.id.bench_tm_current_value);
        squatCurrent = findViewById(R.id.squat_tm_current_value);
        deadliftCurrent = findViewById(R.id.deadlift_tm_current_value);
        pressCurrent = findViewById(R.id.press_tm_current_value);

        Spinner benchUpdateSpinner = findViewById(R.id.tm_bench_update_spinner);
        Spinner pressUpdateSpinner = findViewById(R.id.tm_press_update_spinner);
        Spinner squatUpdateSpinner = findViewById(R.id.tm_squat_update_spinner);
        Spinner deadliftUpdateSpinner = findViewById(R.id.tm_deadlift_update_spinner);

        currentViews = new TextView[]{benchCurrent, pressCurrent, squatCurrent, deadliftCurrent};
        spinnerArray = new Spinner[]{benchUpdateSpinner, pressUpdateSpinner, squatUpdateSpinner, deadliftUpdateSpinner};
    }


    private void getCurrentLiftValues(){
        for(String lift : compoundLifts){
            liftValues.add(db.getLifts(lift));
        }

        for(int i=0; i < liftValues.size(); i++){
            System.out.println(liftValues.get(i).getCompound_movement());
            System.out.println(liftValues.get(i).getTraining_max());
        }
    }

    private void displayLiftViews() {
        int updateVal = 5;
        for(int i=0; i < liftValues.size(); i++){
            if(i>=2)
                updateVal = 10;
            currentViews[i].setText(String.valueOf(liftValues.get(i).getTraining_max()));
            setSpinnerValues(spinnerArray[i], liftValues.get(i), updateVal);
        }
    }


    private void setSpinnerValues(Spinner currentSpinner, CompoundLifts lifts, int updateVal){
        Integer[] spinnerValues = new Integer[]{lifts.getTraining_max(), lifts.getTraining_max()+updateVal};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_resource, spinnerValues);
        adapter.setDropDownViewResource(R.layout.spinner_item_resource);

        currentSpinner.setAdapter(adapter);
        currentSpinner.setSelection(1);
        currentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) view).setTextColor(getColor(R.color.colorRed));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void buttonDisables(){
        for(int i=0; i < compoundLifts.length; i++){
            if(checkAMRAP(compoundLifts[i], i)){
                Button tempBtn = disableButtons.get(compoundLifts[i]);
                Objects.requireNonNull(tempBtn).setEnabled(false);
                tempBtn.setAlpha(0.5f);
            }
        }

    }

    private boolean checkAMRAP(String lift, final int i){
        try{
            int amrapVal = db.getAMRAPValues(lift, cycleValue).getTotalMaxWeight();
                return!(amrapVal == liftValues.get(i).getTraining_max());
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    private boolean checkValues(){
        String errorLift = "";
        try{
            ArrayList<AsManyRepsAsPossible> newAmrapLifts = new ArrayList<>();
            for(String liftName : compoundLifts) {
                errorLift = liftName;
                newAmrapLifts.add(db.getAMRAPValues(liftName, cycleValue));
            }
            if(newAmrapLifts.size() == 4) {
                try {
                    db.updateCycle(cycleValue);
                    cycleValue = db.getCycle();
                    Log.d("Everything work", "Everything worked");
                    for(String lifts: compoundLifts) {
                        db.createAMRAPTable(cycleValue, lifts);
                        addToTotalMax(lifts);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                Log.d("Error_For_Cycle", "From else");
                String message = getResources().getString(R.string.amrap_error_message);
                ErrorAlerts errorAlerts = new ErrorAlerts(this);
                String title = getString(R.string.amrap_error_title);
                errorAlerts.setErrorAlertsValues(false, true, "Error from Else", message, errorLift, true);
                errorAlerts.createDialogAlert(this).show();
                return false;
            }
        } catch (Exception e){
            String message = getResources().getString(R.string.amrap_error_message);
            String title = getString(R.string.amrap_error_title);
            ErrorAlerts errorAlerts = new ErrorAlerts(this);
            errorAlerts.setErrorAlertsValues(false, true, title, message, errorLift, true);
            errorAlerts.createDialogAlert(this).show();
            return false;
        }
    }


    private void addToTotalMax(String compoundName){
        Spinner spinner;
        switch(compoundName){
            case "Deadlift":
                spinner = findViewById(R.id.tm_deadlift_update_spinner);
                break;
            case "Overhand Press":
                spinner = findViewById(R.id.tm_press_update_spinner);
                break;
            case "Squat":
                spinner = findViewById(R.id.tm_squat_update_spinner);
                break;
            default:
            case "Bench":
                spinner = findViewById(R.id.tm_bench_update_spinner);
                break;
        }

        int newMax = Integer.valueOf(spinner.getSelectedItem().toString());

        CompoundLifts newLifts = new CompoundLifts();
        newLifts.setCompound_movement(compoundName);
        newLifts.setTraining_max(newMax);
        Log.d("CompoundValues", compoundName + " is adding " + newMax);
        if(db.updateCompoundStats(newLifts) == 1)
            Log.d("Lifts", "Lift "  + compoundName + " updated!");
    }


    private void cycleCheck(){
        ArrayList<AsManyRepsAsPossible> newAmrapLifts = new ArrayList<>();
        try {
            for(String compoundName: compoundLifts)
                newAmrapLifts.add(db.getAMRAPValues(compoundName, cycleValue));

            if(newAmrapLifts.size() == 4){
                db.updateCycle(cycleValue);
            }
        } catch (Exception e){
            Log.d("CycleCheckFailed", e.getMessage() + "\n Failed due to missing reps");
            Log.d("CycleCheckFailed", "Current cycle: " + cycleValue);
        }

    }

    // Buttons and Listeners
    private void setListeners(){
        submitAll();
    }


    private void setButtons(){
        updateAll = findViewById(R.id.tm_update_all_button);

        homeButton = findViewById(R.id.home_button);
        settingsButton = findViewById(R.id.settings_button);
        uploadButton = findViewById(R.id.upload_button);
    }


    private void submitAll(){
        updateAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkValues()){
                    Intent returnIntent = getIntent();
                    setResult(RESULT_OK);
                    finish();
                }
                //for(String lifts : compoundLifts)
                //    checkValues(lifts);

                //db.updateCycle(cycleValue);
            }
        });
    }


    private void navCheck(){
        homeNav();
        settingsNav();
        backNav();
    }

    public void homeNav(){
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }


    public void settingsNav(){
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navSettings();
            }
        });
    }

    public void navSettings(){
        Toast.makeText(getApplicationContext(), "Settings pressed", Toast.LENGTH_LONG).show();
    }


    public void backNav(){
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navBack();
            }
        });
    }


    public void navBack(){
        Toast.makeText(getApplicationContext(), "Upload pressed", Toast.LENGTH_LONG).show();
    }
}
