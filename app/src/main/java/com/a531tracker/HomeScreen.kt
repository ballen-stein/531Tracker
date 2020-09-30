package com.a531tracker

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.TextView

import com.a531tracker.database.DatabaseHelper

class HomeScreen : AppCompatActivity() {

    private var db: DatabaseHelper? = null
    private var mContext: Context? = null
    private var cycleValue: Int? = null

    private var benchView: Button? = null
    private var deadliftView: Button? = null
    private var pressView: Button? = null
    private var squatView: Button? = null
    private var cycleView: Button? = null
    private var homeButton: Button? = null
    private var settingsButton: Button? = null

    private var newTvArray: Array<TextView>? = null
    private var cycleDisplay: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DatabaseHelper(this)
        mContext = this

        cycleDisplay = findViewById(R.id.cycleDisplay)

        setViews()
        setNav()
        setListeners()
    }


    override fun onStart() {
        super.onStart()
        startCycle()
        setCurrentTrainingValues()
    }


    private fun setCurrentTrainingValues() {
        for (i in compoundLifts.indices) {
            newTvArray!![i].text = db!!.getLifts(compoundLifts[i]).training_max.toString()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SET_TRAINING_MAX_CODE && resultCode == Activity.RESULT_OK) {
            //resetLifValuesArray();
        } else if (requestCode == UPDATE_TRAINING_MAX_CODE && resultCode == Activity.RESULT_OK) {
            //resetLifValuesArray();
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    fun openCompoundWeek(compound: String, swap: String) {
        val intent = Intent(applicationContext, Week::class.java)
        intent.putExtra("Compound", compound)
        intent.putExtra("Swap", swap)
        intent.putExtra("Cycle", cycleValue)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
    }


    fun alertBuilder(title: String, message: String, extraMessage: String, cancelable: Boolean, noLiftsFound: Boolean) {
        val dialog = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        dialog.setTitle(title)
                .setMessage(message)
                .setCancelable(cancelable)
                .setPositiveButton(R.string.ok_text) { dialogInterface, i ->
                    if (noLiftsFound) {
                        val intent = Intent(mContext, SetMaxes::class.java)
                        intent.putExtra("NewLifts", true)
                        startActivityForResult(intent, SET_TRAINING_MAX_CODE)
                    }
                }
        if (!noLiftsFound) {
            val tv = TextView(this)
            tv.gravity = Gravity.CENTER
            tv.setTextColor(ContextCompat.getColor(mContext!!, R.color.colorOrange))
            tv.text = extraMessage
            tv.textSize = resources.getDimension(R.dimen.text_8)
            dialog.setView(tv)
        } else {
            dialog.setMessage(message)
        }
        val alertDialog = dialog.create()
        alertDialog.show()
    }


    private fun startCycle() {
        try {
            if (db!!.startCycle()) {
                cycleValue = db!!.cycle
            }
        } catch (e: Exception) {
            cycleValue = 1
            e.printStackTrace()
        }

        val cycleText = "You are currently on Cycle #" + cycleValue!!
        cycleDisplay!!.text = cycleText
    }


    private fun setViews() {
        benchView = findViewById(R.id.bench_btn)
        deadliftView = findViewById(R.id.deadlift_btn)
        pressView = findViewById(R.id.press_btn)
        squatView = findViewById(R.id.squat_btn)
        cycleView = findViewById(R.id.update_cycle_btn)

        homeButton = findViewById(R.id.home_button)
        settingsButton = findViewById(R.id.settings_button)

        newTvArray = arrayOf(findViewById(R.id.bench_value), findViewById(R.id.press_value), findViewById(R.id.squat_value), findViewById(R.id.deadlift_value))
    }


    private fun setNav() {
        navHome()
        navSettings()
    }


    //Buttons

    fun setListeners() {
        benchButton()
        deadliftButton()
        pressButton()
        squatButton()
        updateCycleButton()
    }


    private fun benchButton() {
        benchView!!.setOnClickListener { openCompoundWeek("Bench", "Overhand Press") }
    }


    private fun deadliftButton() {
        deadliftView!!.setOnClickListener { openCompoundWeek("Deadlift", "Squat") }
    }


    private fun pressButton() {
        pressView!!.setOnClickListener { openCompoundWeek("Overhand Press", "Bench") }
    }


    private fun squatButton() {
        squatView!!.setOnClickListener { openCompoundWeek("Squat", "Deadlift") }
    }


    private fun updateCycleButton() {
        cycleView!!.setOnClickListener {
            val intent = Intent(mContext, UpdateValues::class.java)
            startActivityForResult(intent, UPDATE_TRAINING_MAX_CODE)
        }
    }


    private fun navHome() {
        homeButton!!.isEnabled = false
    }


    private fun navSettings() {
        settingsButton!!.setOnClickListener {
            val intent = Intent(mContext, Settings::class.java)
            startActivity(intent)
        }
    }

    companion object {
        internal val SET_TRAINING_MAX_CODE = 1
        internal val UPDATE_TRAINING_MAX_CODE = 2

        var compoundLifts = arrayOf("Bench", "Overhand Press", "Squat", "Deadlift")
    }
}
