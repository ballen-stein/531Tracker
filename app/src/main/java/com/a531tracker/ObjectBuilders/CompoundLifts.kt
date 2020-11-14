package com.a531tracker.ObjectBuilders

import android.util.Log

class CompoundLifts {
    var compound: String? = null
    var trainingMax: Int? = 0
    var eightFiveReps: Int? = 5
    var ninetyReps: Int? = 3
    var ninetyFiveReps: Int? = 1
    var percent: Float? = null

    fun fromLiftBuilder(liftBuilder: LiftBuilder, liftName: String) {
        compound = liftName
        trainingMax = liftBuilder.getMappedValue(liftName = liftName)
        percent = liftBuilder.getFixedPercent()

    }

    override fun toString(): String {
        return "Compound $compound, " +
                "Training Max $trainingMax," +
                "Eighty Five Reps $eightFiveReps," +
                "Ninety Reps $ninetyReps," +
                "Ninety Five Reps $ninetyFiveReps," +
                "Boring Percent $percent"
    }
}