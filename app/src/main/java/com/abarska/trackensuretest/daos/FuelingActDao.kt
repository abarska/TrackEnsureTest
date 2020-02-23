package com.abarska.trackensuretest.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abarska.trackensuretest.entities.*
import com.abarska.truckensuretest.util.DatabaseJoinUtilityClasses

@Dao
interface FuelingActDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(act: FuelingAct)

    @Query(
        "SELECT $STATION_TABLE.$STATION_NAME AS stationName, " +
                "SUM($FUELING_ACT_TABLE.$TOTAL_SPEND) AS totalSpend, " +
                "SUM($FUELING_ACT_TABLE.$NUMBER_OF_LITERS) AS totalLiters " +
                "FROM $STATION_TABLE LEFT JOIN $FUELING_ACT_TABLE " +
                "ON $STATION_TABLE.$STATION_ID = $FUELING_ACT_TABLE.$FOREIGN_KEY_STATION_ID " +
                "GROUP BY $FUELING_ACT_TABLE.$FOREIGN_KEY_STATION_ID " +
                "ORDER BY totalSpend DESC;"
    )
    fun getStationSpendLiter(): LiveData<List<DatabaseJoinUtilityClasses>>
}