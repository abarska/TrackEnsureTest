package com.abarska.trackensuretest.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

fun hasInternetConnection(app: Application): Boolean {

    val cm = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = cm.activeNetwork
        val connection = cm.getNetworkCapabilities(network)
        return connection != null && (
                connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))

    } else {

        val allNetworks = cm.allNetworks
        var connection: NetworkCapabilities? = null
        var isWifi = false
        var isCellular = false

        for (network in allNetworks) {
            connection = cm.getNetworkCapabilities(network)

            if (connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                isWifi = true
                break
            }
            if (connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                isCellular = true
                break
            }
        }
        return connection != null && (isWifi || isCellular)
    }
}