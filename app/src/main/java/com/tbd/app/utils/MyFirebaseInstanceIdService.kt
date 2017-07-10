package com.tbd.app.utils

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import timber.log.Timber

/**
 * Created by orrie on 2017-07-06.
 */
class MyFirebaseInstanceIdService : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Timber.d("Refreshed token: " + refreshedToken!!)
    }
}