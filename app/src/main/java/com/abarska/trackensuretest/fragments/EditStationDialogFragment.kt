package com.abarska.trackensuretest.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.adapters.LISTVIEWMODEL_SERIALIZABLE
import com.abarska.trackensuretest.adapters.POSITION
import com.abarska.trackensuretest.entities.Station
import com.abarska.trackensuretest.viewmodels.ListViewModel

class EditStationDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val listViewModel = ViewModelProvider(this).get(ListViewModel::class.java)

        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_station, null)

        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_name_edittext)

        var station: Station? = null
        arguments?.let {
            val position = it.getInt(POSITION)
            station =
                (it.getSerializable(LISTVIEWMODEL_SERIALIZABLE) as ListViewModel).stations.value?.get(
                    position
                )
            nameEditText.setText(station?.stationName)
        }

        val dialog = AlertDialog.Builder(activity!!)
            .setTitle(activity!!.getString(R.string.edit_station_header))
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.edit_dialog_dismiss_button)?.setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<TextView>(R.id.edit_dialog_save_button)?.setOnClickListener {
            val newName = nameEditText.text.toString()
            if (TextUtils.isEmpty(newName)) {
                nameEditText.error = activity!!.getString(R.string.empty_field_warning)
            } else {
                val updated = Station(station!!.id, newName, System.currentTimeMillis())
                listViewModel.editStationInRoom(updated)
                listViewModel.editStationInFirebase(updated)
                dialog.dismiss()
            }
        }

        return dialog
    }
}