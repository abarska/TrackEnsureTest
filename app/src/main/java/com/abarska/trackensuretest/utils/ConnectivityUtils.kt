package com.abarska.trackensuretest.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.entities.FuelingAct
import com.abarska.trackensuretest.entities.Station
import com.google.firebase.firestore.FirebaseFirestore

fun hasInternetConnection(app: Application): Boolean {

    val cm = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = cm.activeNetwork
        val connection = cm.getNetworkCapabilities(network)
        return connection != null && connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)

//                (connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
//                        connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))

    } else {

        val allNetworks = cm.allNetworks
        var connection: NetworkCapabilities? = null
        var isWifiEnabled = false
        var isCellularEnabled = false

        for (network in allNetworks) {
            connection = cm.getNetworkCapabilities(network)

            if (connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                isWifiEnabled = true
                break
            }
//            if (connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
//                isCellularEnabled = true
//                break
//            }
        }
        return connection != null && isWifiEnabled
        //(isWifi || isCellular)
    }
}

fun upload(app: Application, station: Station, fuelingAct: FuelingAct) {

    val db = FirebaseFirestore.getInstance()

    val stationsRef = db.collection(app.applicationContext.getString(R.string.stations))
    stationsRef.document(station.id).set(station)

    val fuelingActRef = stationsRef.document(station.id)
    fuelingActRef.collection(app.applicationContext.getString(R.string.fueling_acts))
        .document(fuelingAct.dateTime.toString())
        .set(fuelingAct)
        .addOnSuccessListener {
            Toast.makeText(app.applicationContext, R.string.saved, Toast.LENGTH_SHORT).show()
        }
}