package com.abarska.trackensuretest.viewmodels

import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.database.GasStationDatabase
import com.abarska.trackensuretest.entities.FuelingAct
import com.abarska.trackensuretest.entities.Station
import com.abarska.trackensuretest.services.FirebaseJobService
import com.abarska.trackensuretest.utils.hasInternetConnection
import com.abarska.trackensuretest.utils.upload
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.Serializable

const val UPLOAD_JOB_ID = 123

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
        } else {
            launchUploadService(station, fuelingAct)
        }
    }

    private fun launchUploadService(station: Station, fuelingAct: FuelingAct) {

        val gson = Gson()
        val strinGsonStation = gson.toJson(station)
        val stringGsonFuelingAct = gson.toJson(fuelingAct)
        val bundle = PersistableBundle()
        bundle.putString(app.applicationContext.getString(R.string.stations), strinGsonStation)
        bundle.putString(
            app.applicationContext.getString(R.string.fueling_acts),
            stringGsonFuelingAct
        )

        val componentName = ComponentName(app.applicationContext, FirebaseJobService::class.java)
        val jobInfo = JobInfo.Builder(UPLOAD_JOB_ID, componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
            .setPersisted(true)
            .setPeriodic(15 * 60 * 1000)
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

