package com.a531tracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import com.a531tracker.database.DatabaseHelper;
import com.a531tracker.ObjectBuilders.CompoundLifts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SetMaxes extends Activity {
    private int[] inputIds = new int[]{R.id.bench_input, R.id.press_input, R.id.squat_input, R.id.deadlift_input};
    private Map<String, CompoundLifts> mappedLifts = new HashMap<>();
    private ArrayList<Integer> liftValues = new ArrayList<>();

    private DatabaseHelper db = new DatabaseHelper(this);

    private Button cancelBtn;

    private float bbbPercent;

    private int weightCheck;

    private boolean firstLaunch;
    private boolean revision;
    private boolean usingKilos = false;

    private final String[] compoundLifts = new String[]{"Bench", "Overhand Press", "Squat", "Deadlift"};

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_maxes_view);

        setButtons();
        Intent intent = getIntent();

        try {
            firstLaunch = Objects.requireNonNull(intent.getExtras()).getBoolean("NewLifts", false);
            if(firstLaunch) {
                cancelBtn.setVisibility(View.GONE);
            } else {
                try {
                    liftValues = intent.getIntegerArrayListExtra("LIFT_VALUES");
                    revision = Objects.requireNonNull(intent.getExtras()).getBoolean("Revision", false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(!firstLaunch){
            getSettings();
            if(weightCheck == 8){
                CheckBox kiloBox = findViewById(R.id.inputting_as_kg);
                kiloBox.setChecked(true);
            }
            CheckBox checkBox = findViewById(R.id.training_maxes_used);
            checkBox.setChecked(true);
            setEditTextViews();
        }

    }

    private void getSettings(){
        weightCheck = Integer.parseInt(String.valueOf(String.valueOf(db.getUserSettings().getChosenBBBFormat()).charAt(0)));
    }

    private void setEditTextViews(){
        for(int i = 0; i < inputIds.length; i++) {
            EditText setTrainingMaxesFromDB = findViewById(inputIds[i]);
            if (weightCheck == 9) {
                setTrainingMaxesFromDB.setText(String.valueOf(liftValues.get(i)));
            } else {
                BigDecimal bd = new BigDecimal(liftValues.get(i)/2.20452).setScale(1, BigDecimal.ROUND_HALF_UP);
                setTrainingMaxesFromDB.setText(String.valueOf(bd));
            }
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
        db.startCycle();
    }


    private void startAMRAP(){
        int cycle = db.getCycle();
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

        CheckBox kiloBox = findViewById(R.id.inputting_as_kg);
        if(kiloBox.isChecked()) {
            usingKilos = true;
        }

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
            lifts.setBig_but_boring_weight(bbbPercent);
            int liftValue;
            if(usingKilos){
                double temp = ((Double.parseDouble(String.valueOf(trainingMaxInput.getText()))) * 2.20462);
                BigDecimal bd = new BigDecimal(temp).setScale(0, BigDecimal.ROUND_HALF_UP);
                liftValue = Integer.parseInt(String.valueOf(bd));
            } else {
                liftValue = (int) (Integer.parseInt(String.valueOf(trainingMaxInput.getText()))*modifier);
            }

            lifts.setTraining_max(liftValue);
            mappedLifts.put(compoundLifts[i], lifts);
        }
    }


    private void setBBB(int id){
        switch(id){
            case R.id.radio_bbb_30:
                bbbPercent = 0.30f;
                break;
            case R.id.radio_bbb_35:
                bbbPercent = 0.35f;
                break;
            case R.id.radio_bbb_40:
                bbbPercent = 0.40f;
                break;
            case R.id.radio_bbb_45:
                bbbPercent = 0.45f;
                break;
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
            case R.id.radio_bbb_80:
                bbbPercent = 0.80f;
                break;
            case R.id.radio_bbb_85:
                bbbPercent = 0.85f;
                break;
            case R.id.radio_bbb_90:
                bbbPercent = 0.90f;
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
                if(firstLaunch){
                    db.onNewUser(db.getReadableDatabase());
                    startAMRAP();
                } else if (revision){
                    db.onResetLifts(db.getReadableDatabase());
                }

                setLifts();
                inputLifts();
                startCycle();

                Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}
