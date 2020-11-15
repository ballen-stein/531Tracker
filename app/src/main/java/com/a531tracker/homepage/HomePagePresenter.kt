package com.a531tracker.homepage

import android.content.Context
import android.util.Log
import com.a531tracker.ObjectBuilders.AsManyRepsAsPossible
import com.a531tracker.ObjectBuilders.CompoundLifts
import com.a531tracker.ObjectBuilders.LiftBuilder
import com.a531tracker.database.DatabaseRepository
import com.a531tracker.mvpbase.DependencyInjectorClass
import com.a531tracker.tools.AppConstants
import com.a531tracker.tools.AppUtils

class HomePagePresenter(view: HomePageContract.View, injector: DependencyInjectorClass, appInjector: AppUtils, private val mContext: Context) : HomePageContract.Presenter {
    private var databaseRepository: DatabaseRepository = injector.dataRepo(mContext)

    private var appUtils: AppUtils = appInjector.getInstance()

    private var view: HomePageContract.View? = view

    private val hashHolder: HashMap<String, AsManyRepsAsPossible> = HashMap()

    override fun onViewCreated(mContext: Context) {
        databaseRepository.getDataRepo(mContext = mContext)
        getHashObserverValues()

        view?.setCycle(databaseRepository.getCycle().toString())
        view?.showHomeFragments()
        view?.setHashObserver(hashHolder)
    }

    override fun checkForUpdatedLifts(hashHolder: HashMap<String, AsManyRepsAsPossible>) {
        if (hashHolder.isNotEmpty()) {
            getHashObserverValues()
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