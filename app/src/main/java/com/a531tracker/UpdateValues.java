package com.a531tracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.a531tracker.database.DatabaseHelper;
import com.a531tracker.ObjectBuilders.AsManyRepsAsPossible;
import com.a531tracker.ObjectBuilders.CompoundLifts;

import java.math.BigDecimal;
import java.util.ArrayList;

import static com.a531tracker.HomeScreen.compoundLifts;

public class UpdateValues extends AppCompatActivity {
    private Button updateAll;
    private Button homeButton;
    private Button settingsButton;

    private TextView currentCycle;
    private TextView updateCycle;
    private Context mContext;

    private DatabaseHelper db;
    private CalculateWeight calculateWeight = new CalculateWeight();

    private ArrayList<CompoundLifts> liftValues = new ArrayList<>();
    private TextView[] currentViews;
    private Spinner[] spinnerArray;

    private int cycleValue;
    private int weightCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_cycle);

        db = new DatabaseHelper(this);
        cycleValue = db.getCycle();
        mContext = this;

        startupFunctions();
    }


    public void startupFunctions(){
        getSettings();
        setButtons();
        setListeners();
        setTextViews();
        getCurrentLiftValues();
        displayCycleValues();
        displayLiftViews();
        navCheck();
    }


    private void getSettings(){
        weightCheck = Integer.parseInt(String.valueOf(String.valueOf(db.getUserSettings().getChosenBBBFormat()).charAt(0)));
    }


    private void setTextViews(){
        TextView benchCurrent = findViewById(R.id.bench_tm_current_value);
        TextView squatCurrent = findViewById(R.id.squat_tm_current_value);
        TextView deadliftCurrent = findViewById(R.id.deadlift_tm_current_value);
        TextView pressCurrent = findViewById(R.id.press_tm_current_value);

        currentCycle = findViewById(R.id.current_cycle_number);
        updateCycle = findViewById(R.id.update_cycle_number);

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


    private void displayCycleValues(){
        String text = "Cycle ";
        String currentText = text + cycleValue;
        String updateText = text + (cycleValue+1);
        currentCycle.setText(currentText);
        updateCycle.setText(updateText);
    }


    private void displayLiftViews() {
        float updateVal = 5;
        for(int i=0; i < liftValues.size(); i++){

            if(weightCheck == 9) {
                if (i >= 2)
                    updateVal = 10;
                currentViews[i].setText(String.valueOf(liftValues.get(i).getTraining_max()));
                setSpinnerValues(spinnerArray[i], liftValues.get(i).getTraining_max(), updateVal);
            } else {
                updateVal = Float.parseFloat(calculateWeight.setAsKilograms(5, 1f));
                if (i >= 2)
                    updateVal = Float.parseFloat(calculateWeight.setAsKilograms(10, 1f));
                float passedValAsKg = Float.parseFloat(calculateWeight.setAsKilograms(liftValues.get(i).getTraining_max(), 1f));
                currentViews[i].setText(String.valueOf(passedValAsKg));
                setSpinnerValues(spinnerArray[i], passedValAsKg, updateVal);
            }
        }
    }


    private void setSpinnerValues(Spinner currentSpinner, float lifts, float updateVal){
        String[] spinnerValues;

        if(weightCheck == 9) {
            spinnerValues = new String[]{String.valueOf(lifts).substring(0, String.valueOf(lifts).length()-2), String.valueOf(lifts+updateVal).substring(0, String.valueOf(lifts).length()-2)};
        } else {
            float updatedValue = (lifts+updateVal);
            BigDecimal bd = new BigDecimal(updatedValue).setScale(1, BigDecimal.ROUND_HALF_DOWN);
            spinnerValues = new String[]{String.valueOf(lifts), String.valueOf(bd)};
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_resource, spinnerValues);
        adapter.setDropDownViewResource(R.layout.spinner_item_resource);

        currentSpinner.setAdapter(adapter);
        currentSpinner.setSelection(1);
        currentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) view).setTextColor(getColor(R.color.colorPrimary));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
                    for(int i = 0; i < compoundLifts.length; i++) {
                        db.createAMRAPTable(cycleValue, compoundLifts[i]);
                        addToTotalMax(compoundLifts[i], newAmrapLifts.get(i));
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                String message = getResources().getString(R.string.amrap_error_message);
                ErrorAlerts errorAlerts = new ErrorAlerts(this);
                String title = getString(R.string.amrap_error_title);
                errorAlerts.setErrorAlertsValues(false, true, title, message, errorLift, true);
                errorAlerts.preformattedAlert(this).show();
                return false;
            }
        } catch (Exception e){
            String message = getResources().getString(R.string.amrap_error_message);
            String title = getString(R.string.amrap_error_title);
            ErrorAlerts errorAlerts = new ErrorAlerts(this);
            errorAlerts.setErrorAlertsValues(false, true, title, message, errorLift, true);
            errorAlerts.preformattedAlert(this).show();
            return false;
        }
    }


    private void addToTotalMax(String compoundName, AsManyRepsAsPossible amrap){
        Spinner spinner;
        int updateVal = 5;
        switch(compoundName){
            case "Deadlift":
                spinner = findViewById(R.id.tm_deadlift_update_spinner);
                updateVal = 10;
                break;
            case "Overhand Press":
                spinner = findViewById(R.id.tm_press_update_spinner);
                break;
            case "Squat":
                spinner = findViewById(R.id.tm_squat_update_spinner);
                updateVal = 10;
                break;
            default:
            case "Bench":
                spinner = findViewById(R.id.tm_bench_update_spinner);
                break;
        }
        int newMax;

        if(spinner.getSelectedItemPosition() == 1){
            newMax = amrap.getTotalMaxWeight() + updateVal;
        } else {
            newMax = amrap.getTotalMaxWeight();
        }

        CompoundLifts newLifts = new CompoundLifts();
        newLifts.setCompound_movement(compoundName);
        newLifts.setTraining_max(newMax);
        db.updateCompoundStats(newLifts);
    }


    // Buttons and Listeners
    private void setListeners(){
        submitAll();
    }


    private void setButtons(){
        updateAll = findViewById(R.id.tm_update_all_button);
        homeButton = findViewById(R.id.home_button);
        settingsButton = findViewById(R.id.settings_button);
    }


    private void submitAll(){
        updateAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkValues()){
                    finishValueSubmission();
                }
            }
        });
    }


    private void finishValueSubmission(){
        ErrorAlerts successAlert = new ErrorAlerts(this);
        successAlert.setErrorAlertsValues(false, false, getString(R.string.tm_update_title), getString(R.string.tm_update_message), "", false);
        successAlert.blankAlert(this).setPositiveButton(getString(R.string.ok_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setResult(RESULT_OK);
                finish();
            }
        }).show();
    }


    private void navCheck(){
        homeNav();
        settingsNav();
    }


    public void homeNav(){
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, HomeScreen.class);
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
        Intent intent = new Intent(mContext, Settings.class);
        startActivity(intent);
    }
}
