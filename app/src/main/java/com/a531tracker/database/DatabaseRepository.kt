package com.a531tracker.database

import android.content.Context
import com.a531tracker.ObjectBuilders.AsManyRepsAsPossible
import com.a531tracker.ObjectBuilders.CompoundLifts
import com.a531tracker.ObjectBuilders.GraphDataHolder
import com.a531tracker.tools.AppConstants

class DatabaseRepository(mContext: Context) {

    private var db: DatabaseHelper = DatabaseHelper(mContext)

    fun getDataRepo(mContext: Context) : DatabaseRepository {
        return DatabaseRepository(mContext)
    }

    fun checkCycle(): Boolean {
        return db.startCycle()
    }

    fun getLift(liftName: String): CompoundLifts? {
        return db.getLifts(liftName)
    }

    fun setWeekData(key: Int, value: HashMap<Int, ArrayList<String>>) {
        weeklyData[key] = value
    }

    fun getWeekData(key: Int): HashMap<Int, ArrayList<String>>? {
        return weeklyData[key]
    }

    fun resetRepData() {
        repData.clear()
    }

    fun setRepData(value: ArrayList<String>) {
        repData.addAll(value)
    }

    fun getRepData(): ArrayList<String> {
        return repData
    }

    fun setWeightBreakdown(key: Int, value: ArrayList<ArrayList<Double>>) {
        breakdownData[key] = value
    }

    fun getWeightBreakdown(key: Int): ArrayList<ArrayList<Double>> {
        return breakdownData[key] ?: ArrayList()
    }

    fun getCycle(): Int {
        return db.cycle
    }

    fun updateCycle() {
        db.updateCycle(getCycle())
    }

    fun updateAll(compound: CompoundLifts): Int {
        db.createAMRAPTable(getCycle(), compound.compound)
        return db.updateCompoundStats(compound)
    }

    fun getTrainingMaxes(): HashMap<String, Int> {
        for (liftName in AppConstants.LIFT_ACCESS_LIST) {
            val mapName = AppConstants.LIFT_ACCESS_MAP[liftName] ?: liftName
            trainingMaxes[mapName] = db.getLifts(liftName)?.trainingMax ?: 100
        }
        return trainingMaxes
    }

    fun updatePercent(compound: CompoundLifts): Int {
        return db.updateBBBPercent(compound)
    }

    fun getUserPercentList(liftName: String): ArrayList<Float> {
        val percent = db.getLifts(liftName).percent ?: 0.50f
        return arrayListOf(percent, percent, percent, percent, percent)
    }

    fun setAmrapValue(liftName: String, amrapPercent: String, repsDone: Int, weight: Int): Int {
        return db.updateAMRAPTable(liftName, getCycle(), amrapPercent, repsDone, weight)
    }

    fun amrapGraphValue(liftName: String, cycle: Int): AsManyRepsAsPossible? {
        var amrapVal = AsManyRepsAsPossible()
        try {
            amrapVal = db.getAMRAPValues(liftName, cycle)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return amrapVal
    }

    fun saveAmrapGraph(key: String, value: ArrayList<AsManyRepsAsPossible>, altFormat: Boolean) {
        val graphValList = ArrayList<GraphDataHolder>()
        for (amrap in value) {
            val graphVal = GraphDataHolder(0F,0F,0F,0F,"")
            graphVal.getFromAmrap(amrap = amrap, altFormat = altFormat)

            graphValList.add(graphVal)
        }
        amrapGraphMap[key] = graphValList
    }

    fun getAmrapGraph(key: String): ArrayList<GraphDataHolder>? {
        return amrapGraphMap[key]
    }

    fun getAmrapValue(liftName: String, weekNum: Int): Int {
        var amrapValues: AsManyRepsAsPossible? = null
        try {
            amrapValues = db.getAMRAPValues(liftName, getCycle() - 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return when (weekNum) {
            1 -> amrapValues?.eighty_five_reps ?: 5
            2 -> amrapValues?.ninety_reps ?: 3
            3 -> amrapValues?.ninety_five_reps ?: 1
            else -> -1
        }
    }

    fun checkCurrentAmrap(liftName: String, weekNum: Int): Int {
        var amrapValues: AsManyRepsAsPossible? = null
        try {
            amrapValues = db.getAMRAPValues(liftName, getCycle())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return when (weekNum) {
            0 -> amrapValues?.eighty_five_reps ?: 5
            1 -> amrapValues?.ninety_reps ?: 3
            2 -> amrapValues?.ninety_five_reps ?: 1
            else -> -1
        }
    }

    fun setCompletedLifts(liftWeight: Int) {
        val cycle = getCycle()
        for (liftName in AppConstants.LIFT_ACCESS_LIST) {
            val mapName = AppConstants.LIFT_ACCESS_MAP[liftName] ?: liftName
            val amrapData = db.checkForMissing(liftName, cycle, liftWeight)

            compileCompletedLifts(mapName, amrapData)
        }
    }

    fun getCompletedLifts(): HashMap<String, HashMap<Int, Int>> {
        return completedLifts
    }

    fun getAllAmrapValues(liftName: String): AsManyRepsAsPossible? {
        return db.getAMRAPValues(liftName, getCycle())
    }

    fun resetLifts(newUser: Boolean) {
        if(newUser) {
            db.onNewUser(db.readableDatabase)
            for (liftName in AppConstants.LIFT_ACCESS_LIST) {
                db.createAMRAPTable(getCycle(), liftName)
                db.createAMRAPTable(getCycle() - 1, liftName)
                for (value in amrapPercents) {
                    newUserAmrap(liftName, getCycle() - 1, value, -1, 100)
                }
            }
        } else {
            db.onResetLifts(db.readableDatabase)
        }
    }

    private fun newUserAmrap(liftName: String, cycle: Int, amrapPercent: String, repsDone: Int, weight: Int): Int {
        return db.updateAMRAPTable(liftName, cycle, amrapPercent, repsDone, weight)
    }

    fun inputLifts(compound: CompoundLifts) {
        db.insertCompoundStats(compound)
    }

    fun delete() {
        db.deleteAllData(db.readableDatabase)
        db.onNewUser(db.readableDatabase)
    }

    private fun compileCompletedLifts(mapName: String, amrapData: AsManyRepsAsPossible) {
        val amrapList = arrayListOf(amrapData.eighty_five_reps!!, amrapData.ninety_reps!!, amrapData.ninety_five_reps!!)
        val tempMap = HashMap<Int, Int>()
        for (counter in 0 until 3) {
            tempMap[counter + 1] = amrapList[counter]
        }
        completedLifts[mapName] = tempMap
    }

    companion object {
        internal val weeklyData: HashMap<Int, HashMap<Int, ArrayList<String>>> = HashMap()

        internal val repData: ArrayList<String> = ArrayList()

        internal val breakdownData: HashMap<Int, ArrayList<ArrayList<Double>>> = HashMap()

        internal val trainingMaxes: HashMap<String, Int> = HashMap()

        internal val completedLifts: HashMap<String, HashMap<Int, Int>> = HashMap()

        internal val amrapPercents = arrayListOf<String>("0.85", "0.90", "0.95")

        internal val amrapGraphMap: HashMap<String, ArrayList<GraphDataHolder>> = HashMap()
    }
}