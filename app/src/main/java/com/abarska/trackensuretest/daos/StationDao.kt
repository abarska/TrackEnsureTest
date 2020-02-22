package com.abarska.trackensuretest.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.abarska.trackensuretest.entities.LAST_EDIT
import com.abarska.trackensuretest.entities.STATION_ID
import com.abarska.trackensuretest.entities.STATION_TABLE
import com.abarska.trackensuretest.entities.Station

@Dao
interface StationDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(station: Station)

    @Update
    suspend fun update(station: Station)

    @Query("SELECT * FROM $STATION_TABLE ORDER BY $LAST_EDIT DESC")
    fun getAllStations(): LiveData<List<Station>>

    @Query("SELECT $STATION_ID FROM $STATION_TABLE ORDER BY $LAST_EDIT DESC")
    fun getAllStationIds(): LiveData<List<String>>

    @Query("SELECT * FROM $STATION_TABLE WHERE $STATION_ID = :key")
    fun getStationById(key: String): LiveData<Station>

    @Query("DELETE FROM $STATION_TABLE WHERE $STATION_ID = :key")
    suspend fun delete(key: String)
}