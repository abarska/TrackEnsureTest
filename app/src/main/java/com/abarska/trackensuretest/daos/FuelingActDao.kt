package com.abarska.trackensuretest.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.abarska.trackensuretest.entities.FUELING_ACT_ID
import com.abarska.trackensuretest.entities.FUELING_ACT_TABLE
import com.abarska.trackensuretest.entities.FuelingAct

@Dao
interface FuelingActDao {

    @Insert
    suspend fun insert(act: FuelingAct)

    @Query("SELECT COUNT ($FUELING_ACT_ID) FROM $FUELING_ACT_TABLE")
    suspend fun getCount(): Int
}