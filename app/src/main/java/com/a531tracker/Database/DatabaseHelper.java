package com.a531tracker.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.a531tracker.LiftBuilders.AsManyRepsAsPossible;
import com.a531tracker.LiftBuilders.CompoundLifts;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "531.db";
    private static final int DATABASE_VERSION = 12;

    //  Compounds
    private static final String WORKOUT_COMPOUND_TABLE_NAME = "compound_exercise_list";
    private static final String WORKOUT_COMPOUND_ROW_ID = "id";
    private static final String WORKOUT_COMPOUND_MOVEMENT = "compound_movement";

    //  ----    Breakdowns  ----

    //  Main Work Numbers
    private static final String WORKOUT_COMPOUND_85_REPS = "eight_five_reps";
    private static final String WORKOUT_COMPOUND_90_REPS = "nintey_reps";
    private static final String WORKOUT_COMPOUND_95_REPS = "ninety_five_reps";
    private static final String WORKOUT_COMPOUND_BBB_WEIGHT = "big_but_boring";
    private static final String WORKOUT_COMPOUND_MAX = "total_max";

    //  ----    As Many Reps As Possible Tables  ----

    //  AMRAP
    private static final String WORKOUT_AS_MANY_REPS_AS_POSSIBLE_CYCLE = "cycle";
    private static final String WORKOUT_AS_MANY_REPS_AS_POSSIBLE_85 = "amrap_85";
    private static final String WORKOUT_AS_MANY_REPS_AS_POSSIBLE_90 = "amrap_90";
    private static final String WORKOUT_AS_MANY_REPS_AS_POSSIBLE_95 = "amrap_95";
    private static final String WORKOUT_AS_MANY_REPS_AS_POSSIBLE_WEIGHT = "weight";

    //  Compound names
    private static final String WORKOUT_COMPOUND_SQUAT = "Squat";
    private static final String WORKOUT_COMPOUND_BENCH = "Bench";
    private static final String WORKOUT_COMPOUND_DEADLIFT = "Deadlift";
    private static final String WORKOUT_COMPOUND_PRESS = "Press";


    //  Cycle Tracker
    private static final String WORKOUT_CYCLE_TABLE = "cycle_table";
    private static final String WORKOUT_CYCLE_NUMBER = "cycle_num";
    private static final String WORKOUT_BBB_SWAPS = "bbb_swap";

    private static final String WORKOUT_SETTINGS_TABLE = "settings";
    private static final String WORKOUT_BBB_SEVEN_DAY = "seven_dau";
    private static final String WORKOUT_FORMAT_CHOSEN = "chosen_format";

    //  ----    Accessories Tables  ----

    //  Accessories
    private static final String WORKOUT_ACCESSORY_TABLE_NAME = "accessory_exercise_list";
    private static final String WORKOUT_ACCESSORY_ROW_ID = "id";
    private static final String WORKOUT_ACCESSORY_NAME = "accessory_name";
    private static final String WORKOUT_ACCESSORY_REPS_AND_SETS = "accessory_reps_and_sets";
    private static final String WORKOUT_ACCESSORY_WEIGHT = "accessory_weight";


    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Core Sets and Weight table
        db.execSQL("create table if not exists " + WORKOUT_COMPOUND_TABLE_NAME + " (" +
                WORKOUT_COMPOUND_ROW_ID + " integer primary key, " +
                WORKOUT_COMPOUND_MOVEMENT + " text, " +
                WORKOUT_COMPOUND_MAX + " integer, " +
                WORKOUT_COMPOUND_85_REPS + " integer, " +
                WORKOUT_COMPOUND_90_REPS + " integer, " +
                WORKOUT_COMPOUND_95_REPS + " integer, " +
                WORKOUT_COMPOUND_BBB_WEIGHT + " float)"
        );

        // AMRAP Squat Table
        db.execSQL("create table if not exists " + WORKOUT_COMPOUND_SQUAT + " (" +
                WORKOUT_AS_MANY_REPS_AS_POSSIBLE_CYCLE + " integer primary key, " +
                WORKOUT_AS_MANY_REPS_AS_POSSIBLE_85 + " integer, " +
                WORKOUT_AS_MANY_REPS_AS_POSSIBLE_90 + " integer, " +
                WORKOUT_AS_MANY_REPS_AS_POSSIBLE_95 + " integer, "+
                WORKOUT_AS_MANY_REPS_AS_POSSIBLE_WEIGHT + " text)"
        );

        // AMRAP Bench Table
        db.execSQL("create table if not exists " + WORKOUT_COMPOUND_BENCH + " (" +
                WORKOUT_AS_MANY_REPS_AS_POSSIBLE_CYCLE + " integer primary key, " +
                WORKOUT_AS_MANY_REPS_AS_POSSIBLE_85 + " integer, " +
                WORKOUT_AS_MANY_REPS_AS_POSSIBLE_90 + " integer, " +
                WORKOUT_AS_MANY_REPS_AS_POSSIBLE_95 + " integer, "+
                WORKOUT_AS_MANY_REPS_AS_POSSIBLE_WEIGHT + " text)"
        );

        // AMRAP Deadlift Table
        db.execSQL("create table if not exists " + WORKOUT_COMPOUND_DEADLIFT + " (" +
                WORKOUT_AS_MANY_REPS_AS_POSSIBLE_CYCLE + " integer primary key, " +
                WORKOUT_AS_MANY_REPS_AS_POSSIBLE_85 + " integer, " +
                WORKOUT_AS_MANY_REPS_AS_POSSIBLE_90 + " integer, " +
                WORKOUT_AS_MANY_REPS_AS_POSSIBLE_95 + " integer, "+
                WORKOUT_AS_MANY_REPS_AS_POSSIBLE_WEIGHT + " text)"
        );

        // AMRAP Overhand Press Table
        db.execSQL("create table if not exists " + WORKOUT_COMPOUND_PRESS + " (" +
                WORKOUT_AS_MANY_REPS_AS_POSSIBLE_CYCLE + " integer primary key, " +
                WORKOUT_AS_MANY_REPS_AS_POSSIBLE_85 + " integer, " +
                WORKOUT_AS_MANY_REPS_AS_POSSIBLE_90 + " integer, " +
                WORKOUT_AS_MANY_REPS_AS_POSSIBLE_95 + " integer, " +
                WORKOUT_AS_MANY_REPS_AS_POSSIBLE_WEIGHT + " text)"
        );

        // Track Cycle Table
        db.execSQL("create table if not exists " + WORKOUT_CYCLE_TABLE + " (" +
                WORKOUT_CYCLE_NUMBER + " integer, " +
                WORKOUT_BBB_SWAPS + " integer)"
        );

        db.execSQL("create table if not exists " +  WORKOUT_SETTINGS_TABLE + " (" +
                WORKOUT_BBB_SWAPS + " integer, " +
                WORKOUT_FORMAT_CHOSEN + " integer, "+
                WORKOUT_BBB_SEVEN_DAY + " integer)"
        );

        // Accessory Table
        db.execSQL("create table if not exists " + WORKOUT_ACCESSORY_TABLE_NAME + " (" +
                WORKOUT_ACCESSORY_ROW_ID + " integer primary key, " +
                WORKOUT_ACCESSORY_NAME + " text, " +
                WORKOUT_ACCESSORY_REPS_AND_SETS + " text, " +
                WORKOUT_ACCESSORY_WEIGHT + " float)"
        );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        if(DATABASE_VERSION > db.getVersion()) {
            Log.d("Upgrade", "Upgrading SQL database");
            db.execSQL("drop table if exists " + WORKOUT_COMPOUND_TABLE_NAME);
            db.execSQL("drop table if exists " + WORKOUT_ACCESSORY_TABLE_NAME);
            //db.execSQL("drop table if exists " + WORKOUT_COMPOUND_SQUAT);
            //db.execSQL("drop table if exists " + WORKOUT_COMPOUND_BENCH);
            //db.execSQL("drop table if exists " + WORKOUT_COMPOUND_DEADLIFT);
            //db.execSQL("drop table if exists " + WORKOUT_COMPOUND_PRESS);
            onCreate(db);
        }
    }


    // -------- Compound SQL --------


    public void insertCompoundStats(CompoundLifts lifts){
        if(!checkLiftExists(lifts.getCompound_movement())){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(WORKOUT_COMPOUND_MOVEMENT, lifts.getCompound_movement());
            contentValues.put(WORKOUT_COMPOUND_MAX, lifts.getTraining_max());
            contentValues.put(WORKOUT_COMPOUND_85_REPS, lifts.getEight_five_reps());
            contentValues.put(WORKOUT_COMPOUND_90_REPS, lifts.getNinety_reps());
            contentValues.put(WORKOUT_COMPOUND_95_REPS, lifts.getNinety_five_reps());
            contentValues.put(WORKOUT_COMPOUND_BBB_WEIGHT, lifts.getBig_but_boring_weight());
            Log.d("Compound_value", lifts.getCompound_movement());
            try {
                db.insert(WORKOUT_COMPOUND_TABLE_NAME, null, contentValues);
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            Log.e("SQL_DB_ERROR", lifts.getCompound_movement() + " was found");
        }
    }


    private boolean checkLiftExists(String movement){
        movement = DatabaseUtils.sqlEscapeString(movement);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + WORKOUT_COMPOUND_MOVEMENT +
                " from " + WORKOUT_COMPOUND_TABLE_NAME +
                " where " + WORKOUT_COMPOUND_MOVEMENT + " = " + movement + "",
                null);
        cursor.moveToFirst();
        int result = cursor.getCount();
        cursor.close();
        db.close();
        return 0 < result;
    }


    public void updateCompoundStats(CompoundLifts lifts){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WORKOUT_COMPOUND_MAX, lifts.getTraining_max());
        int i = db.update(WORKOUT_COMPOUND_TABLE_NAME, contentValues, WORKOUT_COMPOUND_MOVEMENT + " = '" + lifts.getCompound_movement() + "'", null);
        Log.d("Did_it_Work", ""+i);
        db.close();
    }


    public CompoundLifts getLifts(String lift){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + WORKOUT_COMPOUND_TABLE_NAME + " where " +WORKOUT_COMPOUND_MOVEMENT + " = '" + lift + "'", null);
        cursor.moveToFirst();
        CompoundLifts newLifts = buildLift(cursor);
        cursor.close();
        db.close();
        return newLifts;
    }


    private CompoundLifts buildLift(Cursor cursor){
        CompoundLifts lifts = new CompoundLifts();
        lifts.setCompound_movement(cursor.getString(cursor.getColumnIndex(WORKOUT_COMPOUND_MOVEMENT)));
        lifts.setTraining_max(cursor.getInt(cursor.getColumnIndex(WORKOUT_COMPOUND_MAX)));
        lifts.setEight_five_reps(cursor.getInt(cursor.getColumnIndex(WORKOUT_COMPOUND_85_REPS)));
        lifts.setNinety_reps(cursor.getInt(cursor.getColumnIndex(WORKOUT_COMPOUND_90_REPS)));
        lifts.setNinety_five_reps(cursor.getInt(cursor.getColumnIndex(WORKOUT_COMPOUND_95_REPS)));
        lifts.setBig_but_boring_weight(cursor.getFloat(cursor.getColumnIndex(WORKOUT_COMPOUND_BBB_WEIGHT)));
        return lifts;
    }


    // -------- AMRAP SQL --------


    public void createAMRAPTable(int cycle, String compound){
        if(!checkAMRAPExists(findAMRAPTable(compound), cycle)) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(WORKOUT_AS_MANY_REPS_AS_POSSIBLE_CYCLE, cycle);
            try {
                db.insert(findAMRAPTable(compound), null, contentValues);
                Log.d("SQ_DB", "Table created for AMRAP!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private boolean checkAMRAPExists(String tableName, int cycle){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + tableName + " where " + WORKOUT_AS_MANY_REPS_AS_POSSIBLE_CYCLE + " = '" + cycle + "'", null);
        cursor.moveToFirst();
        int result = cursor.getCount();
        cursor.close();
        db.close();
        return result > 0;
    }


    public int updateAMRAPTable(String compound, int cycle, String amrapPercent, int reps, int weight){
        SQLiteDatabase db = this.getWritableDatabase();

        Log.d("Cycle_value_in_update", cycle+"");
        ContentValues contentValues = new ContentValues();
        String AMRAP = findAMRAP(amrapPercent);
        contentValues.put(AMRAP, String.valueOf(reps));
        contentValues.put("weight", String.valueOf(weight));

        int i = db.update(findAMRAPTable(compound), contentValues, WORKOUT_AS_MANY_REPS_AS_POSSIBLE_CYCLE + " = " +cycle, null);
        Log.d("Did_it_Work", i + "");
        db.close();
        return i;
    }


    private String findAMRAP(String amrap){
        String newAmrap;
        switch(amrap){
            default:
            case "0.85":
                newAmrap = WORKOUT_AS_MANY_REPS_AS_POSSIBLE_85;
                break;
            case "0.9":
                newAmrap = WORKOUT_AS_MANY_REPS_AS_POSSIBLE_90;
                break;
            case "0.95":
                newAmrap = WORKOUT_AS_MANY_REPS_AS_POSSIBLE_95;
                break;
        }
        return newAmrap;
    }


    private String findAMRAPTable(String compound){
        String compoundTable;
        switch(compound){
            case "Squat":
                compoundTable = WORKOUT_COMPOUND_SQUAT;
                break;
            case "Overhand Press":
                compoundTable = WORKOUT_COMPOUND_PRESS;
                break;
            default:
            case "Bench Press":
                compoundTable = WORKOUT_COMPOUND_BENCH;
                break;
            case "Deadlift":
                compoundTable = WORKOUT_COMPOUND_DEADLIFT;
                break;
        }
        return compoundTable;
    }


    public AsManyRepsAsPossible getAMRAPValues(String compound, int cycle){
        SQLiteDatabase db = this.getReadableDatabase();
        String compoundTable = findAMRAPTable(compound);
        Cursor cursor = db.rawQuery("select * from " + compoundTable + " where " +WORKOUT_AS_MANY_REPS_AS_POSSIBLE_CYCLE + " = '" + cycle + "'", null);
        cursor.moveToNext();
        AsManyRepsAsPossible amrap = buildAMRAP(cursor, compound, cycle);
        cursor.close();
        db.close();
        return amrap;
    }


    private AsManyRepsAsPossible buildAMRAP(Cursor cursor, String compound, int cycle){
        AsManyRepsAsPossible asManyRepsAsPossible = new AsManyRepsAsPossible();
        asManyRepsAsPossible.setCompound(compound);
        asManyRepsAsPossible.setCycle(cycle);
        asManyRepsAsPossible.setEighty_five_reps(Integer.parseInt(cursor.getString(cursor.getColumnIndex(WORKOUT_AS_MANY_REPS_AS_POSSIBLE_85))));
        asManyRepsAsPossible.setNinety_reps(Integer.parseInt(cursor.getString(cursor.getColumnIndex(WORKOUT_AS_MANY_REPS_AS_POSSIBLE_90))));
        asManyRepsAsPossible.setNinety_five_reps(Integer.parseInt(cursor.getString(cursor.getColumnIndex(WORKOUT_AS_MANY_REPS_AS_POSSIBLE_95))));
        asManyRepsAsPossible.setTotalMaxWeight(Integer.parseInt(cursor.getString(cursor.getColumnIndex(WORKOUT_AS_MANY_REPS_AS_POSSIBLE_WEIGHT))));
        return asManyRepsAsPossible;
    }


    // -------- Cycle SQL --------


    public boolean startCycle(){
        if (!checkForCycle()) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(WORKOUT_CYCLE_NUMBER, 1);
            contentValues.put(WORKOUT_BBB_SWAPS, 1);
            try{
                db.insert(WORKOUT_CYCLE_TABLE, null, contentValues);
                return true;
            } catch (Exception e){
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
    }


    private boolean checkForCycle(){
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from " + WORKOUT_CYCLE_TABLE, null);
            cursor.moveToFirst();
            int result = cursor.getCount();
            cursor.close();
            db.close();
            return result >= 0;
        } catch (Exception e){
            return false;
        }
    }


    public int updateCycle(int oldVal){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        int newCycle = oldVal + 1;
        contentValues.put(WORKOUT_CYCLE_NUMBER, newCycle);
        int i = db.update(WORKOUT_CYCLE_TABLE, contentValues, " cycle_num='"+oldVal+"'", null);
        db.close();
        return i;
    }


    public int getCycle(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + WORKOUT_CYCLE_TABLE, null);
        cursor.moveToFirst();
        int cycle = cursor.getInt(cursor.getColumnIndex(WORKOUT_CYCLE_NUMBER));
        cursor.close();
        db.close();
        return cycle;
    }


    // -------- User Settings SQL --------


    public void swapBBBWorkouts(int oldVal, int newVal){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WORKOUT_BBB_SWAPS, newVal);
        db.update(WORKOUT_CYCLE_TABLE, contentValues, WORKOUT_BBB_SWAPS + " = '" + oldVal + "'", null);
    }


    public int updateBBBWeight(CompoundLifts lifts){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WORKOUT_COMPOUND_BBB_WEIGHT, lifts.getBig_but_boring_weight());
        int i = db.update(WORKOUT_COMPOUND_TABLE_NAME, contentValues, WORKOUT_COMPOUND_MOVEMENT + " = '" + lifts.getCompound_movement() + "'", null);
        Log.d("Did_it_Work", ""+i);
        db.close();
        return i;
    }


    public void updateWorkoutFormat(){

    }

}
