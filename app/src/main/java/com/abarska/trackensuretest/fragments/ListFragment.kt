package com.abarska.trackensuretest.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.activities.MapActivity
import com.abarska.trackensuretest.adapters.StationItemAdapter
import com.abarska.trackensuretest.databinding.FragmentListBinding
import com.abarska.trackensuretest.viewmodels.ListViewModel

class ListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<FragmentListBinding>(
            inflater,
            R.layout.fragment_list,
            container,
            false
        )
        binding.lifecycleOwner = this

        val listViewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        val adapter = StationItemAdapter(activity!!, listViewModel)

        listViewModel.stations.observe(viewLifecycleOwner, Observer { list ->
            list?.let { adapter.data = list }
        })
        binding.stationListRecycler.adapter = adapter

        binding.addStationFab.setOnClickListener {
            startActivity(Intent(activity, MapActivity::class.java))
        }

        return binding.root
    }
}