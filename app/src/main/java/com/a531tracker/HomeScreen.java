package com.a531tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.a531tracker.Database.DatabaseHelper;
import com.a531tracker.ObjectBuilders.CompoundLifts;

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

    private ImageView accessoryView;
    private ImageView benchView;
    private ImageView deadliftView;
    private ImageView pressView;
    private ImageView squatView;
    private ImageView cycleView;

    private Button homeButton;
    private Button settingsButton;

    private TextView cycleDisplay;

    private TextView accessoryText;
    private TextView benchText;
    private TextView deadliftText;
    private TextView pressText;
    private TextView squatText;
    private TextView updateCycleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        mContext = this;

        cycleDisplay = findViewById(R.id.cycle_display);

        setViews();
        setText();
        setListeners();
    }


    protected void onStart(){
        super.onStart();
        startCycle();
        resetLifValuesArray();
        checkForLiftValues();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SET_TRAINING_MAX_CODE && resultCode == RESULT_OK){
            resetLifValuesArray();
        } else if (requestCode == UPDATE_TRAINING_MAX_CODE && resultCode == RESULT_OK){
            resetLifValuesArray();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void checkForLiftValues() {
        try{
            addLiftsToArray();
        } catch (Exception e){
            alertBuilder(getResources().getString(R.string.alert_no_lifts_found), getResources().getString(R.string.alert_no_lifts_message), "", false, true);
        }
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
            accessoryText.setVisibility(View.VISIBLE);
        }
    }


    private void setViews(){
        accessoryView = findViewById(R.id.accessories_hex);
        benchView = findViewById(R.id.bench_numbers);
        deadliftView = findViewById(R.id.deadlift_hex);
        pressView = findViewById(R.id.press_hex);
        squatView = findViewById(R.id.squat_hex);
        cycleView = findViewById(R.id.update_cycle_hex);

        homeButton = findViewById(R.id.home_button);
        settingsButton = findViewById(R.id.settings_button);
        setNav();
    }


    private void setNav(){
        navHome();
        navSettings();
    }


    private void setText(){
        accessoryText = findViewById(R.id.accessories_text);
        updateCycleText = findViewById(R.id.update_cycle_text);
        pressText = findViewById(R.id.press_text);
        deadliftText = findViewById(R.id.deadlift_text);
        benchText = findViewById(R.id.bench_text);
        squatText = findViewById(R.id.squat_text);
    }


    //Buttons

    public void setListeners(){
        benchButton();
        deadliftButton();
        pressButton();
        squatButton();
        updateCycleButton();
        accessoriesCheck();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void benchButton() {
        benchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCompoundWeek("Bench", "Overhand Press");
            }
        });
        benchText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        benchView.setPressed(true);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        benchView.setPressed(false);
                        break;
                }
                return false;
            }
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    private void deadliftButton(){
        deadliftText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCompoundWeek("Deadlift", "Squat");
            }
        });
        deadliftText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        deadliftView.setPressed(true);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        deadliftView.setPressed(false);
                        break;
                }
                return false;
            }
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    private void pressButton(){
        pressText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCompoundWeek("Overhand Press", "Bench");
            }
        });
        pressText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressView.setPressed(true);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        pressView.setPressed(false);
                        break;
                }
                return false;
            }
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    private void squatButton(){
        squatText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCompoundWeek("Squat", "Deadlift");
            }
        });
        squatText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        squatView.setPressed(true);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        squatView.setPressed(false);
                        break;
                }
                return false;
            }
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    private void updateCycleButton(){
        updateCycleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, UpdateValues.class);
                startActivityForResult(intent, UPDATE_TRAINING_MAX_CODE);
            }
        });
        updateCycleText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        cycleView.setPressed(true);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        cycleView.setPressed(false);
                        break;
                }
                return false;
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


    public void accessoriesButton (){
        accessoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
