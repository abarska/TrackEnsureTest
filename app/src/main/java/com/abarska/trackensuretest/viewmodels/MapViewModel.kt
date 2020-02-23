package com.abarska.trackensuretest.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.abarska.trackensuretest.database.GasStationDatabase
import com.abarska.trackensuretest.entities.FuelingAct
import com.abarska.trackensuretest.entities.Station
import kotlinx.coroutines.launch

class MapViewModel(val app: Application) : AndroidViewModel(app) {

    private val stationDao = GasStationDatabase.getInstance(app).stationDao
    private val fuelingActDao = GasStationDatabase.getInstance(app).fuelingActDao
    var currentStation: LiveData<Station> = stationDao.getStationById("")

    fun insertIntoDatabase(newStation: Station, newFuelingAct: FuelingAct, isNewStation: Boolean) {
        viewModelScope.launch {
            if (isNewStation) stationDao.insert(newStation)
            else stationDao.update(newStation)
            fuelingActDao.insert(newFuelingAct)
        }
    }

    fun changeCurrentStation(stationId: String) {
        currentStation = stationDao.getStationById(stationId)
    }
}