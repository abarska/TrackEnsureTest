package com.abarska.trackensuretest.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.entities.FuelingAct
import com.abarska.trackensuretest.entities.Station
import com.google.firebase.firestore.FirebaseFirestore

fun hasInternetConnection(app: Application): Boolean {
    val cm = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = cm.activeNetwork
        val connection = cm.getNetworkCapabilities(network)
        return connection != null && (connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))

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
            if (connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                isCellularEnabled = true
                break
            }
        }
        return connection != null && (isWifiEnabled || isCellularEnabled)
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
            app.applicationContext.getString(R.string.saved_to_remote_database).showInfoLog()
        }.addOnFailureListener {
            app.applicationContext.getString(R.string.error_saving).showInfoLog()
        }
}

fun delete(app: Application, station: Station) {
    val db = FirebaseFirestore.getInstance()
    val docRef =
        db.collection(app.applicationContext.getString(R.string.stations)).document(station.id)
    val subCollection = docRef.collection(app.applicationContext.getString(R.string.fueling_acts))
    subCollection.get().addOnCompleteListener { task ->
        task.let {
            for (doc in task.result!!) {
                subCollection.document(doc.id).delete()
            }
        }
    }
    docRef.delete().addOnSuccessListener {
        app.applicationContext.getString(R.string.deleted_in_remote_database).showInfoLog()
    }.addOnFailureListener {
        app.applicationContext.getString(R.string.error_deleting).showInfoLog()
    }
}

fun update(app: Application, station: Station) {
    val db = FirebaseFirestore.getInstance()
    val docRef =
        db.collection(app.applicationContext.getString(R.string.stations)).document(station.id)
    docRef.set(station).addOnSuccessListener {
        app.applicationContext.getString(R.string.updated_in_remote_database).showInfoLog()

    }.addOnFailureListener {
        app.applicationContext.getString(R.string.error_updating).showInfoLog()
    }
}

