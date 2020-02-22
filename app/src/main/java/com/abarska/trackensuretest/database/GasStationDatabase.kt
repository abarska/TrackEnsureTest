package com.abarska.trackensuretest.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.abarska.trackensuretest.daos.FuelingActDao
import com.abarska.trackensuretest.daos.StationDao
import com.abarska.trackensuretest.entities.FuelingAct
import com.abarska.trackensuretest.entities.Station

const val DATABASE_NAME = "favorite_gas_stations_database"

@Database(entities = [Station::class, FuelingAct::class], version = 1, exportSchema = false)
abstract class GasStationDatabase : RoomDatabase() {

    abstract val stationDao: StationDao
    abstract val fuelingActDao: FuelingActDao

    companion object {

        @Volatile
        private var INSTANCE: GasStationDatabase? = null

        fun getInstance(context: Context): GasStationDatabase {

            synchronized(this) {

                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        GasStationDatabase::class.java,
                        DATABASE_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}