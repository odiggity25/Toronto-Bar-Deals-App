package com.tbd.app.utils

import android.content.Context
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places
import timber.log.Timber


object GoogleApiHelper : GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    var googleApiClient: GoogleApiClient? = null

    fun initialize(context: Context) {
        buildGoogleApiClient(context)
        connect()
    }

    fun connect() {
        googleApiClient?.connect()
    }

    fun disconnect() {
        googleApiClient?.let {
            if (it.isConnected) {
                it.disconnect()
            }
        }
    }

    val isConnected: Boolean
        get() = googleApiClient?.isConnected ?: false

    private fun buildGoogleApiClient(context: Context) {
        googleApiClient = GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .build()

    }

    override fun onConnected(bundle: Bundle?) {
    }

    override fun onConnectionSuspended(i: Int) {
        Timber.d("onConnectionSuspended: googleApiClient.connect()")
        googleApiClient?.connect()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Timber.d("onConnectionFailed: connectionResult.toString() = " + connectionResult.toString())
    }
}