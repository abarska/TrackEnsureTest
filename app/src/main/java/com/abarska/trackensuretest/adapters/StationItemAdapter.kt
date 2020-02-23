package com.abarska.trackensuretest.adapters

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.entities.Station
import com.abarska.trackensuretest.viewmodels.ListViewModel

class StationAdapter(
    private val activity: FragmentActivity,
    private val listViewModel: ListViewModel
) :
    RecyclerView.Adapter<StationItemViewHolder>() {

    var data = listOf<Station>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.station_data_item, parent, false)
        return StationItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: StationItemViewHolder, position: Int) {

        val item = data[position]

        holder.view.findViewById<TextView>(R.id.station_item_textview).text = item.stationName

        holder.view.findViewById<ImageView>(R.id.btn_edit_station).setOnClickListener {
            showEditStationDialog(item)
        }

        holder.view.findViewById<ImageView>(R.id.btn_delete_station).setOnClickListener {
            listViewModel.deleteStation(item.id)
        }
    }

    private fun showEditStationDialog(station: Station) {

        val dialogView = activity.layoutInflater.inflate(R.layout.dialog_edit_station, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_name_edittext)
        nameEditText.setText(station.stationName)

        val dialog = AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.edit_station_header))
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.edit_dialog_dismiss_button)?.setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<TextView>(R.id.edit_dialog_save_button)?.setOnClickListener {
            val newName = nameEditText.text.toString()
            if (TextUtils.isEmpty(newName)) {
                nameEditText.error = activity.getString(R.string.empty_field_warning)
            } else {
                listViewModel.editStation(Station(station.id, newName, System.currentTimeMillis()))
                dialog.dismiss()
            }
        }

        dialog.show()
    }
}

class StationItemViewHolder(val view: View) : RecyclerView.ViewHolder(view)