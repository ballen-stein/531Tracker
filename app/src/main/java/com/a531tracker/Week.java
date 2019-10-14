package com.a531tracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.a531tracker.Database.DatabaseHelper;
import com.a531tracker.LiftBuilders.AsManyRepsAsPossible;
import com.a531tracker.LiftBuilders.CompoundLifts;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class Week extends Activity {
    private FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    private final float[] warmupPercents = new float[]{0.45f, 0.50f, 0.60f};
    private final String[] warmupReps = new String[]{"1x5", "1x5", "1x3"};
    private float[] corePercents = new float[]{};
    private String[] coreReps = new String[]{};
    private String[] bbbReps = new String[]{"1x10", "1x10", "1x10", "1x10", "1x10"};

    private String compound;
    private Context mContext;

    private List<CompoundLifts> liftsArrayList = new ArrayList<>();
    private DatabaseHelper db = new DatabaseHelper(this);

    private FrameLayout warmupFrame;
    private FrameLayout coreFrame;
    private FrameLayout bbbFrame;
    private FrameLayout amrapFrame;

    private LinearLayout warmupDisplay;
    private LinearLayout coreDisplay;
    private LinearLayout bbbDisplay;

    private Button warmupsButton;
    private Button coreButton;
    private Button bbbButton;
    private Button amrapButton;

    private FrameLayout informationButton;
    private Integer cycleValue;

    private int amrapWeight;
    private AsManyRepsAsPossible lastWeeksReps;
    private String currentWeek;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.week_view);

        Intent intent = getIntent();
        mContext = getApplicationContext();

        compound = intent.getStringExtra("Compound");
        cycleValue = intent.getIntExtra("Cycle", 1);
        setHeaderText(compound);

        setViews();
        setButtons();
        setListeners();
        getDatabaseLifts();
        weekSelected("Week One");
        selectButton(warmupsButton);
        warmupsButton.callOnClick();
    }


    private void setHeaderText(String headerCompound){
        TextView headerText = findViewById(R.id.header_text);
        headerText.setText(headerCompound);

        TextView cycleNumber = findViewById(R.id.cycle_number);
        String cycleNum = "Cycle Number " + cycleValue;
        cycleNumber.setText(cycleNum);
    }


    private void setButtons(){
        warmupsButton = findViewById(R.id.warmup_sets_btn);
        coreButton = findViewById(R.id.core_sets_btn);
        bbbButton = findViewById(R.id.bbb_sets_btn);
        amrapButton = findViewById(R.id.submit_amrap);
        //informationButton = findViewById(R.id.amrap_info_frame);
    }

    private void setViews(){
        warmupFrame = findViewById(R.id.warmup_sets_frame);
        coreFrame = findViewById(R.id.core_sets_frame);
        bbbFrame = findViewById(R.id.bbb_sets_frame);
        amrapFrame = findViewById(R.id.amrap_frame);

        warmupDisplay = findViewById(R.id.warmup_sets_display);
        coreDisplay = findViewById(R.id.core_sets_display);
        bbbDisplay = findViewById(R.id.bbb_sets_display);
    }


    private void deselectButtons(){
        warmupsButton.setSelected(false);
        coreButton.setSelected(false);
        bbbButton.setSelected(false);
    }


    private void selectButton(View view){
        deselectButtons();
        view.setSelected(true);
    }


    public void weekSelected(String day){
        resetAllViews();
        removeDisplayViews();
        switch(day) {
            default:
            case "Week One":
                coreReps = new String[]{"1x5", "1x5", "1x5+"};
                corePercents = new float[]{0.65f, 0.75f, 0.85f};
                setWeeklyLifts();
                setAMRAPDetails();
                setAmrapFrameVisibility(true);
                break;
            case "Week Two":
                coreReps = new String[]{"1x3", "1x3", "1x3+"};
                corePercents = new float[]{0.70f, 0.80f, 0.90f};
                setWeeklyLifts();
                setAMRAPDetails();
                setAmrapFrameVisibility(true);
                break;
            case "Week Three":
                coreReps = new String[]{"1x5", "1x3", "1x1+"};
                corePercents = new float[]{0.75f, 0.85f, 0.95f};
                setWeeklyLifts();
                setAMRAPDetails();
                setAmrapFrameVisibility(true);
                break;
            case "Week Four":
                coreReps = new String[]{"1x5", "1x5", "1x5"};
                corePercents = new float[]{0.40f, 0.50f, 0.60f};
                setWeeklyLifts();
                setAmrapFrameVisibility(false);
                break;
        }
    }


    private void setWeeklyLifts(){
        createWeeklyLiftsDisplays(warmupDisplay, warmupPercents, warmupReps);
        createWeeklyLiftsDisplays(coreDisplay, corePercents, coreReps);
        float bbbValue = liftsArrayList.get(0).getBig_but_boring_weight();
        float[] bbbPercents  = new float[]{bbbValue,bbbValue,bbbValue,bbbValue,bbbValue};
        createWeeklyLiftsDisplays(bbbDisplay, bbbPercents, bbbReps);
    }


    public void setListeners(){
        setReturnButton();
        warmUpButton();
        coreButton();
        bbbButton();
        tabOnClicks();
        amrapButton();
    }


    private void createWeeklyLiftsDisplays(LinearLayout layout, float[] liftPercent, String[] reps){
        for(int i = 0; i < liftPercent.length; i++){
            layout.addView(setWeekLifts(0, liftPercent[i], reps[i]));
        }
    }


    private FrameLayout setWeekLifts(int i, float liftPercent, String reps) {
        FrameLayout frameLayout = new FrameLayout(getApplicationContext());
        layoutParams.height = 200;
        frameLayout.setLayoutParams(layoutParams);
        frameLayout.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorBlack, null));

        TextView workoutName = createTextView(liftsArrayList.get(0).getCompound_movement(), new int[]{100,25});
        TextView workoutReps = createTextView(reps, new int[]{600, 25});
        TextView workoutWeight = createTextView(String.valueOf(5*(Math.ceil((liftsArrayList.get(0).getTraining_max()*liftPercent)/5))), new int[]{900, 25});
        CheckBox checkbox = createCheckBox(new int[]{1100, 5});

        frameLayout.addView(workoutName);
        frameLayout.addView(workoutReps);
        frameLayout.addView(workoutWeight);
        frameLayout.addView(checkbox);

        return frameLayout;
    }


    private TextView createTextView(String value, int[] marginValues){
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setText(value);
        tv.setTextSize(14);
        tv.setTextColor(Color.parseColor("#FFD9A638"));
        params.setMargins(marginValues[0], marginValues[1], 0, 0);
        tv.setLayoutParams(params);
        return tv;
    }


    private CheckBox createCheckBox(int[] marginValues){
        CheckBox checkBox = new CheckBox(this);
        params.setMargins(marginValues[0], marginValues[1], 0, 0 );
        checkBox.setButtonTintList(ColorStateList.valueOf(0xFFD9A638));
        checkBox.setLayoutParams(params);
        return checkBox;
    }


    private void resetAllViews(){
        warmupFrame.setVisibility(View.GONE);
        coreFrame.setVisibility(View.GONE);
        bbbFrame.setVisibility(View.GONE);
    }


    private void removeDisplayViews(){
        warmupDisplay.removeAllViews();
        coreDisplay.removeAllViews();
        bbbDisplay.removeAllViews();
    }


    private void setAmrapFrameVisibility(boolean bool){
        if(!bool)
            amrapFrame.setVisibility(View.GONE);
        else
            amrapFrame.setVisibility(View.VISIBLE);
    }


    private void submitAMRAP(){
        EditText amrapInput = findViewById(R.id.amrap_input);
        int amrapValue = Integer.parseInt(String.valueOf(amrapInput.getText()));
        String repPercent = String.valueOf(corePercents[2]);
        Log.d("AMRAP_WEIGHT", String.valueOf(amrapWeight));
        int response = db.updateAMRAPTable(compound, cycleValue, repPercent, amrapValue, amrapWeight);
        if(response == 1){
            Log.d("Success", "Successfully updated AMRAP!");
        }
    }


    private void setAMRAPDetails(){
        TextView amrapLastWeek = findViewById(R.id.amrap_last_week_number);
        try{
            Log.d("Cycle_values", cycleValue-1+"");
            lastWeeksReps = db.getAMRAPValues(compound, (cycleValue-1));
            Log.d("Cycle_values", lastWeeksReps.toString());
            Log.d("Cycle_values", lastWeeksReps.getEighty_five_reps()+"");
            Log.d("Cycle_values", lastWeeksReps.getNinety_reps()+"");
            Log.d("Cycle_values", lastWeeksReps.getNinety_five_reps()+"");
            String repsDone = String.valueOf(findAMRAPPercent(String.valueOf(corePercents[2])));
            amrapLastWeek.setText(repsDone);
        } catch (Exception e){
            amrapLastWeek.setText("5");
        }
    }


    private int findAMRAPPercent(String floatVal){
        switch(floatVal){
            case "0.9":
                return lastWeeksReps.getNinety_reps();
            case "0.85":
                return lastWeeksReps.getEighty_five_reps();
            default:
            case "0.95":
                return lastWeeksReps.getNinety_five_reps();
        }
    }


    // ---- Buttons & onClicks
    private void amrapButton() {
        amrapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitAMRAP();
            }
        });
    }


    private void setReturnButton() {
        findViewById(R.id.returnButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    private void warmUpButton() {
        warmupsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(warmupFrame.getVisibility() == View.VISIBLE){
                    deselectButtons();
                    resetAllViews();
                } else {
                    resetAllViews();
                    selectButton(warmupsButton);
                    warmupFrame.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void coreButton(){
        coreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(coreFrame.getVisibility() == View.VISIBLE) {
                    deselectButtons();
                    resetAllViews();
                } else {
                    selectButton(coreButton);
                    resetAllViews();
                    coreFrame.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void bbbButton(){
        bbbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bbbFrame.getVisibility() == View.VISIBLE){
                    deselectButtons();
                    resetAllViews();
                } else {
                    selectButton(bbbButton);
                    resetAllViews();
                    bbbFrame.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void tabOnClicks(){
        TabLayout tabSelected = findViewById(R.id.tab_view_days);
        /*if(cycleValue%2==-0){
            tabSelected.addTab(tabSelected.newTab().setText("Week Four"));
        }*/
        tabSelected.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                deselectButtons();
                currentWeek = String.valueOf(tab.getText());

                if(currentWeek.equals("Week Four")){
                    amrapButton.setEnabled(false);
                } else {
                    amrapButton.setEnabled(true);
                }

                weekSelected(currentWeek);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    public void getDatabaseLifts(){
        liftsArrayList.add(db.getLifts(compound));
        amrapWeight = liftsArrayList.get(0).getTraining_max();
    }
}
