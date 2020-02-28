package com.abarska.trackensuretest.services

import android.app.job.JobParameters
import android.app.job.JobService
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.entities.FuelingAct
import com.abarska.trackensuretest.entities.Station
import com.abarska.trackensuretest.utils.*
import com.abarska.trackensuretest.viewmodels.DELETE_JOB_ID
import com.abarska.trackensuretest.viewmodels.UPDATE_JOB_ID
import com.abarska.trackensuretest.viewmodels.UPLOAD_JOB_ID
import com.google.gson.Gson

class FirebaseJobService : JobService() {

    private var isJobCanceled = false

    override fun onStartJob(params: JobParameters?): Boolean {
        "onStartJob".showInfoLog()
        doBackgroundWork(params)
        return true
    }

    private fun doBackgroundWork(params: JobParameters?) {
        if (isJobCanceled || params == null) return

        val gson = Gson()
        val jsonStation =
            params.extras.getString(applicationContext.getString(R.string.stations))
        val station = gson.fromJson(jsonStation, Station::class.java)

        Thread(Runnable {
            "on background thread".showInfoLog()
            when (params.jobId) {
                UPLOAD_JOB_ID -> {
                    val jsonFuelingAct =
                        params.extras.getString(applicationContext.getString(R.string.fueling_acts))
                    val fuelingAct = gson.fromJson(jsonFuelingAct, FuelingAct::class.java)
                    upload(application, station, fuelingAct)
                }
                DELETE_JOB_ID -> delete(application, station)
                UPDATE_JOB_ID -> update(application, station)
                else -> throw IllegalArgumentException("unknown job id")
            }
            jobFinished(params, false)
        }).start()
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        "onStopJob".showInfoLog()
        isJobCanceled = true
        return true
    }
}