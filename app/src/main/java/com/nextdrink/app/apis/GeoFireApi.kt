package com.nextdrink.app.apis

import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.firebase.geofire.GeoQueryEventListener
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Completable
import io.reactivex.Observable


/**
 * Created by orrie on 2017-07-03.
 */
class GeoFireApi {
    private val geoFireRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("geofire/bars")
    private val geoFire: GeoFire = GeoFire(geoFireRef)
    var geoQuery: GeoQuery? = null

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

    fun watchBarIds(geoLocation: GeoLocation, radius: Double): Observable<GeoChange> {
        geoQuery = geoFire.queryAtLocation(geoLocation, radius/1000)
        return Observable.create<GeoChange> {
                    geoQuery?.addGeoQueryEventListener(object: GeoQueryEventListener {

                        override fun onKeyEntered(key: String, location: GeoLocation?) {
                            it.onNext(GeoChange(GeoAction.ENTERED, key))
                        }

                        override fun onKeyExited(key: String) {
                            it.onNext(GeoChange(GeoAction.EXITED, key))
                        }

                        override fun onKeyMoved(key: String?, location: GeoLocation?) {
                            // No-op, this shouldn't happen
                        }

                        override fun onGeoQueryError(error: DatabaseError) {
                            it.onError(Exception(error.message))
                        }

                        override fun onGeoQueryReady() {
                            it.onNext(GeoChange(GeoAction.FINISHED, ""))
                        }

                    })
                }
    }

    fun updateGeoQuery(geoLocation: GeoLocation, radius: Double) {
        geoQuery?.setLocation(geoLocation, radius/1000)
    }
    enum class GeoAction { ENTERED, EXITED, FINISHED }
    data class GeoChange(val action: GeoAction, val barId: String)

}