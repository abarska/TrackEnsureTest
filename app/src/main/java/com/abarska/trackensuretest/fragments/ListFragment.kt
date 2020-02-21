package com.abarska.trackensuretest.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.activities.MapActivity
import com.abarska.trackensuretest.adapters.StationAdapter
import com.abarska.trackensuretest.databinding.FragmentListBinding
import com.abarska.trackensuretest.viewmodels.ListViewModel
import com.abarska.trackensuretest.viewmodels.ListViewModelFactory

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

        val app = requireNotNull(this.activity).application
        val factory = ListViewModelFactory(app)
        val listViewModel = ViewModelProviders.of(this, factory).get(ListViewModel::class.java)

        val adapter = StationAdapter()
        listViewModel.stations.observe(viewLifecycleOwner, Observer { list ->
            list?.let {
                adapter.data = list
            }
        })
        binding.stationListRecycler.adapter = adapter

        binding.addStationFab.setOnClickListener {
            startActivity(Intent(activity, MapActivity::class.java))
        }

        return binding.root
    }
}