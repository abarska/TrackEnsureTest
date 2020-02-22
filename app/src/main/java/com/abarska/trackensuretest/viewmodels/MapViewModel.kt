package com.abarska.trackensuretest.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abarska.trackensuretest.database.GasStationDatabase
import com.abarska.trackensuretest.entities.Station
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MapViewModel(val app: Application) :
    AndroidViewModel(app) {

    private val stationDao = GasStationDatabase.getInstance(app).stationDao

    private var viewModelJob = Job()
    val ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    fun getStationById(id: String): Station? {
        var station : Station? = null
        ioScope.launch {
            station = stationDao.getStationById(id)
        }
        return station
    }

    fun insertIntoDatabase(newStation: Station) {
        ioScope.launch {
            stationDao.insert(newStation)
        }
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