package com.abarska.trackensuretest.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.abarska.trackensuretest.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("MY_TAG", "before setContentView")
        setContentView(R.layout.activity_map)
        Log.i("MY_TAG", "after setContentView")

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        val ods = LatLng(46.477541, 30.746085)
        val zoomLevel = 15f
        map.addMarker(MarkerOptions().position(ods).title("Marker in Odessa"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(ods, zoomLevel))
    }
}