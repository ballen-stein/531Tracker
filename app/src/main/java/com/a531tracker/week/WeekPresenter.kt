package com.a531tracker.week

import android.content.Context
import android.util.Log
import com.a531tracker.tools.AppConstants
import com.a531tracker.ObjectBuilders.CompoundLifts
import com.a531tracker.R
import com.a531tracker.database.DatabaseRepository
import com.a531tracker.mvpbase.DependencyInjectorClass
import com.a531tracker.tools.AppUtils
import com.a531tracker.tools.PreferenceUtils
import java.lang.Exception
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class WeekPresenter (view: WeekContract.View,
                     injector: DependencyInjectorClass,
                     appInjector: AppUtils,
                     private val prefUtils: PreferenceUtils,
                     private val mContext: Context) : WeekContract.Presenter {

    private var databaseRepository: DatabaseRepository = injector.dataRepo(mContext)

    private var appUtils: AppUtils = appInjector.getInstance()

    private var view: WeekContract.View? = view

    private lateinit var liftData: CompoundLifts

    private var usingKG: Boolean = false
    private var sevenWeek: Boolean = false
    private var altFormat: Boolean = true
    private var fslSwap: Boolean = false
    private var coreSwap: Boolean = false
    private var extraDeload: Boolean = false
    private var hideExtras: Boolean = false

    private var refresh: Boolean = false

    private lateinit var userPrefs: MutableMap<String, *>

    private lateinit var bbbPercentsHolder: ArrayList<Float>

    private val headersText = arrayListOf(
            (AppConstants.SET_WARMUP),
            (AppConstants.SET_CORE),
            (AppConstants.SET_BBB),
            (AppConstants.SET_DELOAD)
    )

    private val warmupPercents = arrayListOf(
            arrayListOf(0.40f, 0.50f, 0.60f)
    )

    private val deloadPercents = arrayListOf(
            arrayListOf(0.40f, 0.50f, 0.60f)
    )

    private val corePercentsBase = arrayListOf(
            arrayListOf(0.75f, 0.80f, 0.85f),
            arrayListOf(0.75f, 0.85f, 0.90f),
            arrayListOf(0.80f, 0.90f, 0.95f),
    )

    private val corePercentsMod = arrayListOf(
            arrayListOf(0.65f, 0.75f, 0.80f),
            arrayListOf(0.70f, 0.80f, 0.85f),
            arrayListOf(0.75f, 0.85f, 0.90f)
    )

    private val boringPercents = arrayListOf(
            arrayListOf(0.5f, 0.5f, 0.5f, 0.5f, 0.5f)
    )

    private val percentHolder = HashMap<Int, ArrayList<ArrayList<Float>>>()

    override fun onViewCreated(mContext: Context, liftName: String) {
        databaseRepository.getDataRepo(mContext = mContext)
        userPrefs = prefUtils.userPreferences()!!.all
        usingKG = prefUtils.getPreference(mContext.getString(R.string.preference_kilogram_key)) == true
        sevenWeek = prefUtils.getPreference(mContext.getString(R.string.preference_week_options_key)) ?: true
        altFormat = prefUtils.getPreference(mContext.getString(R.string.preference_split_variant_extra_key)) ?: false
        fslSwap = prefUtils.getPreference(mContext.getString(R.string.preference_fsl_key)) ?: false
        coreSwap = prefUtils.getPreference(mContext.getString(R.string.preference_swap_extras_key)) ?: false
        extraDeload = prefUtils.getPreference(mContext.getString(R.string.preference_deload_key)) ?: false
        hideExtras = prefUtils.getPreference(mContext.getString(R.string.preference_remove_extras_key)) ?: false


        Log.d("TestingData", "Last prefs : SevenWeek : $sevenWeek and CoreSwap : $coreSwap")

        Log.d("TestingData", "Values: KG:$usingKG, SevenWeek:$sevenWeek, " +
                "BaseFormat:$altFormat, " +
                "FslSwap:$fslSwap, " +
                "CoreSwap:$coreSwap, " +
                "ExtraDeload:$extraDeload")

        getLift(liftName = liftName)
    }

    override fun onAmrapReceived(liftName: String, percent: String, repsDone: Int) {
        Log.d("TestingData", "Values: \nCompound - $liftName\n" +
                "Cycle - ${databaseRepository.getCycle()}\n" +
                "Percent - $percent\n" +
                "Amrap - $repsDone\n" +
                "Amrap - ${liftData.trainingMax}")

        val success = databaseRepository.setAmrapValue(
                liftName = liftName,
                amrapPercent = percent,
                repsDone = repsDone,
                weight = liftData.trainingMax!!
        )

        view?.amrapSnackbar(success == 1)
    }

    private fun getLift(liftName: String) {
        try {
            liftData = databaseRepository.getLift(liftName = liftName)!!
            bbbPercentsHolder = databaseRepository.getUserPercentList(liftName)
            percentHolder.apply {
                put(0, warmupPercents)
                put(1, if (!altFormat) corePercentsBase else corePercentsMod)
                put(2, boringPercents)
                put(3, deloadPercents)
            }
            percentHolder[2]?.replaceAll { bbbPercentsHolder }

            saveWeekData(liftData)
        } catch (e: Exception) {
            Log.d("TestingData", "Error : $e")
            view?.error(e)
        }
    }

    private fun setHeaderText(mContext: Context) {
        headersText.apply {
            add(AppConstants.SET_WARMUP)
            add(AppConstants.SET_CORE)
            add(AppConstants.SET_BBB)
            add(AppConstants.SET_DELOAD)
        }
    }

    private fun saveWeekData(liftData: CompoundLifts) {
        val liftMax = liftData.trainingMax!!
        val tempHolder = ArrayList<String>()
        val repTextHolder = getRepText()
        Log.d("TestingData", "Rep holder (with $altFormat) $repTextHolder")

        for (i in 0 until headersText.size) {
            val weekData = HashMap<Int, ArrayList<String>>()
            val weightBreakdown = HashMap<Int, ArrayList<ArrayList<String>?>>()
            val tempBDHolder = ArrayList<ArrayList<Double>>()
            if (i != 3) {
                val weekListHolder1 = ArrayList<String>()
                val weekListHolder2 = ArrayList<String>()
                val weekListHolder3 = ArrayList<String>()

                // Warm-Up
                weekListHolder1.apply {
                    add(headersText[0])
                    addAll(getWeekPercents(liftMax, percentHolder[0]!![0]))
                }
                weekData[0] = weekListHolder1

                // Core
                weekListHolder2.apply {
                    add(headersText[1])
                    addAll(getWeekPercents(liftMax, percentHolder[1]!![i]))
                }

                weekData[1] = weekListHolder2

                // BBB
                weekListHolder3.apply {
                    if (fslSwap) add(AppConstants.SET_FSL) else add(headersText[2])
                    if (fslSwap) {
                        addAll(getFslPercents(liftMax, percentHolder[1]!![0]))
                    } else {
                        addAll(getWeekPercents(liftMax, percentHolder[2]!![0]))
                    }
                }
                weekData[2] = weekListHolder3

                weightBreakdown[i] = arrayListOf(
                        weekData[0],
                        weekData[1],
                        weekData[2]
                )
            } else {
                val weekListHolder1 = ArrayList<String>()
                val weekListHolder2 = ArrayList<String>()

                // Deload
                weekListHolder1.apply {
                    add(headersText[0])
                    addAll(getWeekPercents(liftMax, percentHolder[0]!![0]))
                }
                weekData[0] = weekListHolder1

                if (extraDeload) {
                    weekListHolder2.apply {
                        add(headersText[1])
                        addAll(getWeekPercents(liftMax, percentHolder[1]!![0]))
                    }
                }
                weekData[1] = weekListHolder2

                weightBreakdown[i] = arrayListOf(
                        weekData[0],
                        if (extraDeload) weekData[1] else null
                )
            }
            databaseRepository.setWeekData(i, weekData)
            tempBDHolder.addAll(createWeightBreakdown(i, weightBreakdown))
            tempHolder.add("")
            tempHolder.addAll(repTextHolder[i])
            databaseRepository.setWeightBreakdown(i, tempBDHolder)

        }

        databaseRepository.resetRepData()
        databaseRepository.setRepData(tempHolder)
        Log.d("TestingData", "Rep data ${databaseRepository.getRepData()}")

        view?.setLastWeek(0)
        view?.updateWeekFragment()
    }

    private fun getRepText(): ArrayList<ArrayList<String>> {
        return arrayListOf(
                arrayListOf("1x5", "1x5", "1x3"),
                if (altFormat) {
                    arrayListOf("1x8", "1x6", "1x3+")
                } else {
                    arrayListOf("1x5", "1x3", "1x1+")
                },
                if (fslSwap) {
                    if (altFormat) {
                        arrayListOf("1x8 or 1x8+", "1x8 ", "1x8", "1x8")
                    } else {
                        arrayListOf("1x5 or 1x5+", "1x5", "1x5", "1x5")
                    }
                } else {
                    arrayListOf("1x10", "1x10", "1x10", "1x10", "1x10")
                },
                if (altFormat) {
                    arrayListOf("1x8", "1x8", "1x8")
                } else {
                    arrayListOf("1x5", "1x5", "1x5")
                }
        )
    }

    private fun createWeightBreakdown(key: Int, weightBreakdown: HashMap<Int, ArrayList<ArrayList<String>?>>): ArrayList<ArrayList<Double>> {
        val weights = weightBreakdown[key]
        val weightListHolder = ArrayList<Double>()

        if (weights != null) {
            val size = weights.size

            for (i in 0 until size) {
                var iterator = 1
                val numSize = weights[i]?.size ?: 1

                while (iterator < numSize) {
                    val data = weights[i]?.get(iterator)
                    if (data != null) {
                        weightListHolder.add(data.toDouble())
                    }
                    iterator++
                }
            }
        }
        return convertToBreakdown(weightListHolder)
    }

    private fun convertToBreakdown(weightListHolder: ArrayList<Double>): ArrayList<ArrayList<Double>> {
        val arrayToSend = ArrayList<ArrayList<Double>>()
        for ((i,value) in weightListHolder.withIndex()) {
            when(i) {
                0,3,6 -> arrayToSend.add(ArrayList())
            }
            appUtils.apply {
                resetWeightArray()
                calculateWeightBreakdown(value, true)
            }
            arrayToSend.add(appUtils.getWeightArray()!!)
        }
        return arrayToSend
    }

    private fun getWeekPercents(liftMax: Int, weekPercents: ArrayList<Float>): ArrayList<String> {
        val liftsAsStrings = ArrayList<String>()
        for (percent in weekPercents) {
            liftsAsStrings.add(appUtils.getWeight(usingKG, liftMax, percent))
        }
        return liftsAsStrings
    }

    private fun getFslPercents(liftMax: Int, weekPercents: ArrayList<Float>): ArrayList<String> {
        val liftsAsStrings = ArrayList<String>()
        for (i in 0..3) {
            liftsAsStrings.add(appUtils.getWeight(usingKG, liftMax, weekPercents[0]))
        }
        return liftsAsStrings
    }

    override fun onPrefUpdate(mContext: Context, liftName: String, preferences: MutableMap<String, *>) {
        resetFragmentValues()
        refresh = true
        onViewCreated(mContext, liftName)
    }

    private fun resetFragmentValues() {
        percentHolder.clear()
    }

    override fun onDestroy() {
        this.view = null
        appUtils.resetWeightArray()
    }
}