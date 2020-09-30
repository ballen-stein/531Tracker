package com.a531tracker

import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioGroup

import com.a531tracker.database.DatabaseHelper
import com.a531tracker.DetailFragments.InformationFragment
import com.a531tracker.ObjectBuilders.CompoundLifts
import com.a531tracker.ObjectBuilders.UserSettings

import java.math.BigDecimal
import java.util.ArrayList
import java.util.Collections

import com.a531tracker.HomeScreen.Companion.compoundLifts

class BBBSettings : AppCompatActivity(), InformationFragment.InformationFragmentListener {

    private var informationFragment: InformationFragment? = null

    private var submitWeight: Button? = null
    private var submitWeek: Button? = null
    private var submitOptions: Button? = null
    private var submitPercents: Button? = null
    private var homeButton: Button? = null
    private var settingsButton: Button? = null

    private var bbbWeekInfoButton: LinearLayout? = null
    private var bbbPercentInfoButton: LinearLayout? = null
    private var bbbFormatInfoButton: LinearLayout? = null

    private var weekGroup: RadioGroup? = null
    private var bbbGroup: RadioGroup? = null
    private var weightGroup: RadioGroup? = null

    private var bbbEight: CheckBox? = null
    private var bbbFSL: CheckBox? = null
    private var bbbJoker: CheckBox? = null
    private var bbbDeload: CheckBox? = null
    private var bbbSwaps: CheckBox? = null
    private var bbbRemove: CheckBox? = null

    private var radioBbbChoices: List<Int>? = null

    private var userSettings = UserSettings()
    private val newSettings = UserSettings()

    private var db: DatabaseHelper? = null


    private val checkboxValues: Int
        get() {
            var i: Int
            if (userSettings.chosenBBBFormat.toString()[0] == '9') {
                i = 900000
            } else {
                i = 800000
            }
            if (bbbEight!!.isChecked)
                i += 1
            if (bbbFSL!!.isChecked)
                i += 10
            if (bbbJoker!!.isChecked)
                i += 100
            if (bbbDeload!!.isChecked)
                i += 1000
            if (bbbRemove!!.isChecked)
                i += 10000
            return i
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bbb_settings_view)

        db = DatabaseHelper(this)

        radioBbbChoices = ArrayList()
        Collections.addAll(radioBbbChoices, R.id.radio_bbb_30, R.id.radio_bbb_35, R.id.radio_bbb_40, R.id.radio_bbb_45, R.id.radio_bbb_50,
                R.id.radio_bbb_55, R.id.radio_bbb_60, R.id.radio_bbb_65, R.id.radio_bbb_70, R.id.radio_bbb_75, R.id.radio_bbb_80, R.id.radio_bbb_85, R.id.radio_bbb_90)

        setButtons()
        setListeners()
        setViews()
    }

    override fun onStart() {
        super.onStart()
        getCurrentSettings()
        setSettingViews()
    }


    private fun getCurrentSettings() {
        userSettings = db!!.userSettings
    }


    private fun setSettingViews() {
        weekGroup!!.check(setChosenWeek(userSettings.weekFormat))
        bbbGroup!!.check(setChosenBbbPercent(userSettings.chosenBBBPercent.toString()))
        setOptions(userSettings.chosenBBBFormat)
        setWeightOption(userSettings.chosenBBBFormat)
        if (userSettings.swapBBBFormat == 1)
            bbbSwaps!!.isChecked = true
    }


    private fun setChosenWeek(week: Int): Int {
        val weekVal: Int
        if (week == 1) {
            weekVal = R.id.radio_standard
        } else {
            weekVal = R.id.radio_seven
        }
        return weekVal
    }


    private fun setChosenBbbPercent(bbbPercent: String): Int {
        val bbbVal: Int
        when (bbbPercent) {
            "0.30", "0.3" -> bbbVal = radioBbbChoices!![0]
            "0.35" -> bbbVal = radioBbbChoices!![1]
            "0.4", "0.40" -> bbbVal = radioBbbChoices!![2]
            "0.45" -> bbbVal = radioBbbChoices!![3]
            "0.5", "0.50" -> bbbVal = radioBbbChoices!![4]
            "0.55" -> bbbVal = radioBbbChoices!![5]
            "0.6", "0.60" -> bbbVal = radioBbbChoices!![6]
            "0.65" -> bbbVal = radioBbbChoices!![7]
            "0.7", "0.70" -> bbbVal = radioBbbChoices!![8]
            "0.75" -> bbbVal = radioBbbChoices!![9]
            "0.80", "0.8" -> bbbVal = radioBbbChoices!![10]
            "0.85" -> bbbVal = radioBbbChoices!![11]
            "0.90", "0.9" -> bbbVal = radioBbbChoices!![12]
            else -> bbbVal = 0
        }
        return bbbVal
    }


    private fun setOptions(chosenBBBFormat: Int) {
        val bbbLength = chosenBBBFormat.toString()
        for (i in 1 until bbbLength.length) {
            checkSettings(bbbLength[i], i)
        }
    }


    private fun setWeightOption(weightFormat: Int) {
        val weightVal = weightFormat.toString()
        if (weightVal[0] != '8') {
            weightGroup!!.check(R.id.radio_lb)
        } else {
            weightGroup!!.check(R.id.radio_kg)
        }
    }


    private fun checkSettings(c: Char, i: Int) {
        when (i) {
            1 -> {
                if (c == '1') {
                    bbbRemove!!.isChecked = true
                    break
                }
                if (c == '1') {
                    bbbDeload!!.isChecked = true
                    break
                }
                if (c == '1') {
                    bbbJoker!!.isChecked = true
                    break
                }
                if (c == '1') {
                    bbbFSL!!.isChecked = true
                    break
                }
                if (c == '1') {
                    bbbEight!!.isChecked = true
                    break
                }
            }
            2 -> {
                if (c == '1') {
                    bbbDeload!!.isChecked = true
                    break
                }
                if (c == '1') {
                    bbbJoker!!.isChecked = true
                    break
                }
                if (c == '1') {
                    bbbFSL!!.isChecked = true
                    break
                }
                if (c == '1') {
                    bbbEight!!.isChecked = true
                    break
                }
            }
            3 -> {
                if (c == '1') {
                    bbbJoker!!.isChecked = true
                    break
                }
                if (c == '1') {
                    bbbFSL!!.isChecked = true
                    break
                }
                if (c == '1') {
                    bbbEight!!.isChecked = true
                    break
                }
            }
            4 -> {
                if (c == '1') {
                    bbbFSL!!.isChecked = true
                    break
                }
                if (c == '1') {
                    bbbEight!!.isChecked = true
                    break
                }
            }
            5 -> if (c == '1') {
                bbbEight!!.isChecked = true
                break
            }
        }
    }


    private fun setViews() {
        weekGroup = findViewById(R.id.radio_cycle_type)
        bbbGroup = findViewById(R.id.bbb_radio_group)
        weightGroup = findViewById(R.id.radio_weight_option)

        bbbEight = findViewById(R.id.bbb_eight_split)
        bbbFSL = findViewById(R.id.bbb_fsl)
        bbbJoker = findViewById(R.id.bbb_joker)
        bbbDeload = findViewById(R.id.bbb_deload)
        bbbSwaps = findViewById(R.id.bbb_swaps)
        bbbRemove = findViewById(R.id.bbb_remove)
    }


    private fun setButtons() {
        submitWeek = findViewById(R.id.submit_week_options)
        submitOptions = findViewById(R.id.submit_bbb_options)
        submitPercents = findViewById(R.id.submit_bbb_percent)
        submitWeight = findViewById(R.id.submit_weight_choice)

        bbbWeekInfoButton = findViewById(R.id.week_options_information)
        bbbFormatInfoButton = findViewById(R.id.bbb_options_information)
        bbbPercentInfoButton = findViewById(R.id.bbb_percent_information)
        navButtons()
    }


    private fun navButtons() {
        homeButton = findViewById(R.id.home_button)
        settingsButton = findViewById(R.id.settings_button)
    }


    private fun updateCycleFormat() {
        val selectedFormat: String
        if (weekGroup!!.checkedRadioButtonId == R.id.radio_standard) {
            newSettings.weekFormat = 1
            selectedFormat = "4 week cycle."
        } else {
            newSettings.weekFormat = 0
            selectedFormat = "7 Week cycle."
        }
        val i = db!!.updateWeekSettings(newSettings, userSettings.weekFormat)

        if (i == 1) {
            clearSettings()
            getCurrentSettings()
            val errorAlerts = ErrorAlerts(this)
            errorAlerts.setErrorAlertsValues(
                    false,
                    true,
                    resources.getString(R.string.settings_success_title),
                    "You've changed your cycle format to the $selectedFormat",
                    "",
                    false
            )
            errorAlerts.preformattedAlert(this).show()
        } else {
            val errorAlerts = ErrorAlerts(this)
            errorAlerts.setErrorAlertsValues(
                    false,
                    true,
                    resources.getString(R.string.settings_fail_title),
                    "Failed to update the cycle format; try again later.\n\nIf this issue persists, please contact the developer at 531developer@gmail.com.",
                    "",
                    false)
            errorAlerts.preformattedAlert(this).show()
        }
    }


    private fun updateBBBOptions() {
        newSettings.swapBBBFormat = swapCheck()
        newSettings.chosenBBBFormat = checkboxValues
        var message = ""
        if (changeSwap(newSettings.swapBBBFormat, userSettings.swapBBBFormat) == 1) {
            if (changeFormat(newSettings, userSettings.chosenBBBFormat) == 1) {
                var createText = ""
                if (bbbEight!!.isChecked)
                    createText += "Eight-Six-Three Split, "
                if (bbbFSL!!.isChecked)
                    createText += "FSL, "
                if (bbbJoker!!.isChecked)
                    createText += "Joker, "
                if (bbbDeload!!.isChecked)
                    createText += "BBB During Deload, "
                if (bbbSwaps!!.isChecked)
                    createText += "Swapped BBBs, "
                clearSettings()
                getCurrentSettings()
                if (createText.length > 0)
                    message = "You've changed your Boring But Big format to include the following: " + createText.substring(0, createText.length - 2) + "."
                else
                    message = "You've removed all Boring But Big options."
                if (bbbRemove!!.isChecked)
                    message += "\n\nYou've selected to remove the Boring But Big options. All prior Boring But Big settings will be ignored. First Set Last will still appear with this option selected"
                newAlert(true, message)
            } else {
                newAlert(false, message)
            }
        } else {
            message = resources.getString(R.string.settings_fail_message)
            newAlert(false, message)
        }
    }


    private fun swapCheck(): Int {
        return if (bbbSwaps!!.isChecked)
            1
        else
            0
    }


    private fun changeSwap(newSwap: Int, oldSwap: Int): Int {
        return db!!.swapBBBWorkouts(oldSwap, newSwap)
    }


    private fun changeFormat(userSettings: UserSettings, oldVal: Int): Int {
        return db!!.updateBbbFormat(userSettings, oldVal)
    }


    private fun updateWeightOption() {
        var currentWeight = userSettings.chosenBBBFormat.toString()
        var message = ""
        if (weightGroup!!.checkedRadioButtonId == R.id.radio_kg) {
            if (currentWeight[0] == '9') {
                currentWeight = currentWeight.replace('9', '8')
                message = "kilograms."
            } else {
                message = "kilograms"
            }
        } else {
            if (currentWeight[0] == '8') {
                currentWeight = currentWeight.replace('8', '9')
                message = "pounds."
            } else {
                message = "pounds"
            }
        }
        newSettings.chosenBBBFormat = Integer.parseInt(currentWeight)
        newSettings.swapBBBFormat = userSettings.swapBBBFormat

        if (changeSwap(newSettings.swapBBBFormat, userSettings.swapBBBFormat) == 1) {
            if (changeFormat(newSettings, userSettings.chosenBBBFormat) == 1) {
                newAlert(true, "You've changed your weight to $message")
                clearSettings()
                getCurrentSettings()
            }
        }

    }


    private fun checkBBBPercent(id: Int): Float {
        var bbbVal = 0.30f
        for (i in radioBbbChoices!!.indices) {
            if (id == radioBbbChoices!![i]) {
                break
            } else {
                bbbVal += 0.05f
            }
        }
        val roundingVal = BigDecimal(java.lang.Float.toString(bbbVal)).setScale(2, BigDecimal.ROUND_DOWN)
        return roundingVal.toFloat()
    }


    private fun updateBBBPercents(bbbVal: Float) {
        var successful = false
        for (lifts in compoundLifts) {
            val newLifts = CompoundLifts()
            newLifts.big_but_boring_weight = bbbVal
            newLifts.compound_movement = lifts
            val i = db!!.updateBBBPercent(newLifts)
            if (i == 1) {
                successful = true
            }
        }
        val message: String
        if (successful) {
            clearSettings()
            getCurrentSettings()
            message = "You've changed your Boring But Big percents to " + bbbVal * 100 + "%."
            newAlert(true, message)
        } else {
            message = resources.getString(R.string.settings_fail_message)
            newAlert(false, message)
        }
    }


    // ---------- Fragment Values ----------

    private fun displayInformation(header: String, desc: String) {
        if (informationFragment != null) {
            closeInformation()
        }
        informationFragment = InformationFragment.newInstance(header, desc)
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_bottom, R.anim.exit_bottom, R.anim.enter_bottom, R.anim.exit_bottom)
                .addToBackStack("@null")
                .replace(R.id.fragment_holder, informationFragment!!)
                .commit()
    }

    override fun closeInformation() {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_bottom, R.anim.exit_bottom, R.anim.enter_bottom, R.anim.exit_bottom)
                .remove(informationFragment!!)
                .commit()
        informationFragment = null
    }


    // ---------- Buttons and Listeners ----------

    private fun setListeners() {
        submitWeekOptions()
        submitOptionsBBB()
        submitPercentsBBB()
        viewInformation()
        submitWeightOption()
        setNav()
    }


    private fun viewInformation() {
        weekInformation()
        percentInformation()
        formatInformation()
    }


    private fun setNav() {
        navHome()
        navSettings()
    }


    private fun navHome() {
        homeButton!!.setOnClickListener {
            val intent = Intent(applicationContext, HomeScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }


    private fun navSettings() {
        settingsButton!!.setOnClickListener {
            onBackPressed()
            finish()
        }
    }


    private fun submitWeightOption() {
        submitWeight!!.setOnClickListener { updateWeightOption() }
    }

    private fun submitWeekOptions() {
        submitWeek!!.setOnClickListener { updateCycleFormat() }
    }


    private fun submitPercentsBBB() {
        submitPercents!!.setOnClickListener { updateBBBPercents(checkBBBPercent(bbbGroup!!.checkedRadioButtonId)) }
    }


    private fun submitOptionsBBB() {
        submitOptions!!.setOnClickListener { updateBBBOptions() }
    }


    private fun weekInformation() {
        bbbWeekInfoButton!!.setOnClickListener { displayInformation(getString(R.string.settings_cycle_header), getString(R.string.settings_week_info)) }
    }


    private fun formatInformation() {
        bbbFormatInfoButton!!.setOnClickListener { displayInformation(resources.getString(R.string.settings_bbb_options), getString(R.string.settings_option_info)) }
    }


    private fun percentInformation() {
        bbbPercentInfoButton!!.setOnClickListener { displayInformation(getString(R.string.settings_bbb_percents), getString(R.string.settings_percent_info)) }
    }


    // ---------- Misc. ----------

    private fun clearSettings() {
        userSettings = UserSettings()
    }


    private fun newAlert(success: Boolean, message: String) {
        val title: String
        if (success)
            title = resources.getString(R.string.settings_success_title)
        else
            title = resources.getString(R.string.settings_fail_title)

        val errorAlerts = ErrorAlerts(this)
        errorAlerts.setErrorAlertsValues(
                false,
                true,
                title,
                message,
                "",
                false)
        errorAlerts.preformattedAlert(this).show()
    }
}
