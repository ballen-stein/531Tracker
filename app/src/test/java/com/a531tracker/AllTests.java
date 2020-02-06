package com.a531tracker;

import android.content.Context;

import com.a531tracker.Database.DatabaseHelper;
import com.a531tracker.ObjectBuilders.AccessoryLifts;
import com.a531tracker.ObjectBuilders.CompoundLifts;

import org.junit.After;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

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
    public void testCalcFunction(){
        CalculateWeight calculateWeight = new CalculateWeight();
        assertEquals("2.3", calculateWeight.setAsKilograms(5, 1f));
        assertEquals("4.5", calculateWeight.setAsKilograms(10, 1f));
        assertEquals("11.3", calculateWeight.setAsKilograms(25, 1f));
        assertEquals("15.9", calculateWeight.setAsKilograms(35, 1f));
        assertEquals("20.4", calculateWeight.setAsKilograms(45, 1f));
    }
    @Test
    public void testCalculateWeightNewFeature(){
        CalculateWeight calculateWeight = new CalculateWeight();
        //int weight = 45;
        int weight = 295;
        int weight2 = 0;
        calculateWeight.weightBreakdown(weight, true);
        ArrayList testList = calculateWeight.getWeightArray();
        //assertEquals(45.0, testList.get(0));
        ArrayList<Double> newList = new ArrayList<>();
        newList.add(45.0);
        newList.add(45.0);
        newList.add(45.0);
        newList.add(5.0);
        //assertEquals(newList, testList);
        assertEquals(2, calculateWeight.weightBreakdown(weight2, true));
    }
    @After
    public void finish(){

    }
}
