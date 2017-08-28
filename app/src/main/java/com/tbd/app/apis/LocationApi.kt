package com.tbd.app.apis

import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import io.reactivex.Single

/**
 * Created by orrie on 2017-08-24.
 */
class LocationApi(context: Context) {
    val fusedLocationProviderClient = FusedLocationProviderClient(context)

    fun getLastLocation(): Single<Location> {
        return Single.create { e ->
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                e.onSuccess(it)
            }
        }
    }
}