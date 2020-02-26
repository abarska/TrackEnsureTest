package com.abarska.trackensuretest.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.entities.FuelingAct
import com.abarska.trackensuretest.entities.Station
import com.abarska.trackensuretest.utils.upload
import com.google.gson.Gson

class FirebaseJobService : JobService() {

    private var isJobCanceled = false

    override fun onStartJob(params: JobParameters?): Boolean {
        doBackgroundWork(params)
        return true
    }

    private fun doBackgroundWork(params: JobParameters?) {
        if (isJobCanceled) return

        val g = Gson()

        val jsonStaion = params?.extras?.getString(applicationContext.getString(R.string.stations))
        val station = g.fromJson(jsonStaion, Station::class.java)

        val jsonFuelingAct =
            params?.extras?.getString(applicationContext.getString(R.string.fueling_acts))
        val fuelingAct = g.fromJson(jsonFuelingAct, FuelingAct::class.java)

        Log.i("MY_TAG", "doing in background")
        upload(application, station, fuelingAct)

        jobFinished(params, false)
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        isJobCanceled = true
        return true
    }
}