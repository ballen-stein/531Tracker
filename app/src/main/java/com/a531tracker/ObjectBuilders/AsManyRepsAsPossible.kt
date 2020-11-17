package com.a531tracker.ObjectBuilders

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

    fun print(): String {
        return "Compound : $compound, Cycle : $cycle, TotalMax : $totalMaxWeight, Eighty-Five : $eighty_five_reps, Ninety : $ninety_reps, Ninety-Five : $ninety_five_reps"
    }
}