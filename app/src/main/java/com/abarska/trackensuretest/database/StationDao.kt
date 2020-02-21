package com.abarska.trackensuretest.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.abarska.trackensuretest.entities.STATION_TABLE
import com.abarska.trackensuretest.entities.Station

@Dao
interface StationDao {

    @Insert
    suspend fun insert(station: Station)

    @Update
    fun update(station: Station)

    @Query("SELECT COUNT (stationId) FROM $STATION_TABLE")
    suspend fun getCount(): Int

    @Query("SELECT * FROM $STATION_TABLE WHERE stationId = :key")
    fun getStation(key: Long): LiveData<Station>

    @Query("SELECT * FROM $STATION_TABLE ORDER BY stationId DESC")
    fun getAllStations(): LiveData<List<Station>>

    @Query("DELETE FROM $STATION_TABLE")
    fun clearAll()

    @Query("DELETE FROM $STATION_TABLE WHERE stationId = :key")
    fun delete(key: Long)
}