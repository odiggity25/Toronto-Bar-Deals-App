package com.tbd.app

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tbd.app.models.Bar
import com.tbd.app.models.Deal
import com.tbd.app.utils.hoursFormattedString

/**
 * Shows the bars deals separated by day of the week in the [BarView]
 * Created by orrie on 2017-07-19.
 */
class BarDealsAdapter(private var context: Context, private val bar: Bar): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Item>()

    init {
        dealsToItems()
    }

    private fun dealsToItems() {
        for (day in 0..6) {
            val dayDeals = bar.deals
                    .filter { it.daysOfWeek.contains(day) }
                    .map { DealItem(it) }
            if (!dayDeals.isEmpty()) {
                items.add(
                        DayItem(
                                when (day) {
                                    0 -> context.getString(R.string.monday)
                                    1 -> context.getString(R.string.tuesday)
                                    2 -> context.getString(R.string.wednesday)
                                    3 -> context.getString(R.string.thursday)
                                    4 -> context.getString(R.string.friday)
                                    5 -> context.getString(R.string.saturday)
                                    6 -> context.getString(R.string.sunday)
                                    else -> context.getString(R.string.monday)
                                }
                        )
                )
                items.addAll(dayDeals)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_day -> (holder as DayHolder).dayText.text = (items[position] as DayItem).day
            else -> {
                val deal = items[position] as DealItem
                val dealHolder = holder as DealHolder
                dealHolder.dealHours.text = deal.deal.hoursFormattedString(context)
                dealHolder.dealDescription.text = deal.deal.description
            }
        }
    }

    override fun getItemCount(): Int =
            items.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.item_day -> DayHolder(View.inflate(context, viewType, null))
            else -> DealHolder(View.inflate(context, viewType, null))
        }

    override fun getItemViewType(position: Int): Int =
        if (items[position] is DayItem) R.layout.item_day else R.layout.item_deal

    private class DealHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dealHours: TextView = view.findViewById(R.id.deal_item_hours) as TextView
        val dealDescription: TextView = view.findViewById(R.id.deal_item_description) as TextView
    }

    private class DayHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayText: TextView = view.findViewById(R.id.day_item_name) as TextView
    }

    private enum class Type { DAY, DEAL }

    abstract private class Item(val type: Type)

    private data class DayItem(val day: String) : Item(Type.DAY)

    private data class DealItem(val deal: Deal) : Item(Type.DEAL)
}