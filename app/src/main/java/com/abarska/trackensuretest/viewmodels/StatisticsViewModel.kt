package com.abarska.trackensuretest.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abarska.trackensuretest.database.GasStationDatabase
import com.abarska.truckensuretest.util.DatabaseJoinUtilityClasses

class StatisticsViewModel(app: Application) : AndroidViewModel(app) {

    private val fuelingActDao = GasStationDatabase.getInstance(app).fuelingActDao
    val joinedDataDatabase: LiveData<List<DatabaseJoinUtilityClasses>> =
        fuelingActDao.getStationSpendLiter()
}

class StatisticsViewModelFactory(
    private val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            return StatisticsViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}