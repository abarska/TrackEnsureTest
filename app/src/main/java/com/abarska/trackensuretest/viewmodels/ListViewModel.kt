package com.abarska.trackensuretest.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.database.GasStationDatabase
import com.abarska.trackensuretest.entities.STATION_TABLE
import com.abarska.trackensuretest.entities.Station
import com.abarska.trackensuretest.utils.hasInternetConnection
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.launch
import java.io.Serializable

class ListViewModel(val app: Application) : AndroidViewModel(app), Serializable {

    private val stationDao = GasStationDatabase.getInstance(app).stationDao

    val stations = stationDao.getAllStations()

    private val database = FirebaseFirestore.getInstance()

    fun deleteStationInRoom(key: String) {
        viewModelScope.launch { stationDao.delete(key) }
    }

    fun deleteStationInFirebase(id: String) {
        if (hasInternetConnection(app)) {
            deleteNow(id)
        } else {
            launchServiceToDelete(id)
        }
    }

    fun editStationInRoom(station: Station) {
        viewModelScope.launch { stationDao.update(station) }
    }

    fun editStationInFirebase(station: Station) {
        if (hasInternetConnection(app)) {
            updateNow(station)
        } else {
            launchServiceToUpdate(station)
        }
    }

    private fun updateNow(station: Station) {
        val docRef = database
            .collection(app.applicationContext.getString(R.string.stations))
            .document(station.id)
        docRef.set(station).addOnSuccessListener {
            Toast.makeText(app.applicationContext, R.string.updated, Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteNow(id: String) {
        val docRef = database
            .collection(app.applicationContext.getString(R.string.stations))
            .document(id)
        val subCollection =
            docRef.collection(app.applicationContext.getString(R.string.fueling_acts))
        subCollection.get().addOnCompleteListener { task ->
            task.let {
                for (doc in task.result!!) {
                    subCollection.document(doc.id).delete()
                }
            }
        }
        docRef.delete().addOnSuccessListener {
            Toast.makeText(app.applicationContext, R.string.deleted, Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchServiceToUpdate(station: Station) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun launchServiceToDelete(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}