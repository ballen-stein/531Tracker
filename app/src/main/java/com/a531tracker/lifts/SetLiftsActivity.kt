package com.a531tracker.lifts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatEditText
import com.a531tracker.BaseActivity
import com.a531tracker.ObjectBuilders.LiftBuilder
import com.a531tracker.R
import com.a531tracker.databinding.ActivitySetliftsBinding
import com.a531tracker.homepage.HomePageActivity
import com.a531tracker.mvpbase.DependencyInjectorClass
import com.a531tracker.tools.AppConstants
import com.a531tracker.tools.AppUtils
import com.a531tracker.tools.PreferenceUtils

class SetLiftsActivity : BaseActivity(), SetLiftsContract.View {

    private lateinit var binding: ActivitySetliftsBinding

    private lateinit var presenter: SetLiftsContract.Presenter

    private lateinit var preferenceUtils: PreferenceUtils

    private val inputHolder: HashMap<String, AppCompatEditText?> = HashMap()

    private var freshLaunch: Boolean = false

    private var usingKg: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetliftsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceUtils = PreferenceUtils.getInstance(mContext = this)
        preferenceUtils.userPreferences()

        freshLaunch = intent.getBooleanExtra(AppConstants.FRESH_LAUNCH, false)
        if (freshLaunch) {
            binding.liftHeaderLayout.visibility = View.GONE
        } else {
            binding.backBtn.setOnClickListener {
                onBackPressed()
                finish()
            }
        }

        if (preferenceUtils.getPreference(getString(R.string.preference_kilogram_key)) == true) {
            binding.liftKg.apply{
                isEnabled = false
            }
        } else {
            binding.liftLb.apply {
                isEnabled = false
            }
        }

        for (liftName in AppConstants.LIFT_ACCESS_LIST) {
            inputHolder[liftName] = when(liftName) {
                "Bench" -> binding.liftBenchInput
                "Squat" -> binding.liftSquatInput
                "Overhand Press" -> binding.liftOhpInput
                else -> binding.liftDlInput
            }
        }

        binding.liftBbbSeekbar.apply {
            setOnClickListener {  }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                min = 30
                max = 90
            }
        }

        binding.liftLb.setOnClickListener {
            it.isEnabled = false
            binding.liftKg.isEnabled = true
            usingKg = false
        }

        binding.liftKg.setOnClickListener {
            it.isEnabled = false
            binding.liftLb.isEnabled = true
            usingKg = true
        }

        binding.liftSubmitBtn.setOnClickListener {
            finalizeData()
        }

        binding.liftHideBbb.setOnCheckedChangeListener { _, isChecked ->
            binding.liftBbbSeekbarValue.isEnabled = !isChecked
        }

        /*val seekProgress = (.getUserPercentList("Bench")[0] * 100).toInt()
        binding.liftBbbSeekbar.apply {
            progress = seekProgress
        }*/

        binding.liftBbbSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                setSeekbarText(progress)
                Log.d("TestingData", "Seekbar is moving to $progress")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Log.d("TestingData", "Seekbar is starting to move")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Log.d("TestingData", "Seekbar is done moving. Moved to ${binding.liftBbbSeekbar.progress}")
            }

        })

        setPresenter(SetLiftsPresenter(this, DependencyInjectorClass(), AppUtils(), preferenceUtils, this))
        presenter.onViewCreated(this, freshLaunch)

    }

    private fun setSeekbarText(progress: Int) {
        val text = AppUtils().normalizePercent(progress.toFloat()).toString()
        binding.liftBbbSeekbarValue.text = text
    }

    private fun finalizeData() {
        val usingTM = binding.trainingMaxCheckbox.isChecked
        val benchVal: Int
        val squatVal: Int
        val ohpVal: Int
        val dlVal: Int

        if(usingKg) {
            benchVal = AppUtils().getWeight(binding.liftBenchInput.text.toString().toDouble()).toInt()
            squatVal = AppUtils().getWeight(binding.liftSquatInput.text.toString().toDouble()).toInt()
            ohpVal = AppUtils().getWeight(binding.liftOhpInput.text.toString().toDouble()).toInt()
            dlVal = AppUtils().getWeight(binding.liftDlInput.text.toString().toDouble()).toInt()
        } else {
            benchVal = binding.liftBenchInput.text.toString().toInt()
            squatVal = binding.liftSquatInput.text.toString().toInt()
            ohpVal = binding.liftOhpInput.text.toString().toInt()
            dlVal = binding.liftDlInput.text.toString().toInt()
        }

        val bbbPercent = binding.liftBbbSeekbar.progress.toFloat()

        val hideBbbStuff = binding.liftHideBbb.isChecked

        val tempBuilder = LiftBuilder(benchTm = benchVal,
                squatTm = squatVal,
                dlTm = dlVal,
                ohpTm = ohpVal,
                percent = bbbPercent,
                usingTm = usingTM
        )
        presenter.saveLiftValues(freshLaunch, tempBuilder, hideBbbStuff)
    }

    override fun setCurrentLifts(tmHolder: HashMap<String, String>, currentPercent: Int) {
        for (liftName in AppConstants.LIFT_ACCESS_LIST) {
            inputHolder[liftName]?.setText(tmHolder[liftName])
        }
        binding.liftBbbSeekbar.apply {
            progress = currentPercent
        }
        setSeekbarText(currentPercent)
    }

    override fun error(throwable: Throwable) {

    }

    override fun finished(freshLaunch: Boolean) {
        if (freshLaunch) {
            startActivity(Intent(this, HomePageActivity::class.java))
            overridePendingTransition(R.anim.enter_right,R.anim.exit_left)
        }
        finish()
    }

    override fun setPresenter(presenter: SetLiftsContract.Presenter) {
        this.presenter = presenter
    }
}