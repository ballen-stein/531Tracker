package com.a531tracker.ObjectBuilders

import android.util.Log

class AsManyRepsAsPossible {
    var compound: String? = null
    var cycle = 0
    var totalMaxWeight: Int = 0
    var eighty_five_reps: Int? = 0
    var ninety_reps: Int? = 0
    var ninety_five_reps: Int? = 0

    fun compare(amrapCompareObject: AsManyRepsAsPossible): Boolean {
        return !(this.compound != amrapCompareObject.compound
                || this.cycle != amrapCompareObject.cycle
                || this.totalMaxWeight != amrapCompareObject.totalMaxWeight
                || this.eighty_five_reps != amrapCompareObject.eighty_five_reps
                || this.ninety_reps != amrapCompareObject.ninety_reps
                || this.ninety_five_reps != amrapCompareObject.ninety_five_reps)
    }

    fun print() {
        Log.d("TestingData", "Data is as follows:\n" +
                "Compound : $compound\n" +
                "Cycle : $cycle\n" +
                "TotalMax : $totalMaxWeight\n" +
                "Eighty-Five : $eighty_five_reps\n" +
                "Ninety : $ninety_reps\n" +
                "Ninety-Five : $ninety_five_reps")
    }
}