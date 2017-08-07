package com.tbd.app.utils

import android.content.Context
import com.tbd.app.DealFilter
import com.tbd.app.R
import com.tbd.app.models.Deal
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by orrie on 2017-07-21.
 */
fun Deal.hoursFormattedString(context: Context): String {
    var dealText = ""
    if (this.allDay) {
        dealText = dealText.plus(context.getString(R.string.all_day))
    } else if (this.startTime != null && this.endTime != null){
        val startTime = this.startTime?.let { SimpleDateFormat("h:mm a").format(Date(it)) }
        val endTime = this.endTime?.let { SimpleDateFormat("h:mm a").format(Date(it)) }
        dealText = dealText.plus(startTime)
                .plus(" - ")
                .plus(endTime)
    } else if (this.startTime != null) {
        val startTime = this.startTime?.let { SimpleDateFormat("h:mm a").format(Date(it)) }
        dealText = dealText.plus(context.getString(R.string.before_end_time, startTime))
    } else if (this.endTime != null) {
        val endTime = this.endTime?.let { SimpleDateFormat("h:mm a").format(Date(it)) }
        dealText = dealText.plus(context.getString(R.string.before_end_time, endTime))
    }
    dealText = dealText.replace(":00", "")
    return dealText
}

fun Deal.matchesFilter(filter: DealFilter): Boolean {
    var dayMatches = false
    var tagMatches = false
    var timeMatches = true

    this.daysOfWeek.filter { filter.daysOfWeek.isEmpty() || filter.daysOfWeek.contains(it) }
            .map { dayMatches = true }
    this.tags.filter { filter.tags.isEmpty() || filter.tags.contains(it) }
            .map { tagMatches = true }
    if (filter.now && !this.allDay) {
        timeMatches = timeInRange(System.currentTimeMillis(), this.startTime, this.endTime)
    }
    return dayMatches && tagMatches && timeMatches
}

fun Deal.isEveryday(): Boolean =
        this.daysOfWeek.containsAll(listOf(0,1,2,3,4,5,6))