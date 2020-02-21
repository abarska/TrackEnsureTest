package com.abarska.trackensuretest.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

const val STATION_TABLE = "station_table"

@Entity(tableName = STATION_TABLE)
data class Station(

    @PrimaryKey
    val stationId: String,

    @NonNull
    var stationName: String,

    @NonNull
    val lat: Double,

    @NonNull
    val lng: Double,

    @NonNull
    var supplier: String
)