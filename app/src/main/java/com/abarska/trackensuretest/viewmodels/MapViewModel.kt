package com.abarska.trackensuretest.viewmodels

import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.database.GasStationDatabase
import com.abarska.trackensuretest.entities.FuelingAct
import com.abarska.trackensuretest.entities.Station
import com.abarska.trackensuretest.services.FirebaseJobService
import com.abarska.trackensuretest.utils.hasInternetConnection
import com.abarska.trackensuretest.utils.showToast
import com.abarska.trackensuretest.utils.upload
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.Serializable

const val UPLOAD_JOB_ID = 121

class MapViewModel(val app: Application) : AndroidViewModel(app), Serializable {

    private val stationDao = GasStationDatabase.getInstance(app).stationDao
    private val fuelingActDao = GasStationDatabase.getInstance(app).fuelingActDao
    var currentStation: LiveData<Station> = stationDao.getStationById("")

    fun changeCurrentStation(stationId: String) {
        currentStation = stationDao.getStationById(stationId)
    }

    fun insertIntoDatabase(newStation: Station, newFuelingAct: FuelingAct, isNewStation: Boolean) {
        viewModelScope.launch {
            if (isNewStation) stationDao.insert(newStation)
            else stationDao.update(newStation)
            fuelingActDao.insert(newFuelingAct)
        }
    }

    fun uploadToFirebase(station: Station, fuelingAct: FuelingAct) {
        if (hasInternetConnection(app)) {
            upload(app, station, fuelingAct)
            app.getString(R.string.saved).showToast(app.baseContext)
        } else {
            launchUploadService(station, fuelingAct)
            app.getString(R.string.will_be_saved_later).showToast(app.baseContext)
        }
    }

    private fun launchUploadService(station: Station, fuelingAct: FuelingAct) {

        val gson = Gson()
        val jsonStation = gson.toJson(station)
        val jsonFuelingAct = gson.toJson(fuelingAct)
        val bundle = PersistableBundle()
        bundle.putString(app.applicationContext.getString(R.string.stations), jsonStation)
        bundle.putString(app.applicationContext.getString(R.string.fueling_acts), jsonFuelingAct)

        val componentName = ComponentName(app.applicationContext, FirebaseJobService::class.java)
        val jobInfo = JobInfo.Builder(UPLOAD_JOB_ID, componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)
            .setExtras(bundle)
            .build()
        val scheduler = app.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val resultCode = scheduler.schedule(jobInfo)
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.i("MY_TAG", "job scheduled")
        } else {
            Log.i("MY_TAG", "job scheduling failed")
        }
    }
}

