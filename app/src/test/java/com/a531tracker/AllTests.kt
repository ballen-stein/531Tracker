package com.a531tracker

import com.a531tracker.ObjectBuilders.CompoundLifts
import com.a531tracker.ObjectBuilders.AccessoryLifts
import com.a531tracker.tools.AppUtils
import org.junit.Assert
import org.junit.Test
import java.util.ArrayList

class AllTests {
    var lifts = CompoundLifts()
    var accessoryLifts = AccessoryLifts()
    @Test
    fun createLifts() {
        lifts.compound_movement = "Bench"
        Assert.assertEquals("Bench", lifts.compound_movement)
    }

    @Test
    fun testNinetyReps() {
        lifts.ninety_reps = 5
        lifts.ninety_five_reps = 4
        Assert.assertEquals(5f, lifts.ninety_reps.toFloat(), 0f)
        Assert.assertEquals(4f, lifts.ninety_five_reps.toFloat(), 0f)
    }

    @Test
    fun testEightyReps() {
        lifts.eight_five_reps = 10
        Assert.assertEquals(10f, lifts.eight_five_reps.toFloat(), 0f)
    }

    @Test
    fun createAccessory() {
        accessoryLifts.accessory_name = "Face-Pulls"
        Assert.assertEquals("Face-Pulls", accessoryLifts.accessory_name)
    }

    @Test
    fun checkAllAccessories() {
        accessoryLifts.accessory_reps_and_sets = "5x20"
        Assert.assertEquals("5x20", accessoryLifts.accessory_reps_and_sets)
        accessoryLifts.accessory_weight = 15f
        Assert.assertEquals(15f, accessoryLifts.accessory_weight, 0f)
    }

    @Test
    fun testCalcFunction() {
        val calculateWeight = CalculateWeight()
        Assert.assertEquals("2.3", calculateWeight.setAsKilograms(5, 1f))
        Assert.assertEquals("4.5", calculateWeight.setAsKilograms(10, 1f))
        Assert.assertEquals("11.3", calculateWeight.setAsKilograms(25, 1f))
        Assert.assertEquals("15.9", calculateWeight.setAsKilograms(35, 1f))
        Assert.assertEquals("20.4", calculateWeight.setAsKilograms(45, 1f))
    }

    @Test
    fun testCalculateWeightNewFeature() {
        val calculateWeight = CalculateWeight()
        //int weight = 45;
        val weight = 295
        val weight2 = 0
        calculateWeight.weightBreakdown(weight.toDouble(), true)
        val testList = calculateWeight.weightArray
        //assertEquals(45.0, testList.get(0));
        val newList = ArrayList<Double>()
        newList.add(45.0)
        newList.add(45.0)
        newList.add(45.0)
        newList.add(5.0)
        //assertEquals(newList, testList);
        Assert.assertEquals(2, calculateWeight.weightBreakdown(weight2.toDouble(), true).toLong())
    }

    @Test
    fun `get list of weights to put on the bar` () {
        val weight1 = 395.00
        val weight2 = 100.00

        val mathUtil = AppUtils()
        //mathUtil.setBaseWeight()
        mathUtil.calculateWeightBreakdown(weight = weight1, true)
        //val weight2Breakdown = mathUtil.calculateWeightBreakdown(weight2, false)

        Assert.assertEquals(mathUtil.getWeightArray(), listOf(45.0,45.0,45.0,45.0,35.0,5.0))
    }
}