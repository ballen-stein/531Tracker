package com.a531tracker.ObjectBuilders

import com.a531tracker.tools.AppUtils

data class LiftBuilder (val benchTm: Int,
                        val squatTm: Int,
                        val dlTm: Int,
                        val ohpTm: Int,
                        val percent: Float,
                        val usingTm: Boolean
) {
    private lateinit var mappedValues: HashMap<String, Int>

    init {
        mapLiftValues()
    }

    private fun mapLiftValues() {
        mappedValues = hashMapOf(
            "Bench" to if (!usingTm) (benchTm*.90).toInt() else benchTm,
            "Squat" to if (!usingTm) (squatTm*.90).toInt() else squatTm,
            "Deadlift" to if (!usingTm) (dlTm*.90).toInt() else dlTm,
            "Overhand Press" to if (!usingTm) (ohpTm*.90).toInt() else ohpTm,
        )
    }

    fun getMappedValue(liftName: String): Int {
        return mappedValues[liftName] ?: 100
    }

    fun getFixedPercent() : Float {
        return AppUtils().normalizePercent(percent)/100F
    }

    fun isFilled(): Boolean {
        return benchTm > 0 && squatTm > 0 && dlTm > 0 && ohpTm > 0
    }
}