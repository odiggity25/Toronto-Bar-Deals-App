package com.tbd.app.utils.firebase

import com.google.firebase.database.DataSnapshot

/**
 * Created by orrie on 2017-06-22.
 */
fun DataSnapshot.getStringValue(key: String): String? =
        this.child(key).value as? String

fun DataSnapshot.getLongValue(key: String): Long? =
        this.child(key).value as? Long

fun DataSnapshot.getBooleanValue(key: String): Boolean =
        this.child(key).value as? Boolean ?: false

fun DataSnapshot.getDoubleValue(key: String): Double? =
        this.child(key).value as? Double

fun DataSnapshot.getIntValue(key: String): Int? =
        getLongValue(key)?.toInt()