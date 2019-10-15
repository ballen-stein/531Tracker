package com.a531tracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateValues extends AppCompatActivity {
    private Button cycleBtn;
    private Button benchBtn;
    private Button pressBtn;
    private Button squatBtn;
    private Button deadliftBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_cycle);

        setButtons();
        setListeners();
    }

    private void setListeners(){
        submitCycle();
        submitBench();
        submitPress();
        submitSquat();
        submitDeadlift();
    }


    private void setButtons(){
        cycleBtn = findViewById(R.id.tm_cycle_update_button);
        benchBtn = findViewById(R.id.tm_bench_update_button);
        pressBtn = findViewById(R.id.tm_press_update_button);
        squatBtn = findViewById(R.id.tm_squat_update_button);
        deadliftBtn = findViewById(R.id.tm_deadlift_update_button);
    }

    private void submitCycle(){
        cycleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    private void submitBench(){
        benchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    private void submitPress(){
        pressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    private void submitSquat(){
        squatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    private void submitDeadlift(){
        deadliftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
