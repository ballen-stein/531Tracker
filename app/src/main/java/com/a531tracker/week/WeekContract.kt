package com.a531tracker.week

import android.content.Context
import com.a531tracker.mvpbase.BasePresenter
import com.a531tracker.mvpbase.BaseView

class WeekContract {
    interface Presenter : BasePresenter {
        fun onViewCreated(mContext: Context, liftName: String)
    }

    interface View : BaseView<Presenter> {
        fun updateWeekFragment()
        fun error(throwable: Throwable)
    }
}