package com.abarska.trackensuretest.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fueling_act_table")
// add foreign keys syntax
data class FuelingAct(

    @PrimaryKey(autoGenerate = true)
    val dateTime: Long,

    @NonNull
    val pricePerGalon: Double,

    @NonNull
    val numberOfGalons: Double,

    @NonNull
    //@ForeignKey
    val stationId: Long
)