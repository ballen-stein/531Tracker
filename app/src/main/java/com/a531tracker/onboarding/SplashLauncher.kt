package com.a531tracker.onboarding

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.a531tracker.R
import com.a531tracker.database.DatabaseRepository
import com.a531tracker.databinding.ActivitySplashlauncherBinding
import com.a531tracker.homepage.HomePageActivity
import com.a531tracker.mvpbase.DependencyInjectorClass
import com.a531tracker.tools.AppConstants
import com.a531tracker.tools.PreferenceUtils


class SplashLauncher : AppCompatActivity() {

    private lateinit var binding: ActivitySplashlauncherBinding

    private lateinit var progressBar: ProgressBar

    private lateinit var dr: DatabaseRepository

    private val timeCountInMilliSeconds = 5000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashlauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressBar = binding.progressBar
        dr = DependencyInjectorClass().dataRepo(this)

        runStartingChecks()
    }

    private fun runStartingChecks() {
        try {
            dr.checkCycle()
            val liftMaxes = ArrayList<Int>()
            for (lifts in AppConstants.LIFT_ACCESS_LIST) {
                dr.getLift(lifts)!!.trainingMax?.let { liftMaxes.add(it) }
            }
            if (liftMaxes.size == 4) {
                launchApp(false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            launchApp(true)
        }
    }

    private fun launchApp(firstLaunch: Boolean) {
        val intent = if (firstLaunch) {
            val preferenceUtils = PreferenceUtils(this)
            if (preferenceUtils.createNewUserPreferences()) {
                Log.d("Preferences", "Successfully instantiated new preferences!")
            }
            Intent(applicationContext, NewUserInformation::class.java)
        } else {
            Intent(applicationContext, HomePageActivity::class.java)
        }
        setProgressBarValues()
        startCountDownTimer(intent)
    }

    private fun setProgressBarValues() {
        progressBar.max = timeCountInMilliSeconds.toInt() / 50
        progressBar.progress = timeCountInMilliSeconds.toInt() / 1000
    }

    private fun startCountDownTimer(intent: Intent) {
        val countDownTimer = object : CountDownTimer(timeCountInMilliSeconds, 50) {
            override fun onTick(millisUntilFinished: Long) {
                progressBar.setProgress((millisUntilFinished / 50).toInt())
            }

            override fun onFinish() {// call to initialize the progress bar values
                startActivity(intent)
                overridePendingTransition(R.anim.enter_right, R.anim.exit_left)
                finish()
            }
        }.start()
    }
}