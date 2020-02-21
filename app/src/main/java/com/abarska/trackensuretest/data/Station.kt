package com.abarska.trackensuretest.data

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "station_table")
data class Station(

    @PrimaryKey(autoGenerate = true)
    val stationId: Long,

    @NonNull
    var stationName: String,

    @NonNull
    val lat: Double,

    @NonNull
    val lng: Double,

    @NonNull
    var fuelProvider: String
)