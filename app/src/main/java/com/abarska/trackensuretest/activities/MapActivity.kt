package com.abarska.trackensuretest.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.entities.FuelingAct
import com.abarska.trackensuretest.entities.Station
import com.abarska.trackensuretest.viewmodels.MapViewModel
import com.abarska.truckensuretest.util.DecimalInputFilter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

const val REQUEST_LOCATION_PERMISSION = 101
const val DEFAULT_ZOOM = 15F

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var mapViewModel: MapViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        map.setOnPoiClickListener { poi -> showAddRecordDialog(poi.placeId) }
        enableMyLocation()
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

        val dialogView = layoutInflater.inflate(R.layout.dialog_add_record, null)

        val stationNameEditText = dialogView.findViewById<EditText>(R.id.station_name_edittext)

        val pricePerLiterEditText = dialogView.findViewById<EditText>(R.id.price_per_liter_edittext)
        pricePerLiterEditText.filters = arrayOf<InputFilter>(DecimalInputFilter(3, 2))

        val numberOfLitersEditText =
            dialogView.findViewById<EditText>(R.id.number_of_liters_edittext)

        val fuelTypes = resources.getStringArray(R.array.types_of_fuel)
        val fuelAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, fuelTypes)
        val fuelTypeSpinner = dialogView.findViewById<Spinner>(R.id.fuel_type_spinner)
        fuelTypeSpinner.adapter = fuelAdapter

        var isNewStation = true

        mapViewModel.currentStation.observe(this, Observer { station ->
            station?.let {
                stationNameEditText.setText(station.stationName)
                isNewStation = false
            }
        })

        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.add_record))
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.add_dialog_dismiss_button).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<TextView>(R.id.add_dialog_save_button).setOnClickListener {

            val name = stationNameEditText.text.toString()
            val price = pricePerLiterEditText.text.toString()
            val liters = numberOfLitersEditText.text.toString()

            val ts = System.currentTimeMillis()
            val type = fuelTypes[fuelTypeSpinner.selectedItemPosition]

            when {
                TextUtils.isEmpty(name) -> stationNameEditText.error =
                    getString(R.string.empty_field_warning)
                TextUtils.isEmpty(price) -> pricePerLiterEditText.error =
                    getString(R.string.empty_field_warning)
                TextUtils.isEmpty(liters) -> numberOfLitersEditText.error =
                    getString(R.string.empty_field_warning)
                else -> {
                    val totalSpend = price.toDouble() * liters.toInt()
                    mapViewModel.insertIntoDatabase(
                        Station(placeId, name, ts),
                        FuelingAct(ts, type, price.toDouble(), liters.toInt(), totalSpend, placeId),
                        isNewStation
                    )
                    Toast.makeText(this, R.string.saved_to_database, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }
}

