package com.a531tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.a531tracker.Database.DatabaseHelper;

public class HomeScreen extends AppCompatActivity {
    static final int SET_TRAINING_MAX_CODE = 1;
    static final int UPDATE_TRAINING_MAX_CODE = 2;

    public static String[] compoundLifts = new String[]{"Bench", "Overhand Press", "Squat", "Deadlift"};

    private DatabaseHelper db;
    private Context mContext;
    private Integer cycleValue;

    private Button benchView, deadliftView, pressView, squatView, cycleView;
    private Button homeButton, settingsButton;

    private TextView[] newTvArray;
    private TextView cycleDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        mContext = this;

        cycleDisplay = findViewById(R.id.cycleDisplay);

        setViews();
        setNav();
        setListeners();
    }


    protected void onStart(){
        super.onStart();
        startCycle();
        setCurrentTrainingValues();
    }


    private void setCurrentTrainingValues() {
        for(int i=0; i < compoundLifts.length; i++){
            newTvArray[i].setText(
                    String.valueOf(
                            db.getLifts(compoundLifts[i]).getTraining_max()
                    )
            );
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SET_TRAINING_MAX_CODE && resultCode == RESULT_OK){
            //resetLifValuesArray();
        } else if (requestCode == UPDATE_TRAINING_MAX_CODE && resultCode == RESULT_OK){
            //resetLifValuesArray();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void openCompoundWeek(String compound, String swap){
        Intent intent = new Intent(getApplicationContext(), Week.class);
        intent.putExtra("Compound", compound);
        intent.putExtra("Swap", swap);
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
                            intent.putExtra("NewLifts", true);
                            startActivityForResult(intent, SET_TRAINING_MAX_CODE);
                        }
                    }
                });
        if(!noLiftsFound){
            TextView tv = new TextView(this);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
            tv.setText(extraMessage);
            tv.setTextSize(getResources().getDimension(R.dimen.text_8));
            dialog.setView(tv);
        } else {
            dialog.setMessage(message);
        }
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }


    private void startCycle(){
        try{
            if(db.startCycle()){
                cycleValue = db.getCycle();
            }
        } catch (Exception e){
            cycleValue = 1;
            e.printStackTrace();
        }
        String cycleText = "You are currently on Cycle #" + cycleValue;
        cycleDisplay.setText(cycleText);
    }


    private void setViews(){
        benchView = findViewById(R.id.bench_btn);
        deadliftView = findViewById(R.id.deadlift_btn);
        pressView = findViewById(R.id.press_btn);
        squatView = findViewById(R.id.squat_btn);
        cycleView = findViewById(R.id.update_cycle_btn);

        homeButton = findViewById(R.id.home_button);
        settingsButton = findViewById(R.id.settings_button);

        newTvArray = new TextView[]{findViewById(R.id.bench_value), findViewById(R.id.press_value), findViewById(R.id.squat_value), findViewById(R.id.deadlift_value)};
    }


    private void setNav(){
        navHome();
        navSettings();
    }


    //Buttons

    public void setListeners(){
        benchButton();
        deadliftButton();
        pressButton();
        squatButton();
        updateCycleButton();
    }


    private void benchButton() {
        benchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCompoundWeek("Bench", "Overhand Press");
            }
        });
    }


    private void deadliftButton(){
        deadliftView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCompoundWeek("Deadlift", "Squat");
            }
        });
    }


    private void pressButton(){
        pressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCompoundWeek("Overhand Press", "Bench");
            }
        });
    }


    private void squatButton(){
        squatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCompoundWeek("Squat", "Deadlift");
            }
        });
    }



    private void updateCycleButton(){
        cycleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, UpdateValues.class);
                startActivityForResult(intent, UPDATE_TRAINING_MAX_CODE);
            }
        });
    }


    private void navHome(){
        homeButton.setEnabled(false);
    }


    private void navSettings(){
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, Settings.class);
                startActivity(intent);
            }
        });
    }
}
