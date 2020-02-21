package com.abarska.trackensuretest.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abarska.trackensuretest.database.GasStationDatabase
import com.abarska.trackensuretest.entities.Station
import kotlinx.coroutines.launch

class ListViewModel(val app: Application) : AndroidViewModel(app) {

    private val stationDao = GasStationDatabase.getInstance(app).stationDao
    val stations = stationDao.getAllStations()

    fun deleteStation(key: String) {
        viewModelScope.launch { stationDao.delete(key) }
    }

    fun editStation(updatedStation: Station) {
        viewModelScope.launch { stationDao.update(updatedStation) }
    }
}

class ListViewModelFactory(
    private val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
            return ListViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}