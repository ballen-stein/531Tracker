package com.a531tracker.lifts

import android.content.Context
import com.a531tracker.ObjectBuilders.LiftBuilder
import com.a531tracker.mvpbase.BasePresenter
import com.a531tracker.mvpbase.BaseView

class SetLiftsContract {
    interface Presenter : BasePresenter {
        fun onViewCreated(mContext: Context, freshLaunch: Boolean)
        fun saveLiftValues(freshLaunch: Boolean, tempBuilder: LiftBuilder, setHidePref: Boolean)
    }

    interface View : BaseView<Presenter> {
        fun setCurrentLifts(tmHolder: HashMap<String, String>, currentPercent: Int)
        fun finished(freshLaunch: Boolean)
        fun error(throwable: Throwable)
    }
}