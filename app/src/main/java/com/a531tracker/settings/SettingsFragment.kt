package com.a531tracker.settings

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.a531tracker.R
import com.a531tracker.database.DatabaseRepository
import com.a531tracker.mvpbase.DependencyInjectorClass
import com.a531tracker.onboarding.SplashLauncher
import com.a531tracker.tools.PreferenceUtils

class SettingsFragment(private val mActivity: Activity, dependencyInjectorClass: DependencyInjectorClass) : PreferenceFragmentCompat() {

    private lateinit var prefUtils: PreferenceUtils

    private var db: DatabaseRepository = dependencyInjectorClass.dataRepo(mActivity)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        prefUtils = PreferenceUtils.getInstance(mActivity)
        val currentPrefs = prefUtils.userPreferences()
        if (currentPrefs != null) {
            Log.d("TestingData", "Current Prefs : ${currentPrefs.all}")
        }

        updateSettings(prefUtils.getPreference(getString(R.string.preference_remove_extras_key))!!)
        setPrefListeners()
    }

    private fun setPrefListeners() {
        findPreference<SwitchPreferenceCompat>(getString(R.string.preference_kilogram_key))!!.setOnPreferenceChangeListener { preference, newValue ->
            if (prefUtils.savePreference(newValue as Boolean, preference.key)) {
                Log.d("TestingData", "Updated Preference!")
                true
            } else {
                false
            }

        }

        findPreference<SwitchPreferenceCompat>(getString(R.string.preference_week_options_key))!!.setOnPreferenceChangeListener { preference, newValue ->
            prefUtils.savePreference(newValue as Boolean, preference.key)
            true
        }

        findPreference<CheckBoxPreference>(getString(R.string.preference_split_variant_extra_key))!!.setOnPreferenceChangeListener { preference, newValue ->
            prefUtils.savePreference(newValue as Boolean, preference.key)
            true
        }

        findPreference<CheckBoxPreference>(getString(R.string.preference_fsl_key))!!.setOnPreferenceChangeListener { preference, newValue ->
            prefUtils.savePreference(newValue as Boolean, preference.key)
            true
        }

        findPreference<CheckBoxPreference>(getString(R.string.preference_joker_key))!!.setOnPreferenceChangeListener { preference, newValue ->
            prefUtils.savePreference(newValue as Boolean, preference.key)
            true
        }

        findPreference<CheckBoxPreference>(getString(R.string.preference_deload_key))!!.setOnPreferenceChangeListener { preference, newValue ->
            prefUtils.savePreference(newValue as Boolean, preference.key)
            true
        }

        findPreference<CheckBoxPreference>(getString(R.string.preference_swap_extras_key))!!.setOnPreferenceChangeListener { preference, newValue ->
            prefUtils.savePreference(newValue as Boolean, preference.key)
            true
        }

        findPreference<CheckBoxPreference>(getString(R.string.preference_remove_extras_key))!!.setOnPreferenceChangeListener { preference, newValue ->
            if (prefUtils.savePreference(newValue as Boolean, preference.key)) {
                updateSettings(prefUtils.getPreference(preference.key)!!)
                true
            } else {
                false
            }
        }

        findPreference<Preference>(getString(R.string.preference_data_deletion_key))!!.setOnPreferenceClickListener {
            AlertDialog.Builder(mActivity, R.style.DeleteAllData).apply {
                setTitle(getString(R.string.pref_delete_alert_title))
                setMessage(getString(R.string.pref_delete_alert_warning))
                setPositiveButton(getString(R.string.pref_delete_alert_ok)) { _, _ ->
                    db.delete()
                    val intent = Intent(mActivity, SplashLauncher::class.java)
                    intent.putExtra("Deletion", "true")
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
                setNeutralButton(getString(R.string.pref_delete_alert_cancel), null)
                setIcon(R.drawable.ic_delete_forever)
            }.show()

            false
        }
    }

    private fun updateSettings(disableAll: Boolean) {
        mActivity.runOnUiThread {
            findPreference<CheckBoxPreference>(getString(R.string.preference_fsl_key))!!.isEnabled = !disableAll
            findPreference<CheckBoxPreference>(getString(R.string.preference_swap_extras_key))!!.isEnabled = !disableAll
        }
    }

}