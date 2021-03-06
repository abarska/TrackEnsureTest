package com.abarska.trackensuretest.viewmodels

import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.database.GasStationDatabase
import com.abarska.trackensuretest.entities.Station
import com.abarska.trackensuretest.services.FirebaseJobService
import com.abarska.trackensuretest.utils.*
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.Serializable

const val DELETE_JOB_ID = 122
const val UPDATE_JOB_ID = 123

class ListViewModel(val app: Application) : AndroidViewModel(app), Serializable {

    private val stationDao = GasStationDatabase.getInstance(app).stationDao
    val stations = stationDao.getAllStations()

    fun deleteStationInRoom(key: String) {
        viewModelScope.launch { stationDao.delete(key) }
    }

    fun deleteStationInFirebase(station: Station) {
        if (hasInternetConnection(app)) {
            delete(app, station)
        } else {
            launchDeleteService(prepareBundle(station))
            app.getString(R.string.will_be_deleted_later).showToast(app.baseContext)
        }
    }

    fun editStationInRoom(station: Station) {
        viewModelScope.launch { stationDao.update(station) }
    }

    fun editStationInFirebase(station: Station) {
        if (hasInternetConnection(app)) {
            update(app, station)
        } else {
            launchUpdateService(prepareBundle(station))
            app.getString(R.string.will_be_updated_later).showToast(app.baseContext)
        }
    }

    private fun launchUpdateService(bundle: PersistableBundle) {
        val componentName = ComponentName(app.applicationContext, FirebaseJobService::class.java)
        val jobInfo = JobInfo.Builder(UPDATE_JOB_ID, componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)
            .setExtras(bundle)
            .build()
        val scheduler = app.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val result = scheduler.schedule(jobInfo)
        checkResult(result)
    }

    private fun launchDeleteService(bundle: PersistableBundle) {
        val componentName = ComponentName(app.applicationContext, FirebaseJobService::class.java)
        val jobInfo = JobInfo.Builder(DELETE_JOB_ID, componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)
            .setExtras(bundle)
            .build()
        val scheduler = app.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val result = scheduler.schedule(jobInfo)
        checkResult(result)
    }

    private fun prepareBundle(station: Station): PersistableBundle {
        val gson = Gson()
        val jsonStation = gson.toJson(station)
        val bundle = PersistableBundle()
        bundle.putString(app.applicationContext.getString(R.string.stations), jsonStation)
        return bundle
    }

    private fun checkResult(resultCode: Int) {
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            app.baseContext.getString(R.string.job_scheduled).showInfoLog()
        } else {
            app.baseContext.getString(R.string.job_scheduling_failed).showInfoLog()
        }
    }
}