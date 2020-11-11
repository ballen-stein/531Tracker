package com.a531tracker.week

import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.a531tracker.tools.AppConstants
import com.a531tracker.ObjectBuilders.CompoundLifts
import com.a531tracker.database.DatabaseRepository
import com.a531tracker.mvpbase.DependencyInjectorClass
import com.a531tracker.tools.AppUtils
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class WeekPresenter (view: WeekContract.View, injector: DependencyInjectorClass, appInjector: AppUtils, private val mContext: Context) : WeekContract.Presenter {

    private var databaseRepository: DatabaseRepository = injector.dataRepo(mContext)

    private var appUtils: AppUtils = appInjector.getInstance()

    private var view: WeekContract.View? = view

    override fun onViewCreated(mContext: Context, liftName: String) {
        databaseRepository.getDataRepo(mContext = mContext)

        getLift(liftName = liftName)
    }

    private fun getLift(liftName: String) {
        val liftData = databaseRepository.getLift(liftName = liftName)

        if (liftData != null) {
            saveWeekData(liftData)
        } else {
            view?.error(Resources.NotFoundException())
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
        val liftMax = liftData.training_max
        val tempHolder = ArrayList<String>()

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
                    add(headersText[2])
                    addAll(getWeekPercents(liftMax, percentHolder[2]!![0]))
                }
                weekData[2] = weekListHolder3

                weightBreakdown[i] = arrayListOf(
                        weekData[0],
                        weekData[1],
                        weekData[2]
                )
            } else {
                val weekListHolder1 = ArrayList<String>()

                // Deload
                weekListHolder1.apply {
                    add(headersText[0])
                    addAll(getWeekPercents(liftMax, percentHolder[0]!![0]))
                }
                weekData[0] = weekListHolder1

                weightBreakdown[i] = arrayListOf(
                        weekData[0],
                )
            }

            databaseRepository.setWeekData(i, weekData)
            tempBDHolder.addAll(createWeightBreakdown(i, weightBreakdown))
            tempHolder.add("")
            tempHolder.addAll(repTextHolder[i])
            databaseRepository.setWeightBreakdown(i, tempBDHolder)
        }
        databaseRepository.setRepData(tempHolder)

        view?.updateWeekFragment()
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
            liftsAsStrings.add(appUtils.getWeight(0, liftMax, percent))
        }
        return liftsAsStrings
    }

    override fun onDestroy() {
        this.view = null
        appUtils.resetWeightArray()
        headersText.clear()
        warmupPercents.clear()
        deloadPercents.clear()
        corePercentsBase.clear()
        corePercentsMod.clear()
        boringPercents.clear()
        percentHolder.clear()
    }

    companion object {
        internal var coreSwap = false
        internal var fslSwap = false

        internal val repTextHolder = arrayListOf(
                arrayListOf("1x5", "1x5", "1x3"),
                if (coreSwap) {
                    arrayListOf("1x8", "1x6", "1x3+")
                } else {
                    arrayListOf("1x5", "1x3", "1x1+")
                },
                if (fslSwap) {
                    arrayListOf("1x5", "1x5", "1x5", "1x5", "1x5")
                } else {
                    arrayListOf("1x10", "1x10", "1x10", "1x10", "1x10")
                },
                if (coreSwap) {
                    arrayListOf("1x8", "1x8", "1x8")
                } else {
                    arrayListOf("1x5", "1x5", "1x5")
                },
        )

        internal val headersText = arrayListOf(
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

        internal val percentHolder = HashMap<Int, ArrayList<ArrayList<Float>>>().apply {
            put(0, warmupPercents)
            if (coreSwap) put(1, corePercentsMod) else put(1, corePercentsBase)
            put(2, boringPercents)
            put(3, deloadPercents)
        }
    }

}