package com.a531tracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.a531tracker.database.DatabaseHelper;
import com.a531tracker.DetailFragments.SubmitAmrap;
import com.a531tracker.ObjectBuilders.AsManyRepsAsPossible;
import com.a531tracker.ObjectBuilders.CompoundLifts;
import com.a531tracker.ObjectBuilders.UserSettings;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Week extends AppCompatActivity implements SubmitAmrap.AllClicks{
    private TableRow.LayoutParams tableParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);

    private SubmitAmrap submitAmrap;

    private final float[] warmupPercents = new float[]{0.40f, 0.50f, 0.60f};
    private float[] corePercents = new float[]{};

    private ArrayList<String> warmupReps;
    private ArrayList<String> coreReps;
    private final ArrayList<String> bbbReps = new ArrayList<>();

    private List<CompoundLifts> liftsArrayList = new ArrayList<>();
    private DatabaseHelper db = new DatabaseHelper(this);

    private LinearLayout bbbFrame;
    private LinearLayout coreFrame;

    private LinearLayout amrapFrame;

    private TableLayout warmupDisplay, coreDisplay, bbbDisplay;

    private TextView warmupTitle, amrapLastWeek;

    private Button amrapButton, homeButton, settingsButton, jokerButton;

    private TabLayout tabSelected;

    private Integer cycleValue;

    private int[] settingsArray = new int[6];

    private int weightCheck, deloadWeekSettingVal, jokerSettingVal, fslSettingVal, eightFormatSettingVal, removeBbbOptionsSettingsVal, amrapWeight, currentWeek, jokerStartWeight;
    private int paddingVal;

    private boolean swapCheckVal;

    private String compound, swapLift, repsDone, weightSuffix;

    private boolean newSettings = true;

    private Context mContext;

    private AsManyRepsAsPossible lastWeeksReps;
    private UserSettings userSettings;
    private CalculateWeight calculateWeight = new CalculateWeight();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.week_old_view);

        Intent intent = getIntent();
        mContext = this;

        compound = intent.getStringExtra("Compound");
        swapLift = intent.getStringExtra("Swap");
        cycleValue = intent.getIntExtra("Cycle", 1);

        setViews();
        setButtons();
        getDatabaseLifts();
    }


    @Override
    protected void onStart(){
        super.onStart();
        paddingVal = (int) mContext.getResources().getDimension(R.dimen.workout_frame_padding);
        getSettings();
        setSettingValues(userSettings);
        if(resumeCheck(settingsArray)){
            newSettings = true;
            currentWeek = tabSelected.getSelectedTabPosition();
        }
        setAllValues(settingsArray);
        swapCheckVal = checkForSwaps(userSettings);
        setTabText();
        setTabViews(userSettings);
        setListeners();
        setJokerButton(jokerSettingVal == 1);

        if(weightCheck == 9){
            weightSuffix = " lbs";
        } else
            weightSuffix = " kgs";

        if(newSettings){
            weekSelected(currentWeek);
            setHeaderText(compound, amrapWeight);
            newSettings = false;
        }
    }


    private void getSettings(){
        userSettings = db.getUserSettings();
    }


    private void setSettingValues(UserSettings userSettings) {
        String str = String.valueOf(userSettings.getChosenBBBFormat());
        char[] strArray = str.toCharArray();
        for(int i = 0; i < strArray.length; i++){
            settingsArray[i] = Integer.parseInt(String.valueOf(strArray[i]));
        }
    }


    private boolean resumeCheck(int[] settings){
        if(settings[0] != weightCheck
                || settings[1] != removeBbbOptionsSettingsVal
                || settings[2] != deloadWeekSettingVal
                || settings[3] != jokerSettingVal
                || settings[4] != fslSettingVal
                || settings[5] != eightFormatSettingVal
                || checkForSwaps(userSettings) != swapCheckVal
                )
            return true;
        else
            return false;
    }


    private boolean checkForSwaps(UserSettings userSettings){
        return (userSettings.getSwapBBBFormat() == 1);
    }


    private void setAllValues(int[] settings){
        weightCheck = settings[0];
        removeBbbOptionsSettingsVal = settings[1];
        deloadWeekSettingVal = settings[2];
        jokerSettingVal = settings[3];
        fslSettingVal = settings[4];
        eightFormatSettingVal = settings[5];
    }


    private void setTabText(){
        if(eightFormatSettingVal == 1){
            Objects.requireNonNull(tabSelected.getTabAt(0)).setText(R.string.format_863_week_1);
            Objects.requireNonNull(tabSelected.getTabAt(1)).setText(R.string.format_863_week_2);
            Objects.requireNonNull(tabSelected.getTabAt(2)).setText(R.string.format_863_week_3);
        } else {
            Objects.requireNonNull(tabSelected.getTabAt(0)).setText(R.string.format_531_week_1);
            Objects.requireNonNull(tabSelected.getTabAt(1)).setText(R.string.format_531_week_2);
            Objects.requireNonNull(tabSelected.getTabAt(2)).setText(R.string.format_531_week_3);
        }
    }


    private void setTabViews(UserSettings userSettings) {
        if(tabSelected.getTabCount() < 4)
            tabSelected.addTab(tabSelected.newTab().setText("DELOAD"));

        if(userSettings.getWeekFormat() != 1) {
            if (cycleValue % 2 != 0)
                tabSelected.removeTab((Objects.requireNonNull(tabSelected.getTabAt(3))));
        }
    }


    public void weekSelected(int day){
        removeDisplayViews();
        switch(day) {
            default:
            case 0:
                setLiftValues(1);
                setWeeklyLifts(false);
                setAMRAPDetails();
                setAmrapFrameVisibility(true);
                break;
            case 1:
                setLiftValues(2);
                setWeeklyLifts(false);
                setAMRAPDetails();
                setAmrapFrameVisibility(true);
                break;
            case 2:
                setLiftValues(3);
                setWeeklyLifts(false);
                setAMRAPDetails();
                setAmrapFrameVisibility(true);
                break;
            case 3:
                setWeeklyLifts(true);
                setAmrapFrameVisibility(false);
                break;
        }
    }


    private void setLiftValues(int weekNumber){
        Collections.addAll(bbbReps, getResources().getStringArray(R.array.format_531_bbb));
        warmupReps = new ArrayList<>();
        coreReps = new ArrayList<>();
        if(eightFormatSettingVal == 1){
            Collections.addAll(warmupReps, getResources().getStringArray(R.array.format_863_warmup));
            switch (weekNumber){
                case 1:
                    Collections.addAll(coreReps, getResources().getStringArray(R.array.format_863_core_week_one));
                    corePercents = new float[]{0.65f, 0.75f, 0.80f};
                    break;
                case 2:
                    Collections.addAll(coreReps, getResources().getStringArray(R.array.format_863_core_week_two));
                    corePercents = new float[]{0.70f, 0.80f, 0.85f};
                    break;
                case 3:
                    Collections.addAll(coreReps, getResources().getStringArray(R.array.format_863_core_week_three));
                    corePercents = new float[]{0.75f, 0.85f, 0.90f};
                    break;
            }
        } else {
            Collections.addAll(warmupReps, getResources().getStringArray(R.array.format_531_warmup));
            switch (weekNumber){
                default:
                case 1:
                    Collections.addAll(coreReps, getResources().getStringArray(R.array.format_531_core_week_one));
                    corePercents = new float[]{0.65f, 0.75f, 0.85f};
                    break;
                case 2:
                    Collections.addAll(coreReps, getResources().getStringArray(R.array.format_531_core_week_two));
                    corePercents = new float[]{0.70f, 0.80f, 0.90f};
                    break;
                case 3:
                    Collections.addAll(coreReps, getResources().getStringArray(R.array.format_531_core_week_three));
                    corePercents = new float[]{0.75f, 0.85f, 0.95f};
                    break;
            }
        }
    }


    private void setWeeklyLifts(boolean deloadWeek) {
        createWeeklyLiftsDisplays(warmupDisplay, warmupPercents, warmupReps);
        if (!deloadWeek) {
            createWeeklyLiftsDisplays(coreDisplay, corePercents, coreReps);
        }
        if (removeBbbOptionsSettingsVal == 1 && fslSettingVal != 1) {
            bbbFrame.setVisibility(View.GONE);
        } else {
            bbbFrame.setVisibility(View.VISIBLE);
            if(fslSettingVal == 1){
                if(swapCheckVal){

                } else {
                    float[] fslValue = new float[]{corePercents[0]};
                    ArrayList<String> fslList = new ArrayList<>();
                    Collections.addAll(fslList, getResources().getStringArray(R.array.fsl_valus));
                    for(int i = 0; i < fslList.size(); i++){
                        createWeeklyLiftsDisplays(bbbDisplay, fslValue, fslList);
                    }

                    TextView bbbTitle = findViewById(R.id.bbb_title);
                    bbbTitle.setText(R.string.fsl_header);
                }
            } else {
                float bbbValue = liftsArrayList.get(0).getPercent();
                float[] bbbPercents;

                if (deloadWeek)
                    bbbPercents = new float[]{bbbValue * .90f, bbbValue * .90f, bbbValue * .90f};
                else
                    bbbPercents = new float[]{bbbValue, bbbValue, bbbValue, bbbValue, bbbValue};

                TextView bbbTitle = findViewById(R.id.bbb_title);
                if (swapCheckVal) {
                    String text = swapLift + " " + getResources().getString(R.string.bbb_text);
                    bbbTitle.setText(text);
                } else {
                    bbbTitle.setText(getResources().getString(R.string.bbb_text));
                }
                createWeeklyLiftsDisplays(bbbDisplay, bbbPercents, bbbReps);
            }
        }
    }


    private void createWeeklyLiftsDisplays(TableLayout layout, float[] liftPercent, ArrayList<String> reps){
        for(int i = 0; i < liftPercent.length; i++){
            int trainingValue;
            if(layout == bbbDisplay){
                if(swapCheckVal && fslSettingVal != 1){
                    CompoundLifts swapLiftValue = db.getLifts(swapLift);
                    trainingValue = swapLiftValue.getTrainingMax();
                    layout.addView(setWeekLifts(swapLiftValue.getPercent(), reps.get(i), trainingValue));
                    layout.addView(weightBreakdown());
                } else {
                    trainingValue = liftsArrayList.get(0).getTrainingMax();
                    layout.addView(setWeekLifts(liftPercent[i], reps.get(i), trainingValue));
                    layout.addView(weightBreakdown());
                }
            } else {
                trainingValue = liftsArrayList.get(0).getTrainingMax();
                layout.addView(setWeekLifts(liftPercent[i], reps.get(i), trainingValue));
                layout.addView(weightBreakdown());
            }
        }
        if(layout == coreDisplay && jokerSettingVal == 1){
            jokerStartWeight = (int) (5 * (Math.ceil((liftsArrayList.get(0).getTrainingMax() * liftPercent[2])/5)));
        }
    }

    private double weightDistVal = 0;

    public TableRow weightBreakdown(){
        calculateWeight.weightBreakdown(weightDistVal, true);
        ArrayList weightDistArray = calculateWeight.getWeightArray();
        double barWeight;
        if(weightCheck == 9){
             barWeight = (double) weightDistArray.get(0);
        } else {
            String str = String.valueOf(weightDistArray.get(0));
            str = str.substring(0, str.length()-2);
            barWeight = Double.parseDouble(calculateWeight.setAsKilograms(Integer.parseInt(str), 1.0f));
        }

        String weightDistDisplayText = "Bar Weight: " + barWeight + "\nPlate Weight to both sides: ";
        for(int i = 1; i < weightDistArray.size(); i++){
            if(weightCheck == 9){
                weightDistDisplayText += (weightDistArray.get(i)) + " | ";
            } else {
                String str = String.valueOf(weightDistArray.get(i));
                str = str.substring(0, str.length()-2);
                str = calculateWeight.setAsKilograms(Integer.parseInt(str), 1.0f);
                weightDistDisplayText += str + " | ";
            }
        }

        weightDistDisplayText = weightDistDisplayText.substring(0, weightDistDisplayText.length() - 2);
        System.out.println(weightDistDisplayText);
        weightDistVal = 0;
        calculateWeight.resetWeightArray();

        TableRow tableRow = new TableRow(mContext);
        TextView textView = new TextView(mContext);
        textView.setText(weightDistDisplayText);
        textView.setTextSize(12);
        textView.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        tableParams.weight = 1.0f;
        textView.setLayoutParams(tableParams);
        textView.setGravity(Gravity.CENTER);
        tableRow.addView(textView);

        return tableRow;
    }

    private TableRow setWeekLifts(float liftPercent, String reps, int trainingValue) {
        TableRow tableRow = new TableRow(mContext);
        String workoutWeightText;
        if(weightCheck == 9) {
            workoutWeightText = calculateWeight.setAsPounds(trainingValue, liftPercent);
        } else {
            workoutWeightText =  calculateWeight.setAsKilograms(trainingValue, liftPercent);
        }
        weightDistVal = Double.parseDouble(calculateWeight.setAsPounds(trainingValue, liftPercent));
        workoutWeightText+= weightSuffix;

        TextView workoutWeight = createTextView(workoutWeightText, paddingVal);
        TextView workoutReps = createTextView(reps, paddingVal);
        CheckBox checkbox = createCheckBox(paddingVal);

        tableRow.addView(workoutWeight);
        tableRow.addView(workoutReps);
        tableRow.addView(checkbox);

        return tableRow;
    }


    private TextView createTextView(String value, int padding){
        TextView tv = new TextView(mContext);
        tv.setText(value);
        tv.setTextSize(25);
        tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        //tv.setBackground(ResourcesCompat.getDrawable(getResources(), (R.drawable.table_boxes), null));
        tv.setPadding(padding*2, padding, padding, padding);
        tableParams.weight = 0.45f;
        tv.setLayoutParams(tableParams);
        if(value.equals("OR"))
            tv.setGravity(Gravity.CENTER);
        else
            tv.setGravity(Gravity.START);
        return tv;
    }


    private CheckBox createCheckBox(int padding){
        CheckBox checkBox = new CheckBox(mContext);
        checkBox.setButtonTintList(ColorStateList.valueOf(0xFF84C9FB));
        //checkBox.setBackground(ResourcesCompat.getDrawable(getResources(), (R.drawable.table_boxes), null));
        checkBox.setPadding(padding, padding, padding, padding);
        checkBox.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.20f));
        checkBox.setGravity(Gravity.CENTER_VERTICAL);
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


    private void setJokerButton(boolean bool){
        if(!bool)
            jokerButton.setVisibility(View.GONE);
        else
            jokerButton.setVisibility(View.VISIBLE);
    }


    private int submitAMRAP(int amrapValue){
        String repPercent;
        if(eightFormatSettingVal == 1) {
            repPercent = (String.valueOf(corePercents[2] + 0.05f)).substring(0, 4);
            if(repPercent.equals("0.90"))
                repPercent = "0.9";
        }
        else
            repPercent = String.valueOf(corePercents[2]);
        Log.d("Testing", "data " + compound +", " + cycleValue+", " +  repPercent+", " +  amrapValue+", " +  amrapWeight );
        return db.updateAMRAPTable(compound, cycleValue, repPercent, amrapValue, amrapWeight);
    }


    private void startRepSubmission(final int value){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(submitAMRAP(value) == 1){
                    newDialog(1);
                    closeAmrapFragment();
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
        try{
            lastWeeksReps = db.getAMRAPValues(compound, (cycleValue-1));
            if(eightFormatSettingVal == 1) {
                repsDone = String.valueOf(findAMRAPPercent((String.valueOf(corePercents[2] + 0.05f)).substring(0, 4)));
            } else {
                repsDone = String.valueOf(findAMRAPPercent(String.valueOf(corePercents[2])));
            }
            amrapLastWeek.setText(repsDone);
        } catch (Exception e){
            amrapLastWeek.setText("5");
        }
    }


    private int findAMRAPPercent(String floatVal){
        switch(floatVal){
            case "0.85":
                return lastWeeksReps.getEighty_five_reps();
            case "0.90":
            case "0.9":
                return lastWeeksReps.getNinety_reps();
            default:
            case "0.95":
                return lastWeeksReps.getNinety_five_reps();
        }
    }


    private void jokerChoose(){
        ErrorAlerts jokerAlert = new ErrorAlerts(mContext);
        jokerAlert.setErrorAlertsValues(true, true, getString(R.string.joker_create), getString(R.string.joker_message), "OK", false);
        jokerAlert.blankAlert(mContext).setPositiveButton("1x5", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addJokerSet("1x5");
            }
        }).setNeutralButton("1x1", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addJokerSet("1x1");
            }
        }).setNegativeButton("1x3", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addJokerSet("1x3");
            }
        }).show();
    }


    private void addJokerSet(String reps){
        coreDisplay.addView(setWeekLifts(1.05f, reps, jokerStartWeight));
        coreDisplay.addView(weightBreakdown());
        jokerStartWeight *= 1.05f;
    }


    private void checkDeloadVisible(){
        if(deloadWeekSettingVal != 0)
            bbbFrame.setVisibility(View.VISIBLE);
        else
            bbbFrame.setVisibility(View.GONE);
    }


    // ---------- Fragments ----------

    private void createAmrapFragment(String lastWeeksReps){
        checkForFragment();
        submitAmrap = SubmitAmrap.newInstance(lastWeeksReps);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_bottom, R.anim.exit_bottom, R.anim.enter_bottom, R.anim.exit_bottom)
                .addToBackStack("@null")
                .replace(R.id.fragment_holder, submitAmrap)
                .commit();
    }


    @Override
    public void closeAmrapFragment() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_bottom, R.anim.exit_bottom, R.anim.enter_bottom, R.anim.exit_bottom)
                .remove(submitAmrap)
                .commit();
        submitAmrap = null;
    }


    @Override
    public void submitAmrapButton() {
        int amrapValue = submitAmrap.getAmrapNumber();
        startRepSubmission(amrapValue);
    }


    private void checkForFragment(){
        if(submitAmrap != null)
            closeAmrapFragment();
    }

    // ---- Buttons, onClicks, & Misc. ----

    private void setHeaderText(String headerCompound, int headerWeight){
        TextView headerText = findViewById(R.id.header_text);
        String displayInfo;
        if(weightCheck == 9){
            displayInfo = headerCompound + ": " + headerWeight + weightSuffix;
        } else {
            displayInfo = headerCompound + ": " + calculateWeight.setAsKilograms(headerWeight, 1.0f) + weightSuffix;
        }

        headerText.setText(displayInfo);
    }


    private void setButtons(){
        amrapButton = findViewById(R.id.submit_amrap);
        homeButton = findViewById(R.id.home_button);
        settingsButton = findViewById(R.id.settings_button);
        jokerButton = findViewById(R.id.add_joker);
        navButtons();
    }


    private void setViews(){
        coreFrame = findViewById(R.id.core_sets_frame);
        bbbFrame = findViewById(R.id.bbb_sets_frame);
        amrapFrame = findViewById(R.id.amrap_frame);

        tabSelected = findViewById(R.id.tab_view_days);

        warmupDisplay = findViewById(R.id.warmup_sets_display);
        coreDisplay = findViewById(R.id.core_sets_display);
        bbbDisplay = findViewById(R.id.bbb_sets_display);

        warmupTitle = findViewById(R.id.warmup_title);

        amrapLastWeek = findViewById(R.id.amrap_last_week_number);
    }


    public void setListeners(){
        tabOnClicks();
        amrapButton();
        setJokerButton();
        setNav();
    }


    private void amrapButton() {
        amrapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAmrapFragment(repsDone);
            }
        });
    }

    private void setJokerButton(){
        jokerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jokerChoose();
            }
        });
    }


    private void navButtons(){
        homeButton = findViewById(R.id.home_button);
        settingsButton = findViewById(R.id.settings_button);
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
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
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


    private void tabOnClicks(){
        tabSelected.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentWeek = tabSelected.getSelectedTabPosition();
                checkForFragment();
                weekSelected(currentWeek);
                if(String.valueOf(tab.getText()).equals("DELOAD")){
                    amrapButton.setEnabled(false);
                    warmupTitle.setText(R.string.deload_text);
                    coreFrame.setVisibility(View.GONE);
                    checkDeloadVisible();
                } else {
                    amrapButton.setEnabled(true);
                    warmupTitle.setText(R.string.warmup_text);
                    coreFrame.setVisibility(View.VISIBLE);
                }
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
        amrapWeight = liftsArrayList.get(0).getTrainingMax();
    }

}
