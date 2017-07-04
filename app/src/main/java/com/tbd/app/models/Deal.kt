package com.tbd.app.models

import android.os.Parcel
import android.os.Parcelable
import paperparcel.PaperParcel

/**
 * Created by orrie on 2017-06-21.
 */
@PaperParcel
class Deal(val id: String,
           val daysOfWeek: MutableSet<Int>, //Mon = 0, Sun = 7
           val tags: MutableSet<String>,
           val description: String,
           val allDay: Boolean,
           val startTime: Long?,
           val endTime: Long?) : Parcelable {

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelDeal.writeToParcel(this, dest, flags)
    }

    override fun describeContents(): Int = 0

    companion object {
        @JvmField val CREATOR = PaperParcelDeal.CREATOR
    }

}