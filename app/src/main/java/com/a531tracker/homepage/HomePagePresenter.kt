package com.a531tracker.homepage

import android.content.Context
import android.util.Log
import com.a531tracker.ObjectBuilders.AsManyRepsAsPossible
import com.a531tracker.ObjectBuilders.LiftBuilder
import com.a531tracker.R
import com.a531tracker.database.DatabaseRepository
import com.a531tracker.mvpbase.DependencyInjectorClass
import com.a531tracker.tools.AppConstants
import com.a531tracker.tools.AppUtils
import com.a531tracker.tools.PreferenceUtils

class HomePagePresenter(view: HomePageContract.View, injector: DependencyInjectorClass, appInjector: AppUtils, private val mContext: Context) : HomePageContract.Presenter {
    private var databaseRepository: DatabaseRepository = injector.dataRepo(mContext)

    private var appUtils: AppUtils = appInjector.getInstance()

    private var view: HomePageContract.View? = view

    private val hashHolder: HashMap<String, AsManyRepsAsPossible> = HashMap()

    private var altFormat: Boolean = false

    override fun onViewCreated(mContext: Context) {
        databaseRepository.getDataRepo(mContext = mContext)
        altFormat = PreferenceUtils.getInstance(mContext = mContext).getPreference(mContext.getString(R.string.preference_split_variant_extra_key)) ?: false
        getHashObserverValues()
        getAmrapNumbers()

        view?.setCycle(databaseRepository.getCycle().toString())
        view?.showHomeFragments()
        view?.setHashObserver(hashHolder)
    }

    override fun checkForUpdatedLifts(hashHolder: HashMap<String, AsManyRepsAsPossible>) {
        if (hashHolder.isNotEmpty()) {
            getHashObserverValues()
            getAmrapNumbers()
            compareMaps(hashHolder, this.hashHolder)
        } else {
            onViewCreated(mContext)
        }
    }

    override fun getDataForUpdate(): LiftBuilder {
        val localHolder = ArrayList<Int>()
        for (liftName in AppConstants.LIFT_ACCESS_LIST) {
            val compound = databaseRepository.getLift(liftName)
            localHolder.add(compound?.trainingMax ?: 100)
        }
        return LiftBuilder(
                benchTm = localHolder[0],
                squatTm = localHolder[1],
                ohpTm = localHolder[2],
                dlTm = localHolder[3],
                percent = 1.0F,
                usingTm = true
        )
    }

    override fun updatePercent(percent: Float) {
        val normalizedPercent = appUtils.normalizePercent(percent) / 100
        var success = 0
        for (liftName in AppConstants.LIFT_ACCESS_LIST) {
            val compound = databaseRepository.getLift(liftName)!!
            compound.percent = normalizedPercent
            success = databaseRepository.updatePercent(compound)
        }
        view?.showSnack(success==1)
    }

    private fun getHashObserverValues() {
        for (liftName in AppConstants.LIFT_ACCESS_LIST) {
            val tempLift = databaseRepository.getAllAmrapValues(liftName)!!
            hashHolder[liftName] = tempLift
        }
    }

    private fun getAmrapNumbers() {
        val cycle = databaseRepository.getCycle()
        for (compound in AppConstants.LIFT_ACCESS_LIST) {
            val dataList = ArrayList<AsManyRepsAsPossible>()
            for (i in 1 until cycle) {
                databaseRepository.amrapGraphValue(compound, i)?.let { dataList.add(it) }
            }
            databaseRepository.saveAmrapGraph(compound, dataList, altFormat = altFormat)
        }
    }

    private fun compareMaps(hashHolderFromHome: HashMap<String, AsManyRepsAsPossible>, hashHolderFromPresenter: HashMap<String, AsManyRepsAsPossible>) {
        for (liftName in AppConstants.LIFT_ACCESS_LIST) {
            if (hashHolderFromHome[liftName]!!.compare(hashHolderFromPresenter[liftName]!!)) {
                Log.d("TestingData", "Holder's don't match!")
                onViewCreated(mContext)
                break
            }
        }
    }

    override fun onDestroy() {
        this.view = null
    }

}