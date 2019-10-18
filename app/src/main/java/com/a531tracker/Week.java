package com.a531tracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.a531tracker.Database.DatabaseHelper;
import com.a531tracker.LiftBuilders.AsManyRepsAsPossible;
import com.a531tracker.LiftBuilders.CompoundLifts;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Week extends Activity {
    private FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    private final float[] warmupPercents = new float[]{0.40f, 0.50f, 0.60f};
    private final String[] warmupReps = new String[]{"1x5", "1x5", "1x3"};
    private float[] corePercents = new float[]{};
    private String[] coreReps = new String[]{};
    private String[] bbbReps = new String[]{"1x10", "1x10", "1x10", "1x10", "1x10"};

    private String compound;
    private Context mContext;

    private List<CompoundLifts> liftsArrayList = new ArrayList<>();
    private DatabaseHelper db = new DatabaseHelper(this);

    private FrameLayout warmupFrame;
    private FrameLayout bbbFrame;
    private FrameLayout amrapFrame;

    private TextView coreTitle;

    private LinearLayout warmupDisplay;
    private LinearLayout coreDisplay;
    private LinearLayout bbbDisplay;

    private Button amrapButton;
    private Button homeButton;
    private Button settingsButton;
    private Button uploadButton;

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

        setViews();
        setButtons();
        setListeners();
        getDatabaseLifts();
        weekSelected("5/5/5");
        navCheck();
        setHeaderText(compound, amrapWeight);
    }


    private void setHeaderText(String headerCompound, int headerWeight){
        TextView headerText = findViewById(R.id.header_text);
        String displayInfo = headerCompound + " " + headerWeight + "lbs";
        headerText.setText(displayInfo);
    }


    private void setButtons(){
        amrapButton = findViewById(R.id.submit_amrap);
        homeButton = findViewById(R.id.home_button);
        settingsButton = findViewById(R.id.settings_button);
        uploadButton = findViewById(R.id.upload_button);
    }

    private void setViews(){
        warmupFrame = findViewById(R.id.warmup_sets_frame);
        bbbFrame = findViewById(R.id.bbb_sets_frame);
        amrapFrame = findViewById(R.id.amrap_frame);

        warmupDisplay = findViewById(R.id.warmup_sets_display);
        coreDisplay = findViewById(R.id.core_sets_display);
        bbbDisplay = findViewById(R.id.bbb_sets_display);

        coreTitle = findViewById(R.id.core_title);
    }


    public void weekSelected(String day){
        removeDisplayViews();
        switch(day) {
            default:
            case "5/5/5":
                coreReps = new String[]{"1x5", "1x5", "1x5+"};
                corePercents = new float[]{0.65f, 0.75f, 0.85f};
                setWeeklyLifts(false);
                setAMRAPDetails();
                setAmrapFrameVisibility(true);
                break;
            case "3/3/3":
                coreReps = new String[]{"1x3", "1x3", "1x3+"};
                corePercents = new float[]{0.70f, 0.80f, 0.90f};
                setWeeklyLifts(false);
                setAMRAPDetails();
                setAmrapFrameVisibility(true);
                break;
            case "5/3/1":
                coreReps = new String[]{"1x5", "1x3", "1x1+"};
                corePercents = new float[]{0.75f, 0.85f, 0.95f};
                setWeeklyLifts(false);
                setAMRAPDetails();
                setAmrapFrameVisibility(true);
                break;
            case "DELOAD":
                coreReps = new String[]{"1x5", "1x5", "1x5"};
                corePercents = new float[]{0.40f, 0.50f, 0.60f};
                setWeeklyLifts(true);
                setAmrapFrameVisibility(false);
                break;
        }
    }


    private void setWeeklyLifts(boolean deloadWeek){
        if(deloadWeek){
            createWeeklyLiftsDisplays(coreDisplay, corePercents, coreReps);
        } else {
            createWeeklyLiftsDisplays(warmupDisplay, warmupPercents, warmupReps);
            createWeeklyLiftsDisplays(coreDisplay, corePercents, coreReps);
            float bbbValue = liftsArrayList.get(0).getBig_but_boring_weight();
            float[] bbbPercents  = new float[]{bbbValue,bbbValue,bbbValue,bbbValue,bbbValue};
            createWeeklyLiftsDisplays(bbbDisplay, bbbPercents, bbbReps);
        }
    }


    public void setListeners(){
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
        frameLayout.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));

        //TextView workoutName = createTextView("Placeholder Breakdown", new int[]{100,25});
        String workoutWeightText = String.valueOf(5*(Math.ceil((liftsArrayList.get(0).getTraining_max()*liftPercent)/5))) + " lbs";
        TextView workoutWeight = createTextView(workoutWeightText, new int[]{125, 25});
        TextView workoutReps = createTextView(reps, new int[]{700, 25});
        CheckBox checkbox = createCheckBox(new int[]{1100, 35});

        //frameLayout.addView(workoutName);
        frameLayout.addView(workoutReps);
        frameLayout.addView(workoutWeight);
        frameLayout.addView(checkbox);

        return frameLayout;
    }


    private TextView createTextView(String value, int[] marginValues){
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setText(value);
        tv.setTextSize(25);
        tv.setTextColor(Color.parseColor("#BB0000"));
        params.setMargins(marginValues[0], marginValues[1], 0, 0);
        tv.setLayoutParams(params);
        return tv;
    }


    private CheckBox createCheckBox(int[] marginValues){
        CheckBox checkBox = new CheckBox(this);
        params.setMargins(marginValues[0], marginValues[1], 0, 0 );
        //checkBox.setButtonTintList(ColorStateList.valueOf(0xFFD9A638));
        checkBox.setButtonTintList(ColorStateList.valueOf(0xFFBB0000));
        checkBox.setLayoutParams(params);
        return checkBox;
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


    private int submitAMRAP(){
        EditText amrapInput = findViewById(R.id.amrap_input);
        int amrapValue = Integer.parseInt(String.valueOf(amrapInput.getText()));
        String repPercent = String.valueOf(corePercents[2]);
        Log.d("AMRAP_WEIGHT", String.valueOf(amrapWeight));
        return db.updateAMRAPTable(compound, cycleValue, repPercent, amrapValue, amrapWeight);
    }


    private void startRepSubmission(){
        final int[] i = new int[1];
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(submitAMRAP() == 1){
                    newDialog(1);
                } else {
                    newDialog(0);
                }
            }
        });
    }


    private void newDialog(int i){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        String message;
        if(i == 0){
            message = mContext.getResources().getString(R.string.alert_amrap_unsuccessful_message);
        } else {
            message = mContext.getResources().getString(R.string.alert_amrap_successful_message);
        }
        builder.setTitle(R.string.alert_amrap_title)
                .setCancelable(true)
                .setMessage(message)
                .setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("Logged", "D");
                    }
                });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(alertDialog.isShowing())
                    alertDialog.dismiss();
            }
        }, 3000);
    }


    private void setAMRAPDetails(){
        TextView amrapLastWeek = findViewById(R.id.amrap_last_week_number);
        try{
            Log.d("Cycle_values Cycle", cycleValue-1+"");
            lastWeeksReps = db.getAMRAPValues(compound, (cycleValue-1));
            Log.d("Cycle_values 85", lastWeeksReps.getEighty_five_reps()+"");
            Log.d("Cycle_values 90", lastWeeksReps.getNinety_reps()+"");
            Log.d("Cycle_values 95", lastWeeksReps.getNinety_five_reps()+"");
            Log.d("Cycle_values Weight", lastWeeksReps.getTotalMaxWeight()+"");
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
                startRepSubmission();
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


    private void tabOnClicks(){
        TabLayout tabSelected = findViewById(R.id.tab_view_days);
        // Remove Deload week if it's a 7 week cycle vs. 4 week cycle
        if(cycleValue%2!=0){
            tabSelected.removeTab((Objects.requireNonNull(tabSelected.getTabAt(3))));
        }
        tabSelected.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentWeek = String.valueOf(tab.getText());

                if(currentWeek.equals("DELOAD")){
                    amrapButton.setEnabled(false);
                    coreTitle.setText(R.string.deload_set);
                    warmupFrame.setVisibility(View.GONE);
                    bbbFrame.setVisibility(View.GONE);
                } else {
                    amrapButton.setEnabled(true);
                    coreTitle.setText(R.string.core_set);
                    warmupFrame.setVisibility(View.VISIBLE);
                    bbbFrame.setVisibility(View.VISIBLE);
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
