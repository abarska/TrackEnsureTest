package com.abarska.trackensuretest.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.fragments.ListFragment
import com.abarska.trackensuretest.fragments.StationStatisticsFragment

const val PAGE_COUNT = 2

class TabFragmentPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ListFragment()
            1 -> StationStatisticsFragment()
            else -> throw IllegalArgumentException("wrong tab position")
        }
    }

    override fun getCount() = PAGE_COUNT

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.favorites_tab_header)
            1 -> context.getString(R.string.statistics_tab_header)
            else -> throw IllegalArgumentException("wrong tab position")
        }
    }
}