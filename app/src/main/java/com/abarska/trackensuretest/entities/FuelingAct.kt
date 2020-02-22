package com.abarska.trackensuretest.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

const val FUELING_ACT_TABLE = "fueling_act_table"
const val FUELING_ACT_ID = "fueling_act_id"
const val FUEL_TYPE = "fuel_type"
const val PRICE_PER_LITER = "price_per_liter"
const val NUMBER_OF_LITERS = "number_of_liters"
const val TOTAL_SPEND = "total_spend"
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

    @PrimaryKey
    @ColumnInfo(name = FUELING_ACT_ID)
    val dateTime: Long,

    @NonNull
    @ColumnInfo(name = FUEL_TYPE)
    val fuelType: String,

    @NonNull
    @ColumnInfo(name = PRICE_PER_LITER)
    val pricePerLiter: Double,

    @NonNull
    @ColumnInfo(name = NUMBER_OF_LITERS)
    val numberOfLiters: Double,

    @NonNull
    @ColumnInfo(name = TOTAL_SPEND)
    val totalSpend: Double,

    @NonNull
    @ColumnInfo(name = FOREIGN_KEY_STATION_ID)
    val stationId: String
)