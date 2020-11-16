package com.a531tracker.tools

class AppConstants {
    companion object {
        internal const val VIEW_BASE_NAVIGATION = 101
        internal const val SET_TRAINING_MAX_CODE = 102
        internal const val UPDATE_TRAINING_MAX_CODE = 103
        internal const val INPUT_WEEKLY_LIFT = 104
        internal const val INFO_URL = "more_info_url"
        internal const val FRESH_LAUNCH = "fresh_launch"

        internal const val NAVIGATION_MENU = 201
        internal const val NAVIGATION_WEEK = 202
        internal const val NAVIGATION_AMRAP = 203
        internal const val NAVIGATION_TOOL_CYCLE = 204
        internal const val NAVIGATION_TOOL_PERCENT = 205
        internal const val NAVIGATION_TOOL_TM = 206

        internal const val JW_URL = "https://jimwendler.com/blogs/jimwendler-com/101077382-boring-but-big"

        internal const val AMRAP_LAST_WEEK = "last_week_amrap"
        internal const val CURRENT_WEEK_DISPLAYED = "current_week"
        internal const val DIALOG_MENU = "view_menu"
        internal const val JOKER_REPS = "added_joker"
        internal const val NEW_USER_ONBOARD = "new_user_onboarding_step"

        //  For BottomDialogTool
        internal const val DIALOG_BENCH = "bench_value"
        internal const val DIALOG_SQUAT = "squat_value"
        internal const val DIALOG_DL = "dl_value"
        internal const val DIALOG_OHP = "ohp_value"
        internal const val DIALOG_CYCLE = "cycle_value"
        //

        internal const val WEEK_BENCH = "bench_press"
        internal const val WEEK_SQUAT = "back_squat"
        internal const val WEEK_DEADLIFT = "deadlift"
        internal const val WEEK_OVERHAND = "overhand_press"

        internal const val BENCH = "Bench"
        internal const val SQUAT = "Squat"
        internal const val DEADLIFT = "Deadlift"
        internal const val OVERHAND = "Overhand Press"

        internal const val MAIN_LIFT = "main_lift"
        internal const val COMPOUND_STRING = "compound"
        internal const val CYCLE_NUM = "cycle_num"
        internal const val SWAP_LIFT = "swap_lift"

        internal val LIFT_ACCESS_LIST: MutableList<String> =
                mutableListOf(
                        "Bench",
                        "Squat",
                        "Overhand Press",
                        "Deadlift"
                )
        internal val LIFT_ACCESS_MAP: HashMap<String, String> =
                hashMapOf(
                        BENCH to WEEK_BENCH,
                        SQUAT to WEEK_SQUAT,
                        OVERHAND to WEEK_OVERHAND,
                        DEADLIFT to WEEK_DEADLIFT
                )

        internal val LIFT_NAMES_MAP: HashMap<String, String> =
                hashMapOf(
                        "Bench" to "Bench",
                        "Squat" to "Squat",
                        "Overhand Press" to "Overhand Press",
                        "Deadlift" to "Deadlift"
                )

        internal val LIFT_NAMES_REVERSE_MAP: HashMap<String, String> =
                hashMapOf(
                        WEEK_BENCH to "Bench",
                        WEEK_SQUAT to "Squat",
                        WEEK_OVERHAND to "Overhand Press",
                        WEEK_DEADLIFT to "Deadlift"
                )

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

        internal const val SET_WARMUP = "Warm-Up Sets"
        internal const val SET_CORE = "Core Sets"
        internal const val SET_BBB = "\"Boring But Big\" Sets"
        internal const val SET_DELOAD = "Deload Sets"
        internal const val SET_FSL = "First Set Last (FSL)"
    }
}