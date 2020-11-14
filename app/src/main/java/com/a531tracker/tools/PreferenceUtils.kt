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

    companion object {
        fun getInstance(mContext: Context): PreferenceUtils {
            return PreferenceUtils(mContext)
        }
    }
}