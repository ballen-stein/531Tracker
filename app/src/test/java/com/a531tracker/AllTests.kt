package com.a531tracker

import com.a531tracker.ObjectBuilders.CompoundLifts
import com.a531tracker.tools.AppUtils
import org.junit.Assert
import org.junit.Test
import java.util.ArrayList

class AllTests {
    var lifts = CompoundLifts()
    @Test
    fun createLifts() {
        lifts.compound = "Bench"
        Assert.assertEquals("Bench", lifts.compound)
    }

    @Test
    fun testNinetyReps() {
        lifts.ninetyReps = 5
        lifts.ninetyFiveReps = 4
        Assert.assertEquals(5f, lifts.ninetyReps!!.toFloat(), 0f)
        Assert.assertEquals(4f, lifts.ninetyFiveReps!!.toFloat(), 0f)
    }

    @Test
    fun testEightyReps() {
        lifts.eightFiveReps = 10
        Assert.assertEquals(10f, lifts.eightFiveReps!!.toFloat(), 0f)
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