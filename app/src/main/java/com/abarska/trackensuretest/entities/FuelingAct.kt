package com.abarska.trackensuretest.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

const val FUELING_ACT_TABLE = "fueling_act_table"
const val FUELING_ACT_ID = "fueling_act_id"
const val UNIT_OF_MEASUREMENT = "unit_of_measurement"
const val PRICE_PER_UNIT = "price_per_unit"
const val NUMBER_OF_UNITS = "number_of_units"
const val FOREIGN_KEY_STATION_ID = "foreign_key_station_id"

@Entity(
    tableName = FUELING_ACT_TABLE,
    foreignKeys = [ForeignKey(
        entity = Station::class,
        parentColumns = arrayOf(STATION_ID),
        childColumns = arrayOf(FOREIGN_KEY_STATION_ID),
        onDelete = ForeignKey.CASCADE
    )]
)
data class FuelingAct(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = FUELING_ACT_ID)
    val dateTime: Long,

    @NonNull
    @ColumnInfo(name = UNIT_OF_MEASUREMENT)
    val unitOfMeasurement: String,

    @NonNull
    @ColumnInfo(name = PRICE_PER_UNIT)
    val pricePerUnit: Double,

    @NonNull
    @ColumnInfo(name = NUMBER_OF_UNITS)
    val numberOfUnits: Double,

    @NonNull
    @ColumnInfo(name = FOREIGN_KEY_STATION_ID)
    val stationId: String
)