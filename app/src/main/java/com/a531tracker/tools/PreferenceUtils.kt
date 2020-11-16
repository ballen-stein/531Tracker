package com.a531tracker.tools

import android.content.Context
import android.content.SharedPreferences
import com.a531tracker.R

class PreferenceUtils (private val mContext: Context) {

    fun savePreference(prefValue: Boolean, prefKey: String): Boolean {
        val sharedPref = mContext.getSharedPreferences(mContext.resources.getString(R.string.app_package), Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putBoolean(prefKey, prefValue)
            return commit()
        }
    }

    fun getPreference(prefKey: String): Boolean? {
        return userPreferences()?.getBoolean(prefKey, false)
    }

    fun userPreferences(): SharedPreferences? {
        return mContext.getSharedPreferences(mContext.resources.getString(R.string.app_package), Context.MODE_PRIVATE)
    }

    fun createNewUserPreferences(): Boolean {
        var success = false
        // Use Kilogram
        success = savePreference(false, mContext.getString(R.string.preference_kilogram_key))
        // Week Format (show 7)
        success = savePreference(true, mContext.getString(R.string.preference_week_options_key))
        // Use 8/6/3 variant
        success = savePreference(false, mContext.getString(R.string.preference_split_variant_extra_key))
        // FSL
        success = savePreference(false, mContext.getString(R.string.preference_fsl_key))
        // Joker
        success = savePreference(false, mContext.getString(R.string.preference_joker_key))
        // Extra sets on Deload
        success = savePreference(false, mContext.getString(R.string.preference_deload_key))
        // Swap Extra sets
        success = savePreference(false, mContext.getString(R.string.preference_swap_extras_key))
        // Remove all extras
        success = savePreference(false, mContext.getString(R.string.preference_remove_extras_key))

        return success
    }

    companion object {
        fun getInstance(mContext: Context): PreferenceUtils {
            return PreferenceUtils(mContext)
        }
    }
}