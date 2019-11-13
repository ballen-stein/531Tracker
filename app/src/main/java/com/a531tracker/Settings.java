package com.a531tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.a531tracker.Database.DatabaseHelper;
import com.a531tracker.ObjectBuilders.AsManyRepsAsPossible;
import com.a531tracker.ObjectBuilders.CompoundLifts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.a531tracker.HomeScreen.UPDATE_TRAINING_MAX_CODE;
import static com.a531tracker.HomeScreen.compoundLifts;

public class Settings extends AppCompatActivity {
    private Context mContext;

    private LinearLayout resetLiftsSettings;
    private LinearLayout bbbSettings;
    private LinearLayout amrapSettings;
    private LinearLayout deleteAllData;

    private TableLayout liftTable;

    private TextView benchNums;
    private TextView pressNums;
    private TextView deadliftNums;
    private TextView squatNums;

    private Button homeButton;
    private Button settingsButton;

    private DatabaseHelper db  = new DatabaseHelper(this);
    TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.3333f);
    private int cycleValue;

    private List<Integer> liftValues = new ArrayList<>();
    private List<CompoundLifts> currentValues = new ArrayList<>();
    private Map<String, Integer> liftMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        mContext = this;

        setButtons();
        setListeners();
        setViews();

    }


    @Override
    protected void onStart(){
        super.onStart();

        cycleValue = db.getCycle();
        clearAllValues();
        clearTable();
        getLiftValues();
        setTableRow(liftMap);
        setCurrentValues();
    }


    private void clearAllValues(){
        liftMap.clear();
        liftValues.clear();
    }


    private void clearTable(){
        while(liftTable.getChildCount() > 1)
            liftTable.removeView(liftTable.getChildAt(liftTable.getChildCount()-1));

    }

    private void getLiftValues() {
        int i = 0;
        for(String lift : compoundLifts){
            currentValues.add(db.getLifts(lift));
            int trainingMax = currentValues.get(i).getTraining_max();
            liftMap.put(lift, trainingMax);
            liftValues.add(trainingMax);
            i++;
        }
    }


    private void setTableRow(Map liftMap) {
        for(String lift : compoundLifts){
            int compoundWeight = (int)liftMap.get(lift);

            AsManyRepsAsPossible amrap = db.checkForMissing(lift, cycleValue, compoundWeight);

            Log.d("LiftValue", "AMRAP Values: " + amrap.getCompound()  + "\n" +  (amrap.getTotalMaxWeight()) + "\n" + amrap.getNinety_five_reps()  + "\n" + amrap.getNinety_reps() + "\n" + amrap.getEighty_five_reps());
            liftTable.addView(createTableRow(amrap));
        }
    }


    private TableRow createTableRow(AsManyRepsAsPossible amrap) {
        TextView liftName = createView(amrap.getCompound());

        TableRow tableRow = new TableRow(mContext);

        String submittedValues = "", missingValues = "";
        if(amrap.getEighty_five_reps() >= 0)
            submittedValues += "85% - " + amrap.getEighty_five_reps() + " reps";
        else
            missingValues += "85%";

        if(amrap.getNinety_reps() >= 0)
            submittedValues += "\n90% - " + amrap.getNinety_reps()  + " reps";
        else
            missingValues += "\n90%";

        if(amrap.getNinety_five_reps() >= 0)
            submittedValues += "\n95% - " + amrap.getNinety_five_reps()  + " reps";
        else
            missingValues += "\n 95%";

        TextView submittedView = createView(submittedValues);
        TextView missingView = createView(missingValues);

        tableRow.addView(liftName);
        tableRow.addView(submittedView);
        tableRow.addView(missingView);
        tableRow.setGravity(Gravity.CENTER);
        return tableRow;
    }

    private TextView createView(String text){
        TextView tv = new TextView(mContext);
        tv.setText(text);
        tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlue));
        tv.setBackground(ResourcesCompat.getDrawable(getResources(), (R.drawable.table_boxes), null));
        int paddingVal = (int) mContext.getResources().getDimension(R.dimen.workout_frame_padding_half);
        tv.setPadding(paddingVal, paddingVal, paddingVal, paddingVal);

        //final float scale = mContext.getResources().getDisplayMetrics().density;
        //int height = (int)(70 * scale + 0.5f);
        //int width = (int)(105 * scale + 0.5f);
        //tv.setHeight(height);
        //tv.setWidth(width);
        //params.weight = 0.75f;
        tv.setLayoutParams(params);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }


    private void setViews(){
        liftTable = findViewById(R.id.lifts_table);

        benchNums = findViewById(R.id.bench_value);
        pressNums = findViewById(R.id.press_value);
        deadliftNums = findViewById(R.id.deadlift_value);
        squatNums = findViewById(R.id.squat_value);
    }


    private void setCurrentValues() {
        benchNums.setText(String.valueOf(currentValues.get(0).getTraining_max()));
        pressNums.setText(String.valueOf(currentValues.get(1).getTraining_max()));
        squatNums.setText(String.valueOf(currentValues.get(2).getTraining_max()));
        deadliftNums.setText(String.valueOf(currentValues.get(3).getTraining_max()));
    }


    private void setButtons(){
        resetLiftsSettings = findViewById(R.id.reset_settings_frame);
        bbbSettings = findViewById(R.id.lift_settings_frame);
        amrapSettings = findViewById(R.id.amrap_settings_frame);
        //Hide AMRAP Button until graphing is implemented
        if(!getResources().getBoolean(R.bool.show_amrap_numbers))
            //amrapSettings.setVisibility(View.GONE);
        deleteAllData = findViewById(R.id.delete_settings_frame);
        navButtons();
    }


    private void navButtons(){
        homeButton = findViewById(R.id.home_button);
        settingsButton = findViewById(R.id.settings_button);
    }


    private void setListeners(){
        openResetSettings();
        openBBBSettings();
        openAMRAPSettings();
        deleteAllDataOption();
        setNav();
    }


    private void setNav(){
        navHome();
        navSettings();
    }


    private void navHome(){
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, HomeScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }


    private void navSettings(){
        settingsButton.setEnabled(false);
    }


    private void openResetSettings() {
        resetLiftsSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SetMaxes.class);
                intent.putIntegerArrayListExtra("LIFT_VALUES", (ArrayList<Integer>) liftValues);
                intent.putExtra("Revision", true);
                startActivityForResult(intent, UPDATE_TRAINING_MAX_CODE);
            }
        });
    }


    private void openBBBSettings(){
        bbbSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("LiftSettingsClicked", "Go to BBB settings");
                Intent intent = new Intent(mContext, BBBSettings.class);
                startActivity(intent);
            }
        });
    }


    private void openAMRAPSettings(){
        amrapSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    private void deleteAllDataOption(){
        deleteAllData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData();
            }
        });
    }


    private void deleteData(){
        ErrorAlerts errorAlerts = new ErrorAlerts(mContext);
        String title = getResources().getString(R.string.settings_delete_title);
        errorAlerts.setErrorAlertsValues(true, true, title, getResources().getString(R.string.settings_delete_message), "OK", false);
        errorAlerts.blankAlert(mContext).setPositiveButton(mContext.getString(R.string.settings_delete_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteAllDataRequest();
            }
        }).show();
    }

    private void deleteAllDataRequest(){
        db.deleteAllData(db.getReadableDatabase());
        db.onNewUser(db.getReadableDatabase());
        Intent intent = new Intent(mContext, Launcher.class);
        intent.putExtra("Deletion", "true");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
}
