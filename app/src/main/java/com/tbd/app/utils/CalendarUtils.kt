package com.tbd.app.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by orrie on 2017-07-26.
 */
fun dayOfWeekAsInt(): Int =
        when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }

fun timeInRange(timeLong: Long, startTimeLong: Long?, endTimeLong: Long?): Boolean {
    val dateFormat = SimpleDateFormat("HH:mm")
    val time = dateFormat.format(Date(timeLong))
    var afterStartTime = false
    var beforeEndTime = false

    if (startTimeLong == null) {
        afterStartTime = true
    } else {
        val startTime = dateFormat.format(Date(startTimeLong))
        if (dateFormat.parse(time).after(dateFormat.parse(startTime))) {
            afterStartTime = true
        }
    }

    if (endTimeLong == null) {
        beforeEndTime = true
    } else {
        val endTime = dateFormat.format(Date(endTimeLong))
        if (dateFormat.parse(time).before(dateFormat.parse(endTime))) {
            beforeEndTime = true
        }
    }

    return afterStartTime && beforeEndTime
}