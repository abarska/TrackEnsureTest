package com.abarska.trackensuretest.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abarska.trackensuretest.database.GasStationDatabase
import com.abarska.trackensuretest.entities.JoinStationSpendLiterData

class StatisticsViewModel(app: Application) : AndroidViewModel(app) {

    private val fuelingActDao = GasStationDatabase.getInstance(app).fuelingActDao
    val joinedData: LiveData<List<JoinStationSpendLiterData>> =
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