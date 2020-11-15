package com.a531tracker.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.a531tracker.homepage.HomePageFragment
import com.a531tracker.homepage.ToolsPageFragment

class HomepagePagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity)  {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            HomePageFragment().newInstance()
        } else {
            ToolsPageFragment().newInstance()
        }
    }
}
