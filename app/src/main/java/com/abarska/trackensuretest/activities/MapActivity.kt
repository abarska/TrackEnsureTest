package com.abarska.trackensuretest.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.databinding.DialogAddRecordBinding
import com.abarska.trackensuretest.entities.FuelingAct
import com.abarska.trackensuretest.entities.Station
import com.abarska.trackensuretest.viewmodels.MapViewModel
import com.abarska.trackensuretest.viewmodels.MapViewModelFactory
import com.abarska.truckensuretest.util.DecimalInputFilter
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

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var mapViewModel: MapViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val factory = MapViewModelFactory(requireNotNull(this).application)
        mapViewModel = ViewModelProviders.of(this, factory).get(MapViewModel::class.java)

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

    private fun setPoiClick() {
        map.setOnPoiClickListener { poi ->
            map.addMarker(MarkerOptions().position(poi.latLng).title(poi.name))
            showAddRecordDialog(poi.placeId)
        }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            } else {
                Toast.makeText(baseContext, R.string.location_access_denied, Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    private fun showAddRecordDialog(placeId: String) {

        mapViewModel.changeCurrentStation(placeId)

        val binding = DataBindingUtil.inflate<DialogAddRecordBinding>(
            layoutInflater, R.layout.dialog_add_record, null, false
        )
        binding.pricePerLiterEdittext.filters = arrayOf<InputFilter>(DecimalInputFilter(3, 2))

        val fuelTypes = application.resources.getStringArray(R.array.types_of_fuel)
        val fuelAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, fuelTypes)
        binding.fuelTypeSpinner.adapter = fuelAdapter

        var isNewStation = true

        mapViewModel.currentStation.observe(this, Observer { station ->
            station?.let {
                binding.stationNameEdittext.setText(station.stationName)
                isNewStation = false
            }
        })

        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.add_record))
            .setView(binding.root)
            .setPositiveButton(getString(R.string.button_save)) { _, _ ->

                val name = binding.stationNameEdittext.text.toString()
                val price = binding.pricePerLiterEdittext.text.toString()
                val liters = binding.numberOfLitersEdittext.text.toString()

                if (TextUtils.isEmpty(name)) binding.stationNameEdittext.error =
                    getString(R.string.empty_field_warning)
                if (TextUtils.isEmpty(price)) binding.pricePerLiterEdittext.error =
                    getString(R.string.empty_field_warning)
                if (TextUtils.isEmpty(liters)) binding.stationNameEdittext.error =
                    getString(R.string.empty_field_warning)

                // data validation missing

                val timeStamp = System.currentTimeMillis()
                val totalSpend = price.toDouble() * liters.toInt()
                val fuelType = fuelTypes[binding.fuelTypeSpinner.selectedItemPosition]

                mapViewModel.insertIntoDatabase(
                    Station(placeId, name, timeStamp),
                    FuelingAct(
                        timeStamp,
                        fuelType,
                        price.toDouble(),
                        liters.toInt(),
                        totalSpend,
                        placeId
                    ),
                    isNewStation
                )

                Toast.makeText(this, R.string.saved_to_database, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.button_dismiss)) { _, _ ->
                return@setNegativeButton
            }.create()
    }
}

