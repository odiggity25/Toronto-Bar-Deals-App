package com.nextdrink.app.models

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.location.places.Place
import paperparcel.PaperParcel

/**
 * Created by orrie on 2017-06-21.
 */
@PaperParcel
class BarMeta(val id: String,
              val name: String,
              val lat: Double,
              val lng: Double,
              val address: String,
              val priceLevel: Int?,
              val rating: Double?,
              val website: String?,
              var image: Bitmap? = null) : Parcelable {

    constructor(place: Place) : this(
            place.id,
            place.name.toString(),
            place.latLng.latitude,
            place.latLng.longitude,
            place.address.toString(),
            place.priceLevel,
            place.rating.toDouble(),
            place.websiteUri?.toString(),
            null
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelBarMeta.writeToParcel(this, dest, flags)
    }

    override fun describeContents(): Int = 0

    companion object {
        @JvmField val CREATOR = PaperParcelBarMeta.CREATOR
    }
}