package com.a531tracker;

import android.content.Context;

import com.a531tracker.Database.DatabaseHelper;
import com.a531tracker.LiftBuilders.AccessoryLifts;
import com.a531tracker.LiftBuilders.CompoundLifts;

import org.junit.After;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class AllTests {
    CompoundLifts lifts = new CompoundLifts();
    AccessoryLifts accessoryLifts = new AccessoryLifts();

    Context mContext;
    DatabaseHelper db;


    @Test
    public void createLifts(){
        lifts.setCompound_movement("Bench");
        assertEquals("Bench", lifts.getCompound_movement());
    }
    @Test
    public void testMax(){
        lifts.setTraining_max((float) 170);
        assertEquals((float)175, lifts.getTraining_max(), 5);
    }
    @Test
    public void testNinetyReps(){
        lifts.setNinety_reps(5);
        lifts.setNinety_five_reps(4);
        assertEquals(5, lifts.getNinety_reps(), 0);
        assertEquals(4, lifts.getNinety_five_reps(),0);
    }
    @Test
    public void testEightyReps(){
        lifts.setEight_five_reps(10);
        assertEquals(10, lifts.getEight_five_reps(), 0);
    }
    @Test
    public void createAccessory(){
        accessoryLifts.setAccessory_name("Face-Pulls");
        assertEquals("Face-Pulls", accessoryLifts.getAccessory_name());
    }
    @Test
    public void checkAllAccessories(){
        accessoryLifts.setAccessory_reps_and_sets("5x20");
        assertEquals("5x20", accessoryLifts.getAccessory_reps_and_sets());
        accessoryLifts.setAccessory_weight(15);
        assertEquals(15, accessoryLifts.getAccessory_weight(),0);
    }


    @Test
    public void createDataBase(){
        db = new DatabaseHelper(mContext);
        //DatabaseHelper db = new DatabaseHelper(mContext);
        lifts.setCompound_movement("Bench");
        lifts.setTraining_max((float) 170);
        lifts.setNinety_five_reps(5);
        lifts.setNinety_reps(8);
        lifts.setEight_five_reps(12);
        //assertEquals(lifts, db.createLiftsTest());
        //db.insertCompoundStats(lifts);
        //assertTrue(db.getLiftNameTest("Bench"));
    }
    @After
    public void finish(){

    }
}
