package com.a531tracker.week

import android.content.Context
import android.content.res.Resources
import com.a531tracker.database.DatabaseRepository
import com.a531tracker.mvpbase.DependencyInjectorClass
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

//@AndroidEntryPoint
class WeekPresenter (view: WeekContract.View, injector: DependencyInjectorClass) : WeekContract.Presenter {

    private var databaseRepository: DatabaseRepository = injector.dataRepo()

    private var view: WeekContract.View? = view

    //@Inject
    //lateinit var databaseRepository: DatabaseRepository

    override fun onViewCreated(mContext: Context, liftName: String) {
        databaseRepository.getDataRepo(mContext = mContext)

        getLift(liftName = liftName)
    }

    private fun getLift(liftName: String) {
        val liftData = databaseRepository.getLift(liftName = liftName)

        if (liftData != null) {
            view?.updateWeekFragment()
        } else {
            view?.error(Resources.NotFoundException())
        }
    }

    override fun onDestroy() {
        this.view = null
    }

}