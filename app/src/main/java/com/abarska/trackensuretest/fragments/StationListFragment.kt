package com.abarska.trackensuretest.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.activities.MapActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StationListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_list, container, false)
        val recycler = rootView.findViewById<RecyclerView>(R.id.station_list_recycler)
        val fab = rootView.findViewById<FloatingActionButton>(R.id.add_station_fab)
        fab.setOnClickListener {
            startActivity(Intent(activity, MapActivity::class.java))
            Log.i("MY_TAG", "FAB was clicked")
        }
        return rootView
    }
}