package com.abarska.trackensuretest.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abarska.trackensuretest.database.GasStationDatabase

class ListViewModel(val app: Application) : AndroidViewModel(app) {

    private val stationDao = GasStationDatabase.getInstance(app).stationDao
    val stations = stationDao.getAllStations()
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