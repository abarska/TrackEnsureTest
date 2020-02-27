package com.abarska.trackensuretest.services

import android.app.job.JobParameters
import android.app.job.JobService
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.entities.FuelingAct
import com.abarska.trackensuretest.entities.Station
import com.abarska.trackensuretest.utils.delete
import com.abarska.trackensuretest.utils.sendNotification
import com.abarska.trackensuretest.utils.update
import com.abarska.trackensuretest.utils.upload
import com.abarska.trackensuretest.viewmodels.DELETE_JOB_ID
import com.abarska.trackensuretest.viewmodels.UPDATE_JOB_ID
import com.abarska.trackensuretest.viewmodels.UPLOAD_JOB_ID
import com.google.gson.Gson

class FirebaseJobService : JobService() {

    private var isJobCanceled = false

    override fun onStartJob(params: JobParameters?): Boolean {
        doBackgroundWork(params)
        return true
    }

    private fun doBackgroundWork(params: JobParameters?) {
        if (isJobCanceled && params == null) return

        val gson = Gson()
        val jsonStation =
            (params as JobParameters).extras.getString(applicationContext.getString(R.string.stations))
        val station = gson.fromJson(jsonStation, Station::class.java)

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
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        isJobCanceled = true
        return true
    }
}