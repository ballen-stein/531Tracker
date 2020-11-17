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