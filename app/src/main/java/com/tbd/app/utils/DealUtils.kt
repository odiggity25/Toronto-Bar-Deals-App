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
    } else {
        val startTime = this.startTime?.let { SimpleDateFormat("h:mm a").format(Date(it)) }
        val endTime = this.endTime?.let { SimpleDateFormat("h:mm a").format(Date(it)) }
        dealText = dealText.plus(startTime?.replace(":00", ""))
                .plus(" - ")
                .plus(endTime?.replace(":00", ""))
    }
    return dealText
}

fun Deal.matchesFilter(filter: DealFilter): Boolean {
    var dayMatches = false
    var tagMatches = false

    this.daysOfWeek.filter { filter.daysOfWeek.isEmpty() || filter.daysOfWeek.contains(it) }
            .map { dayMatches = true }
    this.tags.filter { filter.tags.isEmpty() || filter.tags.contains(it) }
            .map { tagMatches = true }
    return dayMatches && tagMatches
}