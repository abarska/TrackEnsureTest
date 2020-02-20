package com.abarska.trackensuretest

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class TabFragmentPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> StationListFragment()
            1 -> StationStatisticsFragment()
            else -> throw IllegalArgumentException("wrong tab position")
        }
    }

    override fun getCount(): Int {
        return 2
    }
}