package com.abarska.trackensuretest.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.databinding.ActivityMapBinding
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
import com.google.android.gms.maps.model.PointOfInterest

const val REQUEST_LOCATION_PERMISSION = 101
const val DEFAULT_ZOOM = 15F

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
                    "access to your location is needed to display map",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
//        if (requestCode == REQUEST_INTERNET_PERMISSION) {
//            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                // use internet to access places
//                Toast.makeText(
//                    baseContext,
//                    "internet permission granted",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else {
//                Toast.makeText(
//                    baseContext,
//                    "internet access is needed to access gas stations info",
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
            showAddStationDialog(poi)
        }
    }

    private fun showAddStationDialog(poi: PointOfInterest) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_station, null)
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.add_station_header))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.button_save)) { _, _ ->
                val stationId = poi.placeId
                val name =
                    dialogView.findViewById<EditText>(R.id.station_name_edittext).text.toString()
                val lat = poi.latLng.latitude
                val lng = poi.latLng.longitude
                val supplier =
                    dialogView.findViewById<EditText>(R.id.fuel_supplier_edittext).text.toString()
                mapViewModel.insertIntoDatabase(Station(stationId, name, lat, lng, supplier))
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
//            Toast.makeText(
//                baseContext,
//                "internet permission granted",
//                Toast.LENGTH_SHORT
//            ).show()
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
