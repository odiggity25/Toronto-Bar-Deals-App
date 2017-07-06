package com.tbd.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places
import timber.log.Timber


class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val googleApiClient = GoogleApiClient.Builder(this)
                        .addApi(Places.GEO_DATA_API)
                        .enableAutoManage(this, this)
                        .build()
        setContentView(MainView(this, supportFragmentManager, googleApiClient))
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Timber.e("Failed to connect to google api: ${p0.errorMessage}")
    }
}
