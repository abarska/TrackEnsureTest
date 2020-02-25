package com.abarska.trackensuretest.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.activities.PLACE_ID
import com.abarska.trackensuretest.entities.FuelingAct
import com.abarska.trackensuretest.entities.Station
import com.abarska.trackensuretest.viewmodels.MapViewModel
import com.abarska.truckensuretest.util.DecimalInputFilter

class AddRecordDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_add_record, null)

        var placeId: String? = null
        arguments?.let {
            placeId = it.getString(PLACE_ID)
            mapViewModel.changeCurrentStation(placeId!!)
        }

        val stationNameEditText = dialogView.findViewById<EditText>(R.id.station_name_edittext)

        val pricePerLiterEditText = dialogView.findViewById<EditText>(R.id.price_per_liter_edittext)
        pricePerLiterEditText.filters = arrayOf<InputFilter>(DecimalInputFilter(3, 2))

        val numberOfLitersEditText =
            dialogView.findViewById<EditText>(R.id.number_of_liters_edittext)

        val fuelTypes = resources.getStringArray(R.array.types_of_fuel)
        val fuelAdapter =
            ArrayAdapter<String>(
                context!!,
                android.R.layout.simple_spinner_dropdown_item,
                fuelTypes
            )
        val fuelTypeSpinner = dialogView.findViewById<Spinner>(R.id.fuel_type_spinner)
        fuelTypeSpinner.adapter = fuelAdapter

        var isNewStation = true

        mapViewModel.currentStation.observe(this, Observer { station ->
            station?.let {
                stationNameEditText.setText(station.stationName)
                isNewStation = false
            }
        })

        val dialog = AlertDialog.Builder(context!!)
            .setTitle(getString(R.string.add_record))
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.add_dialog_dismiss_button).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<TextView>(R.id.add_dialog_save_button).setOnClickListener {

            val name = stationNameEditText.text.toString()
            val price = pricePerLiterEditText.text.toString()
            val liters = numberOfLitersEditText.text.toString()

            when {
                TextUtils.isEmpty(name) -> stationNameEditText.error =
                    getString(R.string.empty_field_warning)
                TextUtils.isEmpty(price) -> pricePerLiterEditText.error =
                    getString(R.string.empty_field_warning)
                TextUtils.isEmpty(liters) -> numberOfLitersEditText.error =
                    getString(R.string.empty_field_warning)
                else -> {
                    val timeStamp = System.currentTimeMillis()
                    val type = fuelTypes[fuelTypeSpinner.selectedItemPosition]
                    val totalSpend = price.toDouble() * liters.toInt()
                    val newStation = Station(placeId!!, name, timeStamp)
                    val newFuelingAct = FuelingAct(
                        timeStamp,
                        type,
                        price.toDouble(),
                        liters.toInt(),
                        totalSpend,
                        placeId!!
                    )
                    mapViewModel.insertIntoDatabase(newStation, newFuelingAct, isNewStation)
                    mapViewModel.uploadToFirebase(newStation, newFuelingAct)
                    dialog.dismiss()
                }
            }
        }

        return dialog
    }
}