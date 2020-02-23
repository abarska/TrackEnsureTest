package com.abarska.trackensuretest.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.adapters.StatisticsItemAdapter
import com.abarska.trackensuretest.databinding.FragmentStatisticsBinding
import com.abarska.trackensuretest.viewmodels.StatisticsViewModel

class StatisticsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<FragmentStatisticsBinding>(
            inflater,
            R.layout.fragment_statistics,
            container,
            false
        )
        binding.lifecycleOwner = this

        val statisticsViewModel = ViewModelProvider(this).get(StatisticsViewModel::class.java)
        val adapter = StatisticsItemAdapter(activity!!)

        statisticsViewModel.joinedDataDatabase.observe(viewLifecycleOwner, Observer { list ->
            list?.let { adapter.data = list }
        })
        binding.statisticsListRecycler.adapter = adapter

        return binding.root
    }
}