package com.abarska.trackensuretest.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abarska.trackensuretest.database.GasStationDatabase
import com.abarska.trackensuretest.entities.FuelingAct
import com.abarska.trackensuretest.entities.Station
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MapViewModel(val app: Application) : AndroidViewModel(app) {

    private var viewModelJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    private val stationDao = GasStationDatabase.getInstance(app).stationDao
    private val fuelingActDao = GasStationDatabase.getInstance(app).fuelingActDao
    var currentStation: LiveData<Station> = stationDao.getStationById("")

    fun insertIntoDatabase(newStation: Station, newFuelingAct: FuelingAct, isNewStation: Boolean) {
        ioScope.launch {
            if (isNewStation) stationDao.insert(newStation)
            else stationDao.update(newStation)
            fuelingActDao.insert(newFuelingAct)
        }
    }

    fun changeCurrentStation(stationId: String) {
        currentStation = stationDao.getStationById(stationId)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

class MapViewModelFactory(private val app: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}