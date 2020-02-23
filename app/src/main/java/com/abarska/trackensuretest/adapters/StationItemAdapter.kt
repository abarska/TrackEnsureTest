package com.abarska.trackensuretest.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.entities.Station
import com.abarska.trackensuretest.fragments.EditStationDialogFragment
import com.abarska.trackensuretest.viewmodels.ListViewModel

const val POSITION = "position"
const val LISTVIEWMODEL_SERIALIZABLE = "listviewmodel_serializable"

class StationItemAdapter(
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
            val dialog = EditStationDialogFragment()
            val args = Bundle()
            args.putInt(POSITION, position)
            args.putSerializable(LISTVIEWMODEL_SERIALIZABLE, listViewModel)
            dialog.arguments = args
            dialog.show(
                activity.supportFragmentManager,
                "EditStationDialogFragment"
            )
        }

        holder.view.findViewById<ImageView>(R.id.btn_delete_station).setOnClickListener {
            listViewModel.deleteStation(item.id)
        }
    }
}

class StationItemViewHolder(val view: View) : RecyclerView.ViewHolder(view)