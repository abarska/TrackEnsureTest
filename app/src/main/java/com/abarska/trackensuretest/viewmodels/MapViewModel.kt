package com.abarska.trackensuretest.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.database.GasStationDatabase
import com.abarska.trackensuretest.entities.FuelingAct
import com.abarska.trackensuretest.entities.Station
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.io.Serializable

class MapViewModel(val app: Application) : AndroidViewModel(app), Serializable {

    private val stationDao = GasStationDatabase.getInstance(app).stationDao
    private val fuelingActDao = GasStationDatabase.getInstance(app).fuelingActDao
    var currentStation: LiveData<Station> = stationDao.getStationById("")

    fun changeCurrentStation(stationId: String) {
        currentStation = stationDao.getStationById(stationId)
    }

    fun insertIntoDatabase(newStation: Station, newFuelingAct: FuelingAct, isNewStation: Boolean) {
        viewModelScope.launch {
            if (isNewStation) stationDao.insert(newStation)
            else stationDao.update(newStation)
            fuelingActDao.insert(newFuelingAct)
        }
        Toast.makeText(app.applicationContext, R.string.saved_to_local_database, Toast.LENGTH_SHORT)
            .show()
    }

    fun uploadToFirebase(station: Station, fuelingAct: FuelingAct) {
        if (hasInternetConnection()) uploadNow(station, fuelingAct)
        else launchService(station, fuelingAct)
    }

    // start from here
    private fun hasInternetConnection(): Boolean {
        val connectivity = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networks = connectivity.allNetworks
        var networkInfo: NetworkInfo
        for (n in networks) {
            networkInfo = connectivity.getNetworkInfo(n);
            if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                return true;
            }
        }
        Toast.makeText(app.applicationContext, "error", Toast.LENGTH_SHORT).show();
        return false;
    }

    fun uploadNow(station: Station, fuelingAct: FuelingAct) {

        val db = FirebaseFirestore.getInstance()

        val stationsRef = db.collection(app.applicationContext.getString(R.string.stations))
        stationsRef.add(station)

        val fuelingActRef = stationsRef
            .document(station.id)
            .collection(app.applicationContext.getString(R.string.fueling_acts))
        fuelingActRef.add(fuelingAct)

        Log.i("MY_TAG", "act = ${fuelingActRef.document().get()}")

        Toast.makeText(
            app.applicationContext,
            R.string.saved_to_remote_database,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun launchService(station: Station, fuelingAct: FuelingAct) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}