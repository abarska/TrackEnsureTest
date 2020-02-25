package com.abarska.trackensuretest.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.abarska.trackensuretest.database.GasStationDatabase
import com.abarska.trackensuretest.entities.Station
import com.abarska.trackensuretest.utils.hasInternetConnection
import kotlinx.coroutines.launch
import java.io.Serializable

class ListViewModel(val app: Application) : AndroidViewModel(app), Serializable {

    private val stationDao = GasStationDatabase.getInstance(app).stationDao

    val stations = stationDao.getAllStations()

    fun deleteStationInRoom(key: String) {
        viewModelScope.launch { stationDao.delete(key) }
    }

    fun deleteStationInFirebase(id: String) {
        if (hasInternetConnection(app)) {
            deleteNow(id)
        } else {
            launchServiceToDelete(id)
        }
    }

    fun editStationInRoom(station: Station) {
        viewModelScope.launch { stationDao.update(station) }
    }

    fun editStationInFirebase(station: Station) {
        if (hasInternetConnection(app)) {
            updateNow(station)
        } else {
            launchServiceToUpdate(station)
        }
    }

    private fun updateNow(station: Station) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun deleteNow(id: String){
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun launchServiceToUpdate(station: Station) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun launchServiceToDelete(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}