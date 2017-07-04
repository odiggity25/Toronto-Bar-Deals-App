package com.tbd.app.utils.geofire

import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.firebase.database.DatabaseError

/**
 * Created by orrie on 2017-07-03.
 */
interface SimpleGeoQueryEventListener : GeoQueryEventListener {
    override fun onGeoQueryReady()

    override fun onKeyEntered(key: String, location: GeoLocation?)

    override fun onKeyMoved(key: String?, location: GeoLocation?) {
    }

    override fun onKeyExited(key: String?) {
    }

    override fun onGeoQueryError(error: DatabaseError?) {
    }
}