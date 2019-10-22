package com.a531tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.a531tracker.Database.DatabaseHelper;
import com.a531tracker.LiftBuilders.AsManyRepsAsPossible;
import com.a531tracker.LiftBuilders.CompoundLifts;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends AppCompatActivity {
    static final int SET_TRAINING_MAX_CODE = 1;
    static final int UPDATE_TRAINING_MAX_CODE = 2;

    public static String[] compoundLifts = new String[]{"Bench", "Overhand Press", "Squat", "Deadlift"};
    private List<CompoundLifts> liftsArray = new ArrayList<>();
    private List<Integer> liftValues = new ArrayList<>();

    private DatabaseHelper db;
    private Context mContext;
    private Integer cycleValue;

    private Button accessoryButton;
    private Button benchNumbers;
    private Button deadliftNumbers;
    private Button pressNumbers;
    private Button squatNumbers;
    private Button tmUpdate;

    private Button homeButton;
    private Button uploadLifts;
    private Button settingsButton;

    private TextView cycleDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        mContext = this;

        cycleDisplay = findViewById(R.id.cycle_display);

        setButtons();
        setListeners();
        //checkForLiftValues();
        createNavigation();
    }


    protected void onStart(){
        super.onStart();
        Log.d("OnStartCalled", "OnStartFired");
        startCycle();
        checkForLiftValues();
    }

    // TODO Create listener/Change activity for set total maxes so lifts are reset
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SET_TRAINING_MAX_CODE && resultCode == RESULT_OK){
            Log.d("OnStartCalled", "ActivityResultFired1");
            resetLifValuesArray();
        } else if (requestCode == UPDATE_TRAINING_MAX_CODE && resultCode == RESULT_OK){
            Log.d("OnStartCalled", "ActivityResultFired2");
            resetLifValuesArray();
            //onStart();
        } else if(resultCode == RESULT_CANCELED){
            Log.d("RESULT_ACT", "CANCELED");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void checkForLiftValues() {
        try{
            addLiftsToArray();
        } catch (NullPointerException e){
            Log.d("Compound_lifts", "No lifts found, force user to make lifts");
        } catch (Exception e){
            alertBuilder(getResources().getString(R.string.alert_no_lifts_found), getResources().getString(R.string.alert_no_lifts_message), "", false, true);
        }
    }


    public void openCompoundWeek(String compound){
        Intent intent = new Intent(getApplicationContext(), Week.class);
        intent.putExtra("Compound", compound);
        intent.putExtra("Cycle", cycleValue);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }


    public void alertBuilder(String title, String message, String extraMessage, boolean cancelable, final boolean noLiftsFound){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        dialog.setTitle(title)
                .setMessage(message)
                .setCancelable(cancelable)
                .setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(noLiftsFound) {
                            Intent intent = new Intent(mContext, SetMaxes.class);
                            intent.putExtra("Has_Lifts", true);
                            startActivityForResult(intent, SET_TRAINING_MAX_CODE);
                        }
                    }
                });
        if(!noLiftsFound){
            TextView tv = new TextView(this);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorRed));
            tv.setText(extraMessage);
            tv.setTextSize(getResources().getDimension(R.dimen.text_8));
            dialog.setView(tv);
        } else {
            dialog.setMessage(message);
        }
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }


    public class emptyCheck implements isItEmpty {
        @Override
        public boolean emptyChecker(boolean val) {
            return val;
        }
    }


    private void startCycle(){
        try{
            if(db.startCycle()){
                cycleValue = db.getCycle();
                Log.d("Cycle_Value", cycleValue+" from if");
            } else {
                Log.d("Cycle_Started", "Cycle has already started");
                Log.d("Cycle_Val", cycleValue+" from else");
            }
        } catch (Exception e){
            cycleValue = 1;
            Log.d("Cycle_Value", "Default from catch");
            e.printStackTrace();
        }
        String cycleText = "You are currently on Cycle #" + cycleValue;
        cycleDisplay.setText(cycleText);
    }


    private void addLiftsToArray(){
        for(String lifts : compoundLifts){
            liftsArray.add(db.getLifts(lifts));
            liftValues.add(db.getLifts(lifts).getTraining_max());
        }
    }


    private void resetLifValuesArray(){
        liftsArray.clear();
        liftValues.clear();
    }


    public void accessoriesCheck(){
        if(getResources().getBoolean(R.bool.have_accessories)) {
            accessoriesButton();
            accessoryButton.setVisibility(View.VISIBLE);
        }
    }


    private void setButtons(){
        accessoryButton = findViewById(R.id.addAccessoriesButton);
        benchNumbers = findViewById(R.id.bench_numbers);
        deadliftNumbers = findViewById(R.id.deadlift_numbers);
        pressNumbers = findViewById(R.id.press_numbers);
        squatNumbers = findViewById(R.id.squat_numbers);
        tmUpdate = findViewById(R.id.increase_training_max);

        homeButton = findViewById(R.id.home_button);
        settingsButton = findViewById(R.id.settings_button);
        uploadLifts = findViewById(R.id.upload_button);
    }


    public void setListeners(){
        benchButton();
        deadliftButton();
        pressButton();
        squatButton();
        updateTrainingMax();
        //accessoriesButton();
        accessoriesCheck();
        testBBB();
    }


    //Buttons
    private void benchButton() {
        benchNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCompoundWeek("Bench");
            }
        });
    }


    private void deadliftButton(){
        deadliftNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCompoundWeek("Deadlift");
            }
        });
    }


    private void pressButton(){
        pressNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCompoundWeek("Overhand Press");
            }
        });
    }


    private void squatButton(){
        squatNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCompoundWeek("Squat");
            }
        });
    }


    private void testBBB(){
        Button testBBB = findViewById(R.id.bbb_test);
        testBBB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(String lifts : compoundLifts){
                    CompoundLifts newLifts = new CompoundLifts();
                    newLifts.setBig_but_boring_weight(0.65f);
                    newLifts.setCompound_movement(lifts);
                    int i = db.updateBBBWeight(newLifts);
                    if(i == 1){
                        Log.d("BBB Update", "Successfully updated BBB weights!");
                    }
                }
                testCheck();
            }
        });
    }


    private void testCheck(){
        AsManyRepsAsPossible amrap = db.checkForMissing("Squat", cycleValue, liftValues.get(2));
        //Bench - 190 | Press - 145 | Squat - 120 | Deadlift - 135

        Log.d("AMRAP_Values", "Weight: " + amrap.getTotalMaxWeight()
                + "\nCycle: " + amrap.getCycle() + "\nCompound: " + amrap.getCompound()
                + "\n85: " + amrap.getEighty_five_reps() + "\n90: " + amrap.getNinety_reps()
                + "\n95: " + amrap.getNinety_five_reps());

        amrap = db.checkForMissing("Deadlift", cycleValue, liftValues.get(3));

        Log.d("AMRAP_Values", "Weight: " + amrap.getTotalMaxWeight()
                + "\nCycle: " + amrap.getCycle() + "\nCompound: " + amrap.getCompound()
                + "\n85: " + amrap.getEighty_five_reps() + "\n90: " + amrap.getNinety_reps()
                + "\n95: " + amrap.getNinety_five_reps());

        amrap = db.checkForMissing("Overhand Press", cycleValue, liftValues.get(1));

        Log.d("AMRAP_Values", "Weight: " + amrap.getTotalMaxWeight()
                + "\nCycle: " + amrap.getCycle() + "\nCompound: " + amrap.getCompound()
                + "\n85: " + amrap.getEighty_five_reps() + "\n90: " + amrap.getNinety_reps()
                + "\n95: " + amrap.getNinety_five_reps());

        amrap = db.checkForMissing("Bench", cycleValue, liftValues.get(0));

        Log.d("AMRAP_Values", "Weight: " + amrap.getTotalMaxWeight()
                + "\nCycle: " + amrap.getCycle() + "\nCompound: " + amrap.getCompound()
                + "\n85: " + amrap.getEighty_five_reps() + "\n90: " + amrap.getNinety_reps()
                + "\n95: " + amrap.getNinety_five_reps());
    }


    private void updateTrainingMax(){
        tmUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, UpdateValues.class);
                startActivityForResult(intent, UPDATE_TRAINING_MAX_CODE);
            }
        });
    }


    private void createNavigation(){
        homeNav();
        settingsNav();
        setTrainingMax();
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
                Intent intent = new Intent(mContext, Settings.class);
                startActivity(intent);
            }
        });
    }


    public void setTrainingMax(){
        uploadLifts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(new emptyCheck().emptyChecker(liftsArray.isEmpty())){
                    Intent intent = new Intent(mContext, SetMaxes.class);
                    startActivity(intent);
                } else {
                    Log.d("IntentStarted", "Correct intent!");
                    Intent intent = new Intent(mContext, SetMaxes.class);
                    intent.putIntegerArrayListExtra("LIFT_VALUES", (ArrayList<Integer>) liftValues);
                    intent.putExtra("Revision", true);
                    startActivity(intent);
                }
            }
        });
    }


    public void accessoriesButton (){
        accessoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
