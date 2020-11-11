package com.a531tracker.tools

import java.math.BigDecimal
import java.util.*
import kotlin.math.ceil

class AppUtils {

    fun getInstance(): AppUtils {
        return AppUtils()
    }

    fun getWeight(value: Int, trainingValue: Int, liftPercent: Float): String {
        // Pounds == 0 | Kilograms == 1
        return if (value == 0) {
            ((5 * ceil(trainingValue * liftPercent / 5.toDouble())).toInt()).toString()
        } else {
            BigDecimal(trainingValue * liftPercent / 2.20462).setScale(1, BigDecimal.ROUND_HALF_UP).toString()
        }
    }

    private var weightArray = ArrayList<Double>()

    fun calculateWeightBreakdown(weight: Double, firstRun: Boolean): Int {
        val fixedWeight: Double = if (firstRun) {
            if (weight < 45) {
                return 0
            } else {
                (weight - 45) / 2
            }
        } else {
            weight
        }
        if (fixedWeight - 45 >= 0) {
            if (checkIfZero(fixedWeight, 45.0)) setWeightArray(45) else {
                setWeightArray(45)
                calculateWeightBreakdown(fixedWeight - 45, false)
            }
        } else if (fixedWeight - 35 >= 0) {
            if (checkIfZero(fixedWeight, 35.0)) setWeightArray(35) else {
                setWeightArray(35)
                calculateWeightBreakdown(fixedWeight - 35, false)
            }
        } else if (fixedWeight - 25 >= 0) {
            if (checkIfZero(fixedWeight, 25.0)) {
                setWeightArray(25)
            } else {
                setWeightArray(25)
                calculateWeightBreakdown(fixedWeight - 25, false)
            }
        } else if (fixedWeight - 10 >= 0) {
            if (checkIfZero(fixedWeight, 10.0)) setWeightArray(10) else {
                setWeightArray(10)
                calculateWeightBreakdown(fixedWeight - 10, false)
            }
        } else if (fixedWeight - 5 >= 0) {
            if (checkIfZero(fixedWeight, 5.0)) setWeightArray(5) else {
                setWeightArray(5)
                calculateWeightBreakdown(fixedWeight - 5, false)
            }
        } else if (fixedWeight - 2.5 >= 0) {
            setWeightArray(1)
        } else {
            return 1
        }
        return 1
    }

    private fun checkIfZero(currVal: Double, returnVal: Double): Boolean {
        return currVal - returnVal == 0.0
    }

    private fun setWeightArray(weight: Int) {
        when (weight) {
            45 -> weightArray.add(45.0)
            35 -> weightArray.add(35.0)
            25 -> weightArray.add(25.0)
            10 -> weightArray.add(10.0)
            5 -> weightArray.add(5.0)
            else -> weightArray.add(2.5)
        }
    }

    fun getWeightArray(): ArrayList<Double>? {
        return weightArray
    }

    fun resetWeightArray() {
        weightArray = ArrayList()
        weightArray.add(45.0)
    }
}