package com.a531tracker.ObjectBuilders

import com.a531tracker.tools.AppUtils

data class GraphDataHolder(
        var trainingMax: Float,
        var tmWeekOne: Float,
        var tmWeekTwo: Float,
        var tmWeekThree: Float,
        var compoundName: String
) {

    fun getFromAmrap(amrap: AsManyRepsAsPossible, altFormat: Boolean) {
        this.trainingMax = amrap.totalMaxWeight.toFloat()
        this.tmWeekOne = if (altFormat) {
            AppUtils().getGraphWeight(amrap.totalMaxWeight, .80F, true)
        } else {
            AppUtils().getGraphWeight(amrap.totalMaxWeight, .85F, true)
        }
        this.tmWeekTwo = if (altFormat) {
            AppUtils().getGraphWeight(amrap.totalMaxWeight, .85F, true)
        } else {
            AppUtils().getGraphWeight(amrap.totalMaxWeight, .90F, true)
        }
        this.tmWeekThree = if (altFormat) {
            AppUtils().getGraphWeight(amrap.totalMaxWeight, .90F, true)
        } else {
            AppUtils().getGraphWeight(amrap.totalMaxWeight, .95F, true)
        }
        this.compoundName = amrap.compound ?: ""
    }
}
