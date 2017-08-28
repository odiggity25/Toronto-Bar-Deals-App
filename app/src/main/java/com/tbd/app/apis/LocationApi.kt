package com.tbd.app.apis

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import io.reactivex.Single

/**
 * Created by orrie on 2017-08-24.
 */
class LocationApi(private val context: Context) {
    val fusedLocationProviderClient = FusedLocationProviderClient(context)

    fun getLastLocation(): Single<Location> {
        return Single.create { e ->
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                e.onSuccess(it)
            }
        }
    }

    fun hasLocationPermission(): Boolean =
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
}