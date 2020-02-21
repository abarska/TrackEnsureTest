package com.abarska.trackensuretest.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abarska.trackensuretest.R
import com.abarska.trackensuretest.entities.Station

class StationAdapter : RecyclerView.Adapter<TextItemViewHolder>() {

    var data = listOf<Station>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
        val item = data[position]
        holder.textView.text = item.stationName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(
                R.layout.station_list_item,
                parent,
                false
            ) as TextView
        return TextItemViewHolder(view)
    }
}

class TextItemViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)