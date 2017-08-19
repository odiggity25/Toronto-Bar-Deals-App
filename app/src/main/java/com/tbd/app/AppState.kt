package com.tbd.app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.tbd.app.utils.GoogleApiHelper
import com.wattpad.tap.util.Login
import timber.log.Timber


/**
 * Created by orrie on 2017-06-19.
 */
class AppState : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        if (FirebaseAuth.getInstance().currentUser == null) {
            Login().anonymouslyWithFirebase()
        }

        Timber.plant(Timber.DebugTree())
        Timber.d("Firebase token is ${FirebaseInstanceId.getInstance().token}")

        GoogleApiHelper.initialize(this)
    }
}