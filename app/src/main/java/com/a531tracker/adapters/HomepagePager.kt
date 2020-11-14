package com.a531tracker.adapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.a531tracker.homepage.HomePageFragment

class HomepagePagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity)  {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        Log.d("TestingData", "Position on homepage : $position")
        return if (position == 0) {
            HomePageFragment().newInstance()
        } else {
            HomePageFragment().newInstance()
        }
    }
}
