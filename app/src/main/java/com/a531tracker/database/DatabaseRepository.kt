package com.a531tracker.database

import android.content.Context
import com.a531tracker.ObjectBuilders.CompoundLifts

class DatabaseRepository (mContext: Context) {

    private var db: DatabaseHelper = DatabaseHelper(mContext)

    fun getDataRepo(mContext: Context) : DatabaseRepository {
        return DatabaseRepository(mContext)
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

    companion object {
        internal val weeklyData: HashMap<Int, HashMap<Int, ArrayList<String>>> = HashMap()

        internal val repData: ArrayList<String> = ArrayList()

        internal val breakdownData: HashMap<Int, ArrayList<ArrayList<Double>>> = HashMap()
    }
}