package com.wattpad.tap.util

import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber

class Login(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {

    fun anonymouslyWithFirebase() {
        Timber.i("Start sign in with Firebase")

        auth.signInAnonymously().addOnCompleteListener { task ->
            Timber.i("Finished signing in? ${task.isSuccessful}")
        }
    }
}
