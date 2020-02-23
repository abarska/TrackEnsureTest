package com.abarska.trackensuretest.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.abarska.trackensuretest.R
import com.abarska.truckensuretest.util.DatabaseJoinUtilityClasses

class StatisticsItemAdapter(
    private val activity: FragmentActivity
) : RecyclerView.Adapter<StatisticsItemViewHolder>() {

    var data = listOf<DatabaseJoinUtilityClasses>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.statistics_data_item, parent, false)
        return StatisticsItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatisticsItemViewHolder, position: Int) {
        val item = data[position]
        holder.itemView.findViewById<TextView>(R.id.alias_textview).text = item.stationName
        holder.itemView.findViewById<TextView>(R.id.total_liters_textview).text =
            activity.getString(R.string.total_fuel_consumed, item.totalLiters)
        holder.itemView.findViewById<TextView>(R.id.total_spend_textview).text =
            activity.getString(R.string.total_money_spent, item.totalSpend)
    }
}

class StatisticsItemViewHolder(val view: View) : RecyclerView.ViewHolder(view)