package com.abarska.trackensuretest.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.abarska.trackensuretest.database.GasStationDatabase
import com.abarska.trackensuretest.entities.Station
import kotlinx.coroutines.launch
import java.io.Serializable

class ListViewModel(val app: Application) : AndroidViewModel(app), Serializable{

    private val stationDao = GasStationDatabase.getInstance(app).stationDao

    val stations = stationDao.getAllStations()

    fun deleteStation(key: String) {
        viewModelScope.launch { stationDao.delete(key) }
    }

    fun editStation(updatedStation: Station) {
        viewModelScope.launch { stationDao.update(updatedStation) }
    }
}