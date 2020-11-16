package com.a531tracker.week

import android.content.Context
import com.a531tracker.mvpbase.BasePresenter
import com.a531tracker.mvpbase.BaseView

class WeekContract {
    interface Presenter : BasePresenter {
        fun onViewCreated(mContext: Context, liftName: String, swapLift: String)
        fun onAmrapReceived(liftName: String, percent: String, repsDone: Int)
        fun onPrefUpdate(mContext: Context, liftName: String, preferences: MutableMap<String, *>, swapLift: String)
    }

    interface View : BaseView<Presenter> {
        fun updateWeekFragment()
        fun setLastWeek(int: Int)
        fun amrapSnackbar(success: Boolean)
        fun refresh(refresh: Boolean)
        fun error(throwable: Throwable)
    }
}