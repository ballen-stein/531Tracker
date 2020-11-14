package com.a531tracker.lifts

import android.content.Context
import android.util.Log
import com.a531tracker.ObjectBuilders.CompoundLifts
import com.a531tracker.ObjectBuilders.LiftBuilder
import com.a531tracker.R
import com.a531tracker.database.DatabaseRepository
import com.a531tracker.mvpbase.DependencyInjectorClass
import com.a531tracker.tools.AppConstants
import com.a531tracker.tools.AppUtils
import com.a531tracker.tools.PreferenceUtils


class SetLiftsPresenter(view: SetLiftsContract.View,
                        injector: DependencyInjectorClass,
                        appInjector: AppUtils,
                        private val preferenceUtils: PreferenceUtils,
                        private val mContext: Context) : SetLiftsContract.Presenter {

    private var databaseRepository: DatabaseRepository = injector.dataRepo(mContext)

    private var appUtils: AppUtils = appInjector.getInstance()

    private var view: SetLiftsContract.View? = view

    private val tmHolder: HashMap<String, String> = HashMap()

    override fun onViewCreated(mContext: Context, freshLaunch: Boolean) {
        databaseRepository.getDataRepo(mContext = mContext)
        getCurrentTM(freshLaunch)

        view?.setCurrentLifts(tmHolder)
    }

    private fun getCurrentTM(freshLaunch: Boolean) {
        for (liftName in AppConstants.LIFT_ACCESS_LIST) {
            if (!freshLaunch) {
                val compound = databaseRepository.getLift(liftName = liftName)
                tmHolder[liftName] = if (preferenceUtils.getPreference(mContext.getString(R.string.preference_kilogram_key)) == true) {
                    appUtils.getWeight(true, compound?.trainingMax ?: 100, 1.0f)
                } else {
                    (compound?.trainingMax ?: 100).toString()
                }
            } else {
                tmHolder[liftName] = "100"
            }
        }
    }

    override fun saveLiftValues(freshLaunch: Boolean, tempBuilder: LiftBuilder) {
        if (freshLaunch) {
            databaseRepository.resetLifts(true)
            for (liftName in AppConstants.LIFT_ACCESS_LIST) {
                val compound: CompoundLifts = CompoundLifts()
                compound.fromLiftBuilder(tempBuilder, liftName)
                Log.d("TestingData", "Compound is $compound and Fresh")
                databaseRepository.inputLifts(compound)
            }
        } else {
            databaseRepository.resetLifts(false)
            for (liftName in AppConstants.LIFT_ACCESS_LIST) {
                val compound: CompoundLifts = CompoundLifts()
                compound.fromLiftBuilder(tempBuilder, liftName)
                Log.d("TestingData", "Compound is $compound and Stale")
                databaseRepository.inputLifts(compound)
            }
        }
        view?.finished(freshLaunch)
    }

    override fun onDestroy() {
        view = null
    }

}