package com.a531tracker.adapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.a531tracker.week.WeekFragment

class WorkoutPagerAdapter(fragmentActivity: FragmentActivity,
                          private val sevenWeek: Boolean,
                          private val cycleNum: Int,
                          private val liftName: String,
                          private val swapLiftName: String
) : FragmentStateAdapter(fragmentActivity)  {

    override fun getItemCount(): Int {
        return if(sevenWeek && cycleNum%2 != 0) 3 else 4
    }

    override fun createFragment(position: Int): Fragment {
        return WeekFragment(weekToShow = position, liftName = liftName, swapLiftName = swapLiftName).newInstance(position = position)
    }
}
