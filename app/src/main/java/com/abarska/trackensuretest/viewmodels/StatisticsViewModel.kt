package com.abarska.trackensuretest.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.abarska.trackensuretest.database.GasStationDatabase
import com.abarska.truckensuretest.util.DatabaseJoinUtilityClasses

class StatisticsViewModel(app: Application) : AndroidViewModel(app) {

    private val fuelingActDao = GasStationDatabase.getInstance(app).fuelingActDao

    val joinedDataDatabase: LiveData<List<DatabaseJoinUtilityClasses>> =
        fuelingActDao.getStationSpendLiter()
}