package com.abarska.trackensuretest.viewmodels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.database.GasStationDatabase
import com.abarska.trackensuretest.entities.FuelingAct
import com.abarska.trackensuretest.entities.Station
import com.abarska.trackensuretest.utils.hasInternetConnection
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
    }

    fun uploadToFirebase(station: Station, fuelingAct: FuelingAct) {
        if (hasInternetConnection(app)) {
            uploadNow(station, fuelingAct)
        } else {
            launchService(station, fuelingAct)
        }
    }

    private fun uploadNow(station: Station, fuelingAct: FuelingAct) {

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

    private fun launchService(station: Station, fuelingAct: FuelingAct) {
        Log.i("MY_TAG", app.applicationContext.getString(R.string.will_be_saved_later))
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

