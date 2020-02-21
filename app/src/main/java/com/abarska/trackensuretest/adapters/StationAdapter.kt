package com.abarska.trackensuretest.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.entities.Station

class StationAdapter : RecyclerView.Adapter<ItemViewHolder>() {

    var data = listOf<Station>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = data[position]
        val textView = holder.view.findViewById<TextView>(R.id.station_item_textview)
        textView.text = item.stationName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.station_list_item, parent, false)
        return ItemViewHolder(view)
    }
}

class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view)