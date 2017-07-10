package com.tbd.app.apis

import com.google.firebase.database.DataSnapshot
import com.tbd.app.models.BarMeta
import com.tbd.app.models.Deal
import com.tbd.app.utils.firebase.getBooleanValue
import com.tbd.app.utils.firebase.getDoubleValue
import com.tbd.app.utils.firebase.getLongValue
import com.tbd.app.utils.firebase.getStringValue

/**
 * Created by orrie on 2017-06-22.
 */
class BarParser {

    fun parseBarMeta(dataSnapshot: DataSnapshot): BarMeta? {
        val id = dataSnapshot.key
        val name = dataSnapshot.getStringValue("name") ?: return null
        val lat = dataSnapshot.getDoubleValue("lat") ?: return null
        val lon = dataSnapshot.getDoubleValue("lon") ?: return null
        return BarMeta(id, name, lat, lon)
    }

    fun parseDeal(barId: String, dataSnapshot: DataSnapshot): Deal? {
        val id = dataSnapshot.key
        val daysOfWeek = parseDaysOfWeek(dataSnapshot.child("days_of_week"))
        val tags = parseDealTags(dataSnapshot.child("tags"))
        val description = dataSnapshot.getStringValue("description") ?: return null
        val allDay = dataSnapshot.getBooleanValue("all_day")
        val startTime = dataSnapshot.getLongValue("start_time")
        val endTime = dataSnapshot.getLongValue("end_time")
        return Deal(id, daysOfWeek, tags, description, allDay, startTime, endTime, barId)
    }

    fun parseDeals(barId: String, dataSnapshot: DataSnapshot): MutableList<Deal> {
        val deals = mutableListOf<Deal>()
        dataSnapshot.children
                .forEach {
                    val deal = parseDeal(barId, it)
                    deal?.let { deals.add(it) }
                }
        return deals
    }

    fun parseUnmoderatedDeals(dataSnapshot: DataSnapshot): MutableList<Deal> {
            val deals = mutableListOf<Deal>()
            dataSnapshot.children
                    .forEach {
                        val barId = it.key
                        it.children.forEach {
                            val deal = parseDeal(barId, it)
                            deal?.let { deals.add(it) }
                        }

                    }
            return deals
        }

    fun parseDealTags(dataSnapshot: DataSnapshot): MutableSet<String> {
        val tags = mutableSetOf<String>()
        dataSnapshot.children.forEach { tags.add(it.value as String) }
        return tags
    }

    fun parseDaysOfWeek(dataSnapshot: DataSnapshot): MutableSet<Int> {
        val days = mutableSetOf<Int>()
        dataSnapshot.children.forEach { days.add((it.value as Long).toInt()) }
        return days
    }

}