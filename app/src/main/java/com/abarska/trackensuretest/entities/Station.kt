package com.abarska.trackensuretest.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val STATION_TABLE = "station_table"
const val STATION_ID = "id"
const val STATION_NAME = "station_name"
const val LAST_EDIT = "last_edit"

@Entity(tableName = STATION_TABLE)
data class Station(

    @PrimaryKey
    @ColumnInfo(name = STATION_ID)
    val id: String,

    @NonNull
    @ColumnInfo(name = STATION_NAME)
    var stationName: String,

    @NonNull
    @ColumnInfo(name = LAST_EDIT)
    var lastEdit: Long
)