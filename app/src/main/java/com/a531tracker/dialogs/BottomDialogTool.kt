package com.a531tracker.dialogs

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.a531tracker.ObjectBuilders.LiftBuilder
import com.a531tracker.R
import com.a531tracker.database.DatabaseRepository
import com.a531tracker.databinding.BottomDialogToolLayoutBinding
import com.a531tracker.tools.AppConstants
import com.a531tracker.tools.AppUtils
import com.a531tracker.tools.PreferenceUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomDialogTool(private val mContext: Context, private val dr: DatabaseRepository): BottomSheetDialogFragment(), ViewBinding {

    lateinit var binding: BottomDialogToolLayoutBinding

    private lateinit var listener: BottomDialogToolsClick

    private lateinit var prefUtils: PreferenceUtils

    private lateinit var appUtils: AppUtils

    private var benchTm = 0f
    private var squatTm = 0f
    private var dlTm = 0f
    private var ohpTm = 0f
    private var cycleVal = 0
    private var canContinue = true
    private var usingKg = false

    interface BottomDialogToolsClick {
        fun confirmUpdate()
    }

    fun newInstance(liftBuilder: LiftBuilder): BottomDialogTool {
        val bundle = Bundle()
        bundle.apply {
            putFloat(AppConstants.DIALOG_BENCH, liftBuilder.benchTm.toFloat())
            putFloat(AppConstants.DIALOG_SQUAT, liftBuilder.squatTm.toFloat())
            putFloat(AppConstants.DIALOG_DL, liftBuilder.dlTm.toFloat())
            putFloat(AppConstants.DIALOG_OHP, liftBuilder.ohpTm.toFloat())
            putInt(AppConstants.DIALOG_CYCLE, dr.getDataRepo(mContext).getCycle())
        }

        return BottomDialogTool(mContext, dr).apply { arguments = bundle }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is BottomDialogToolsClick) {
            listener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BottomDialogToolLayoutBinding.inflate(inflater)
        prefUtils = PreferenceUtils.getInstance(mContext)
        appUtils = AppUtils().getInstance()

        if (arguments != null) {
            benchTm = arguments?.getFloat(AppConstants.DIALOG_BENCH) ?: 0f
            squatTm = arguments?.getFloat(AppConstants.DIALOG_SQUAT) ?: 0f
            dlTm = arguments?.getFloat(AppConstants.DIALOG_DL) ?: 0f
            ohpTm = arguments?.getFloat(AppConstants.DIALOG_OHP) ?: 0f
            cycleVal = arguments?.getInt(AppConstants.DIALOG_CYCLE) ?: 0
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        usingKg = prefUtils.getPreference(mContext.getString(R.string.preference_kilogram_key)) ?: false

        if (usingKg) {
            benchTm = appUtils.getWeight(usingKg, benchTm.toInt(), 1f).toFloat()
            squatTm = appUtils.getWeight(usingKg, squatTm.toInt(), 1f).toFloat()
            ohpTm = appUtils.getWeight(usingKg, ohpTm.toInt(), 1f).toFloat()
            dlTm = appUtils.getWeight(usingKg, dlTm.toInt(), 1f).toFloat()

            binding.benchCurrent.text = benchTm.toString()
            binding.squatCurrent.text = squatTm.toString()
            binding.ohpCurrent.text = ohpTm.toString()
            binding.dlCurrent.text = dlTm.toString()

            val extraFive = appUtils.getKilo(5)
            val extraTen = appUtils.getKilo(10)

            binding.benchUpdate.setText(String.format("%.2f",(benchTm + extraFive)))
            binding.squatUpdate.setText(String.format("%.2f",(squatTm + extraTen)))
            binding.ohpUpdate.setText(String.format("%.2f",(ohpTm + extraFive)))
            binding.dlupdate.setText(String.format("%.2f",(dlTm + extraTen)))
        } else {
            binding.benchCurrent.text = benchTm.toInt().toString()
            binding.squatCurrent.text = squatTm.toInt().toString()
            binding.ohpCurrent.text = ohpTm.toInt().toString()
            binding.dlCurrent.text = dlTm.toInt().toString()

            binding.benchUpdate.setText((benchTm.toInt() + 5).toString())
            binding.squatUpdate.setText((squatTm.toInt() + 10).toString())
            binding.ohpUpdate.setText((ohpTm.toInt() + 5).toString())
            binding.dlupdate.setText((dlTm.toInt() + 10).toString())
        }

        binding.cycleCurrent.text = cycleVal.toString()
        binding.cycleUpdate.text = (cycleVal + 1).toString()

        checkForButton()
        if (canContinue) {
            binding.updateError.visibility = View.GONE
            binding.confirmButton.isEnabled = true
            setUpdateListener()
        } else {
            binding.confirmButton.isEnabled = false
            binding.confirmButton.setTextColor(ContextCompat.getColor(binding.root.context, R.color.disabledWhite))
            binding.updateError.visibility = View.VISIBLE
        }
    }

    private fun checkForButton() {
        for (liftName in AppConstants.LIFT_ACCESS_LIST) {
            for (i in 0 until 3) {
                val amrapVal = dr.checkCurrentAmrap(liftName, i)
                if (amrapVal < 0) {
                    canContinue = false
                    break
                } else {
                    canContinue = true
                }
            }
        }
    }

    private fun setUpdateListener() {
        binding.confirmButton.setOnClickListener {
            var success = 0
            val updatedTm = arrayListOf(
                    binding.benchUpdate.text.toString(),
                    binding.squatUpdate.text.toString(),
                    binding.ohpUpdate.text.toString(),
                    binding.dlupdate.text.toString()
                    )
            dr.updateCycle()
            for ((i,liftName) in AppConstants.LIFT_ACCESS_LIST.withIndex()) {
                val liftTwo = dr.getLift(liftName)!!
                liftTwo.apply {
                    trainingMax = if (usingKg) appUtils.getPound(updatedTm[i].toDouble()).toInt() else updatedTm[i].toInt()
                    eightFiveReps = null
                    ninetyReps = null
                    ninetyFiveReps = null
                }
                success = dr.updateAll(liftTwo)
            }
            if (success == 1) {
                dismiss()
                listener.confirmUpdate()
            }
        }
    }

    override fun getRoot(): View {
        return binding.root
    }
}