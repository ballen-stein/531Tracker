package com.a531tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import com.a531tracker.database.DatabaseHelper;
import com.a531tracker.ObjectBuilders.AsManyRepsAsPossible;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ViewAmrapStats extends AppCompatActivity {
    private LineChart[] lineCharts;
    private ScrollView allCharts;
    private Button homeButton, settingsButton, returnButton, saveButton;

    private String savedDirectory;
    private String filename;

    private DatabaseHelper db;
    private ArrayList<AsManyRepsAsPossible> amrapValues;
    private CalculateWeight calculateWeight = new CalculateWeight();

    private int cycleValue;
    private int weightCheck;

    private Context mContext;

    private final String[] compoundLifts = new String[]{"Bench", "Overhand Press", "Squat", "Deadlift"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_amrap_stats);


        db = new DatabaseHelper(this);
        mContext = this;
        cycleValue = db.getCycle();
        savedDirectory = Objects.requireNonNull(getExternalFilesDir(Environment.DIRECTORY_PICTURES)).toString();
        filename = createFileName();

        setViews();
        setButtons();
        setNav();

    }

    @Override
    protected void onStart(){
        super.onStart();
        getSettings();
        try {
            for (int i = 0; i < compoundLifts.length; i++) {
                getLift(compoundLifts[i]);
                setChartTheme(lineCharts[i]);
                setChart(lineCharts[i]);
            }
        } catch (Exception e){
            ErrorAlerts errorAlerts = new ErrorAlerts(mContext);
            errorAlerts.setErrorAlertsValues(false, true, getString(R.string.settings_view_amrap_title), getString(R.string.settings_view_amrap_message), "", false);
            errorAlerts.blankAlert(mContext).setPositiveButton(getString(R.string.ok_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    onBackPressed();
                }
            }).show();
        }
    }

    private void getSettings(){
        weightCheck = Integer.parseInt( String.valueOf( String.valueOf( db.getUserSettings().getChosenBBBFormat()).charAt(0)));
    }


    private void setChart(LineChart lineChart){
        List<ILineDataSet> allData = new ArrayList<>();
        for(int i = 0; i < amrapValues.size(); i++){
            int[] weightPoints = new int[]{Integer.parseInt(calculateWeight.setAsPounds(amrapValues.get(i).getTotalMaxWeight(), 0.85f)),
                    Integer.parseInt(calculateWeight.setAsPounds(amrapValues.get(i).getTotalMaxWeight(), 0.90f)),
                    Integer.parseInt(calculateWeight.setAsPounds(amrapValues.get(i).getTotalMaxWeight(), 0.95f))};

            List<Entry> entries = new ArrayList<>();

            entries.add(new Entry(weightPoints[0], amrapValues.get(i).getEighty_five_reps()));
            entries.add(new Entry(weightPoints[1], amrapValues.get(i).getNinety_reps()));
            entries.add(new Entry(weightPoints[2], amrapValues.get(i).getNinety_five_reps()));

            LineDataSet lineDataSet = new LineDataSet(entries, amrapValues.get(i).getCycle()+": " + amrapValues.get(i).getTotalMaxWeight()+" lbs");
            lineDataSet.setCircleColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
            lineDataSet.setFillColor(ContextCompat.getColor(mContext, R.color.colorDarkBlue));
            lineDataSet.setValueTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            lineDataSet.setValueTextSize(12f);
            lineDataSet.setColor(ContextCompat.getColor(mContext, setGraphLineColors(i)));

            allData.add(lineDataSet);
        }

        LineData lineData = new LineData(allData);
        lineChart.getDescription().setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        lineChart.getDescription().setText(amrapValues.get(0).getCompound() + " Cycles");
        lineChart.setData(lineData);
        lineChart.invalidate();
    }


    private void setChartTheme(LineChart lineChart){
        //Declaration of Left/Right axis
        YAxis right = lineChart.getAxisRight();
        YAxis left = lineChart.getAxisLeft();

        int maxReps = getGraphMinMax(true)+1;
        int minReps = getGraphMinMax(false)-1;

        left.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        left.setGridColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        left.setAxisLineColor((ContextCompat.getColor(mContext, R.color.colorWhite)));
        left.setAxisMinimum(minReps);
        left.setAxisMaximum(maxReps);
        left.setLabelCount((maxReps-minReps+1), true);

        right.setAxisLineColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        right.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        right.setGridColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        right.setAxisMinimum(minReps);
        right.setAxisMaximum(maxReps);
        right.setLabelCount((maxReps-minReps+1), true);

        lineChart.getXAxis().setGridColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        lineChart.getXAxis().setTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));

        int lastCompound = amrapValues.size();
        float weightMin;
        float weightMax;

        if(weightCheck == 9) {
            weightMin  = Integer.parseInt(calculateWeight.setAsPounds(amrapValues.get(0).getTotalMaxWeight(), 0.85f)) - 5;
            weightMax = Integer.parseInt(calculateWeight.setAsPounds(amrapValues.get(lastCompound-1).getTotalMaxWeight(), 1f));
        } else {
            weightMin  = Integer.parseInt(calculateWeight.setAsPounds(amrapValues.get(0).getTotalMaxWeight(), 0.85f)) - 5;
            weightMax = Integer.parseInt(calculateWeight.setAsPounds(amrapValues.get(lastCompound-1).getTotalMaxWeight(), 1f));
        }

        float k = Float.parseFloat((calculateWeight.setAsKilograms(amrapValues.get(0).getTotalMaxWeight(), 0.85f)));
        float j = Float.parseFloat((calculateWeight.setAsKilograms(amrapValues.get(lastCompound-1).getTotalMaxWeight(), 1f)));

        int graphBreaks = 1 + (((int) weightMax - (int) weightMin) / 5);

        lineChart.setBackground(ContextCompat.getDrawable(mContext, R.color.colorBlue));
        lineChart.getLegend().setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        lineChart.getDescription().setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        lineChart.getDescription().setTextSize(10f);

        lineChart.getXAxis().setLabelCount(graphBreaks, true);
        lineChart.getXAxis().setAxisMinimum(weightMin);
        lineChart.getXAxis().setAxisMaximum(weightMax);
    }


    private int setGraphLineColors(int i){
        switch (i%3){
            default:
            case 0:
                return R.color.colorPrimary;
            case 1:
                return R.color.colorOrange;
            case 2:
                return R.color.colorGreen;
        }
    }


    private int getGraphMinMax(boolean max){
        int newMinMax = 0;
        if(!max)
            newMinMax = 100;
        for(int i = 0; i < amrapValues.size(); i++) {
            if (max) {
                if (newMinMax < amrapValues.get(i).getEighty_five_reps())
                    newMinMax = amrapValues.get(i).getEighty_five_reps();
                if (newMinMax < amrapValues.get(i).getNinety_reps())
                    newMinMax = amrapValues.get(i).getNinety_reps();
                if (newMinMax < amrapValues.get(i).getNinety_five_reps())
                    newMinMax = amrapValues.get(i).getNinety_five_reps();
            } else {
                if (newMinMax > amrapValues.get(i).getEighty_five_reps())
                    newMinMax = amrapValues.get(i).getEighty_five_reps();
                if (newMinMax > amrapValues.get(i).getNinety_reps())
                    newMinMax = amrapValues.get(i).getNinety_reps();
                if (newMinMax > amrapValues.get(i).getNinety_five_reps())
                    newMinMax = amrapValues.get(i).getNinety_five_reps();
            }
        }
        return newMinMax;
    }


    private void getLift(String compound){
        amrapValues = new ArrayList<>();
        for(int i = 1; i < cycleValue; i++){
            amrapValues.add(db.getAMRAPValues(compound, i));
        }
    }


    private void setViews(){
        LineChart lineChart1, lineChart2, lineChart3, lineChart4;
        lineChart1 = findViewById(R.id.chart);
        lineChart2 = findViewById(R.id.chart2);
        lineChart3 = findViewById(R.id.chart3);
        lineChart4 = findViewById(R.id.chart4);

        allCharts = findViewById(R.id.all_charts);

        lineCharts = new LineChart[]{lineChart1, lineChart2, lineChart3, lineChart4};
    }


    private void saveStats(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 112);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 112);
            }
        } else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                File dir = new File(savedDirectory);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File newFile = new File(dir, filename);
                if(newFile.exists()){
                    newFile.delete();
                    newFile = new File(dir, filename);
                }
                if(saveToPhone(newFile))
                    Toast.makeText(mContext, "Charts Saved", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(mContext, "Failed to save charts", Toast.LENGTH_LONG).show();
            } else{
                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures");
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File newFile = new File(dir, filename);
                if (newFile.exists()) {
                    newFile.delete();
                    newFile = new File(dir, filename);
                }
                if(saveToPhone(newFile))
                    Toast.makeText(mContext, "Charts Saved", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(mContext, "Failed to save charts", Toast.LENGTH_LONG).show();
            }
        }
    }


    private boolean saveToPhone(File newFile){
        Bitmap bitmap = getAllCharts(allCharts, allCharts.getChildAt(0).getHeight(), allCharts.getChildAt(0).getWidth());
        FileOutputStream fos = null;
        boolean worked = false;
        try {
            fos = new FileOutputStream(newFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                    updatePicturesFolder(mContext, newFile.getPath());
                }
                worked = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return worked;
    }


    private void updatePicturesFolder(Context context,String path) {
        MediaScannerConnection.scanFile(context,
                new String[] { path }, null,null);
    }


    private Bitmap getAllCharts(View view, int height, int width){
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable drawable = view.getBackground();
        if(drawable != null){
            drawable.draw(canvas);
        } else {
            canvas.drawColor(ContextCompat.getColor(mContext, R.color.colorOrange));
        }
        view.draw(canvas);
        return bitmap;
    }

    private String createFileName() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
        return "stats_" + formatter.format(new Date()) + ".png";
    }

    // -------- Buttons & Listeners -----

    private void setButtons(){
        homeButton = findViewById(R.id.home_button);
        settingsButton = findViewById(R.id.settings_button);
        returnButton = findViewById(R.id.settings_back);
        saveButton = findViewById(R.id.save_button);
    }


    private void setNav(){
        navHome();
        navSettings();
        navBack();
        navSave();
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
                onBackPressed();
            }
        });
    }


    private void navBack(){
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void navSave(){
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveStats();
            }
        });
    }
}
