package com.a531tracker.tools

class AppConstants {
    companion object {
        internal val SET_TRAINING_MAX_CODE = 1
        internal val UPDATE_TRAINING_MAX_CODE = 2

        internal val warmupPercents = arrayListOf(0.40f, 0.50f, 0.60f)
        internal val corePercentsBase = arrayOf(
                arrayListOf<Float>(0.75f, 0.80f, 0.85f),
                arrayListOf(0.75f, 0.85f, 0.90f),
                arrayListOf(0.80f, 0.90f, 0.95f)
        )

        internal val corePercentsMod = arrayOf(
                arrayListOf<Float>(0.65f, 0.75f, 0.80f),
                arrayListOf(0.70f, 0.80f, 0.85f),
                arrayListOf(0.75f, 0.85f, 0.90f)
        )

        internal val boringPercents = arrayListOf<Float>()

        internal val deloadBoring = arrayListOf<Float>()

        internal const val COMPOUND_STRING = "compound"
        internal const val CYCLE_NUM = "cycle"
        internal const val SWAP_LIFT = "swap"

        internal const val SET_WARMUP = "Warm-Up Sets"
        internal const val SET_CORE = "Core Sets"
        internal const val SET_BBB = "\"Boring But Big\" Sets"
        internal const val SET_DELOAD = "Deload Sets"
    }
}