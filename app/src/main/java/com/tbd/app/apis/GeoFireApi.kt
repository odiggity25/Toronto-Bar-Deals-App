package com.tbd.app.apis

import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.tbd.app.utils.geofire.SimpleGeoQueryEventListener
import io.reactivex.Completable
import io.reactivex.Single


/**
 * Created by orrie on 2017-07-03.
 */
class GeoFireApi {
    private val geoFireRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("geofire/bars")
    private val geoFire: GeoFire = GeoFire(geoFireRef)

    fun setBarLocation(key: String, location: GeoLocation): Completable {
        return Completable.create { completable ->
            geoFire.setLocation(key, location) { _, error ->
                if (error == null) {
                    completable.onComplete()
                } else {
                    completable.onError(Throwable(error.message))
                }
            }
        }
    }

    fun fetchBarIds(geoLocation: GeoLocation, radius: Double): Single<Set<String>> {
        val geoQuery = geoFire.queryAtLocation(geoLocation, radius)
        val barIds = mutableSetOf<String>()
        return Single.create<Set<String>> {
                    geoQuery.addGeoQueryEventListener(object: SimpleGeoQueryEventListener{
                        override fun onGeoQueryReady() {
                            it.onSuccess(barIds)
                        }

                        override fun onKeyEntered(key: String, location: GeoLocation?) {
                            barIds.add(key)
                        }
                    })
                }
    }

}