package com.tbd.app.models

import android.os.Parcel
import android.os.Parcelable
import paperparcel.PaperParcel

/**
 * Created by orrie on 2017-06-21.
 */
@PaperParcel
class BarDeals(val bar: Bar,
               val deals: List<Deal>) : Parcelable {

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelBarDeals.writeToParcel(this, dest, flags)
    }

    override fun describeContents(): Int = 0

    companion object {
        @JvmField val CREATOR = PaperParcelBarDeals.CREATOR
    }
}