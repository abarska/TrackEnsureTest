package com.abarska.trackensuretest.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.databinding.ActivityMapBinding
import com.abarska.trackensuretest.entities.FuelingAct
import com.abarska.trackensuretest.entities.Station
import com.abarska.trackensuretest.viewmodels.MapViewModel
import com.abarska.trackensuretest.viewmodels.MapViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

const val REQUEST_LOCATION_PERMISSION = 101
const val DEFAULT_ZOOM = 15F
const val INTENT_EXTRA_POI_PLACE_ID = "intent_extra_poi_place_id"

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var mapViewModel: MapViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMapBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_map)
        val factory = MapViewModelFactory(requireNotNull(this).application)
        mapViewModel = ViewModelProviders.of(this, factory).get(MapViewModel::class.java)
        binding.mapViewModel = mapViewModel

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        setPoiClick()
        enableMyLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            } else {
                Toast.makeText(
                    baseContext,
                    R.string.location_access_denied,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
//        if (requestCode == REQUEST_INTERNET_PERMISSION) {
//            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                // use internet to access places
//            } else {
//                Toast.makeText(
//                    baseContext,
//                    R.string.internet_access_denied,
//                    Toast.LENGTH_SHORT
//                ).show()
//                finish()
//            }
//        }
    }

    private fun setPoiClick() {
        map.setOnPoiClickListener { poi ->
            map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            showAddRecordDialog(poi.placeId)
        }
    }

    private fun showAddRecordDialog(placeId: String) {

        mapViewModel.changeCurrentStation(placeId)
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_record, null)

        val fuelSpinner = dialogView.findViewById<Spinner>(R.id.fuel_type_spinner)
        val fuelTypes = application.resources.getStringArray(R.array.types_of_fuel)
        val fuelAdapter = ArrayAdapter<String>(
            baseContext,
            android.R.layout.simple_spinner_dropdown_item,
            fuelTypes
        )
        fuelSpinner.adapter = fuelAdapter

        val unitSpinner = dialogView.findViewById<Spinner>(R.id.unit_of_measurement_spinner)
        val unitTypes = application.resources.getStringArray(R.array.units_of_measurement)
        val unitAdapter = ArrayAdapter<String>(
            baseContext,
            android.R.layout.simple_spinner_dropdown_item,
            unitTypes
        )
        unitSpinner.adapter = unitAdapter

        var isNewStation = true

        mapViewModel.currentStation.observe(this, Observer { station ->
            station?.let {
                dialogView.findViewById<EditText>(R.id.station_name_edittext)
                    .setText(station.stationName)
                isNewStation = false
            }
        })

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.add_record))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.button_save)) { _, _ ->

                val timestamp = System.currentTimeMillis()

                val stationName =
                    dialogView.findViewById<EditText>(R.id.station_name_edittext).text.toString()
                val newStation = Station(placeId, stationName, timestamp)

                val fuelType = fuelTypes[fuelSpinner.selectedItemPosition]
                val unitType = unitTypes[unitSpinner.selectedItemPosition]
                val unitPrice = dialogView.findViewById<EditText>(R.id.price_per_unit_edittext).text.toString().toDouble()
                val numberOfUnits = dialogView.findViewById<EditText>(R.id.number_of_units_edittext).text.toString().toDouble()
                val newFuelingAct = FuelingAct(timestamp, fuelType, unitType, unitPrice, numberOfUnits, placeId)

                mapViewModel.insertIntoDatabase(newStation, newFuelingAct, isNewStation)
                finish()
            }
            .setNegativeButton(getString(R.string.button_dismiss)) { _, _ ->
                return@setNegativeButton
            }.create().show()

    }

    private fun enableMyLocation() {
        if (isLocationPermissionGranted()) {
            map.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(location.latitude, location.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

//    private fun enableInternetConnectivity() {
//        if (isInternetPermissionGranted()) {
//            use internet
//        } else {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.INTERNET),
//                REQUEST_INTERNET_PERMISSION
//            )
//        }
//    }
//
//    private fun isInternetPermissionGranted(): Boolean {
//        return ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.INTERNET
//        ) == PackageManager.PERMISSION_GRANTED
//    }
}
