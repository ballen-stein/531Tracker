package com.a531tracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import com.a531tracker.Database.DatabaseHelper;
import com.a531tracker.ObjectBuilders.CompoundLifts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.a531tracker.HomeScreen.compoundLifts;

public class SetMaxes extends Activity {
    private int[] inputIds = new int[]{R.id.bench_input, R.id.press_input, R.id.squat_input, R.id.deadlift_input};
    private Map<String, CompoundLifts> mappedLifts = new HashMap<>();
    private ArrayList<Integer> liftValues = new ArrayList<>();

    private DatabaseHelper db = new DatabaseHelper(this);

    private Button cancelBtn;

    private float bbbPercent;
    private boolean firstLaunch;
    private boolean revision;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_maxes_view);

        setButtons();
        Intent intent = getIntent();
        /*
        if(intent.getExtras().getBoolean("Has_Lifts")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    .setTitle(R.string.no_lifts_found)
                    .setMessage(R.string.no_lifts_set_lifts)
                    .setPositiveButton(R.string.no_lifts_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) { }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            try {
                liftValues = intent.getIntegerArrayListExtra("LIFT_VALUES");
                setEditTextViews();
            } catch (Exception e) {
                Log.e("ERROR_WITH_LIFTS", e.getMessage() + "");
                Log.d("ERROR_WITH_LIFTS", "Couldn't get lifts from database");
            }
        }

         */
        try {
            firstLaunch = Objects.requireNonNull(intent.getExtras()).getBoolean("NewLifts", false);
            if(firstLaunch) {
                cancelBtn.setVisibility(View.GONE);
            } else {
                try {
                    liftValues = intent.getIntegerArrayListExtra("LIFT_VALUES");
                    revision = Objects.requireNonNull(intent.getExtras()).getBoolean("Revision", false);
                    setEditTextViews();
                } catch (Exception e) {
                    e.getMessage();
                    //e.printStackTrace();
                    Log.d("ERROR_WITH_LIFTS", "Couldn't get lifts from database");
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.d("Has Lifts", "NO LIFTS FOUND");
        }
    }


    private void setEditTextViews(){
        for(int i = 0; i < inputIds.length; i++){
            EditText setTrainingMaxesFromDB = findViewById(inputIds[i]);
            setTrainingMaxesFromDB.setText(String.valueOf(liftValues.get(i)));
        }
    }


    private void setButtons(){
        cancelBtn = findViewById(R.id.cancel);
        returnHome();
        submitLiftsButton();
    }


    private void inputLifts() {
        for(int i = 0; i < mappedLifts.size(); i++)
            db.insertCompoundStats(Objects.requireNonNull(mappedLifts.get(compoundLifts[i])));
    }


    private void startCycle(){
        if(db.startCycle())
            Log.d("CycleValue", "Fresh Cycle Started");
        else
            Log.d("CycleValue", "Old Cycle Started");
    }


    private void startAMRAP(){
        int cycle = db.getCycle();
        Log.d("CycleValue", cycle+"");
        for(String lift : compoundLifts) {
            db.createAMRAPTable(cycle, lift);
            db.createAMRAPTable(cycle - 1, lift);
            db.updateAMRAPTable(lift, cycle-1, "0.85", 5, 100);
            db.updateAMRAPTable(lift, cycle-1, "0.9", 3, 100);
            db.updateAMRAPTable(lift, cycle-1, "0.95", 1, 100);
        }
    }


    private void setLifts(){
        RadioGroup radioGroup = findViewById(R.id.radioGroupBBB);
        setBBB(radioGroup.getCheckedRadioButtonId());

        CheckBox checkBox = findViewById(R.id.training_maxes_used);
        if(checkBox.isChecked()){
            setTrainingMaxes(1.0f);
        } else {
            setTrainingMaxes(0.90f);
        }
    }


    private void setTrainingMaxes(double modifier){
        for (int i = 0; i < compoundLifts.length; i++) {
            CompoundLifts lifts = new CompoundLifts();
            lifts.setCompound_movement(compoundLifts[i]);
            EditText trainingMaxInput = findViewById(inputIds[i]);
            lifts.setTraining_max((int) (Integer.parseInt(String.valueOf(trainingMaxInput.getText()))*modifier));
            //lifts.setTraining_max((int)(((String.valueOf(trainingMaxInput.getText())) * modifier));
            lifts.setBig_but_boring_weight(bbbPercent);
            mappedLifts.put(compoundLifts[i], lifts);
        }
    }


    private void setBBB(int id){
        switch(id){
            case R.id.radio_bbb_40:
                bbbPercent = 0.40f;
                break;
            case R.id.radio_bbb_45:
                bbbPercent = 0.45f;
                break;
            default:
            case R.id.radio_bbb_50:
                bbbPercent = 0.50f;
                break;
            case R.id.radio_bbb_55:
                bbbPercent = 0.55f;
                break;
            case R.id.radio_bbb_60:
                bbbPercent = 0.60f;
                break;
            case R.id.radio_bbb_65:
                bbbPercent = 0.65f;
                break;
            case R.id.radio_bbb_70:
                bbbPercent = 0.70f;
                break;
            case R.id.radio_bbb_75:
                bbbPercent = 0.75f;
                break;
        }
    }


    // ---- Buttons ----
    private void returnHome(){
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void submitLiftsButton(){
        findViewById(R.id.submit_compounds).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.onNewUser(db.getReadableDatabase());
                setLifts();
                inputLifts();
                startCycle();
                startAMRAP();

                if(firstLaunch){
                    /*Intent returnIntent = getIntent();
                    returnIntent.putExtra("Submitted", "Values were submitted");
                    returnIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    setResult(RESULT_OK, returnIntent);
                    finish();*/
                    Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                    startActivity(intent);
                } else if(revision){
                    Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
