package com.a531tracker.homepage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.a531tracker.R
import com.a531tracker.database.DatabaseRepository
import com.a531tracker.databinding.FragmentToolspageBinding
import com.a531tracker.tools.AppConstants
import com.a531tracker.tools.AppUtils
import com.a531tracker.tools.PreferenceUtils

class ToolsPageFragment : Fragment(), ViewBinding {

    private lateinit var binding: FragmentToolspageBinding

    private lateinit var dr: DatabaseRepository

    private lateinit var prefUtils: PreferenceUtils

    private var cycleVal = 0

    private var percentVal = 0.0f

    private var extrasDisabled = false

    fun newInstance(): ToolsPageFragment {
        return ToolsPageFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentToolspageBinding.inflate(layoutInflater)
        dr = DatabaseRepository(binding.root.context)
        prefUtils = PreferenceUtils.getInstance(binding.root.context)
        extrasDisabled = prefUtils.getPreference(getString(R.string.preference_remove_extras_key)) ?: false
        setViews()

        return root
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        val seekProgress = (dr.getUserPercentList("Bench")[0] * 100).toInt()
        binding.liftToolsPercentSeekbar.apply {
            progress = seekProgress
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                min = 30
                max = 90
            }
        }
    }

    private fun setViews() {
        cycleVal = dr.getCycle()

        binding.liftToolsCycleValue.text = cycleVal.toString()
        binding.liftToolsUpdateCycleBtn.setOnClickListener {
            (root.context as HomePageActivity).launchTool(AppConstants.NAVIGATION_TOOL_CYCLE)
        }

        if (extrasDisabled) {
            binding.liftToolsPercentSeekbar.isEnabled = false
            binding.liftToolsUpdatePercentBtn.isEnabled = false
            binding.liftToolsPercentBarText.text = root.context.getString(R.string.extras_disabled)
        } else {
            binding.liftToolsPercentSeekbar.isEnabled = true
            binding.liftToolsUpdatePercentBtn.isEnabled = true
        }
        binding.liftToolsPercentSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                percentVal = AppUtils().getInstance().normalizePercent(progress.toFloat())

                binding.liftToolsPercentBarText.apply {
                    text = percentVal.toString()
                    if (percentVal > 90f || percentVal < 30f) {
                        setTextColor(root.context.getColor(R.color.colorRed))
                    } else {
                        setTextColor(root.context.getColor(R.color.colorWhite))
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        binding.liftToolsUpdatePercentBtn.setOnClickListener {
            (root.context as HomePageActivity).launchTool(AppConstants.NAVIGATION_TOOL_PERCENT)
        }

        binding.liftToolsUpdateLiftsBtn.setOnClickListener {
            (root.context as HomePageActivity).launchTool(AppConstants.NAVIGATION_TOOL_TM)
        }
    }

    override fun getRoot(): View {
        return binding.root
    }

    companion object {
        val lifts: MutableList<String> = mutableListOf(AppConstants.WEEK_BENCH, AppConstants.WEEK_DEADLIFT, AppConstants.WEEK_OVERHAND, AppConstants.WEEK_SQUAT)
        val liftsBackup: MutableList<String> = mutableListOf("Bench", "Squat", "Overhand Press", "Deadlift")
    }
}