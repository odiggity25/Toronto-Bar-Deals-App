package com.tbd.app.apis

import android.graphics.Bitmap
import com.firebase.geofire.GeoLocation
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places
import com.tbd.app.models.Bar
import com.tbd.app.models.BarDeals
import com.tbd.app.models.Deal
import com.tbd.app.utils.firebase.RxFirebaseDb
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by orrie on 2017-06-22.
 */
class BarDealsApi(private val rxFirebaseDb: RxFirebaseDb = RxFirebaseDb(),
                  private val barDealsParser: BarDealsParser = BarDealsParser(),
                  private val geoFireApi: GeoFireApi = GeoFireApi(),
                  private val googleApiClient: GoogleApiClient? = null) {

    fun addBarDeal(bar: Bar, deal: Deal): Completable =
            rxFirebaseDb.valueExists("bars/${bar.id}")
                    .flatMapCompletable { exists ->
                        if (!exists) {
                            addBar(bar)
                        } else {
                            Completable.complete()
                        }
                    }
                    .andThen (
                        addDeal(bar, deal)
                    )

    fun addBar(bar: Bar): Completable =
            rxFirebaseDb.setValue("bars/${bar.id}",
                    mapOf(
                            "name" to bar.name,
                            "lat" to bar.lat,
                            "lon" to bar.lng
                    ))
                    .andThen (
                        geoFireApi.setBarLocation(bar.id, GeoLocation(bar.lat, bar.lng))
                    )

    fun addDeal(bar: Bar, deal: Deal): Completable {
        val key = rxFirebaseDb.getKey("deals/${bar.id}/")
        return rxFirebaseDb.setValue("deals/${bar.id}/$key", mapOf(
                                        "days_of_week" to deal.daysOfWeek.toList(),
                                        "tags" to deal.tags.toList(),
                                        "description" to deal.description,
                                        "all_day" to deal.allDay,
                                        "start_time" to deal.startTime,
                                        "end_time" to deal.endTime
                                ))
    }

    fun fetchBar(id: String): Single<Bar> =
            rxFirebaseDb.fetch("bars/$id", barDealsParser::parseBar)

    fun fetchDeals(barId: String): Single<MutableList<Deal>> =
            rxFirebaseDb.fetch("deals/$barId/", barDealsParser::parseDeals)

    fun fetchBarDeals(id: String): Single<BarDeals> =
            fetchBar(id)
                    .flatMap { bar ->
                        fetchDeals(bar.id)
                                .flatMap { Single.just(BarDeals(bar, it)) }

                    }

    fun watchBarsForLocation(geoLocation: GeoLocation, radius: Double): Observable<BarChange> =
                geoFireApi.watchBarIds(geoLocation, radius)
                        .flatMap { geoChange ->
                            fetchBarDeals(geoChange.barId)
                                    .flatMapObservable {
                                        Observable.just(BarChange(geoChange.action, it))
                                    }

                        }

    fun updateWatchLocation(geoLocation: GeoLocation, radius: Double) {
        geoFireApi.updateGeoQuery(geoLocation, radius)
    }

    data class BarChange(val action: GeoFireApi.GeoAction, val barDeals: BarDeals)

    fun imageForBar(barId: String): Bitmap? {
        if (googleApiClient == null) {
            return null
        }
        val result = Places.GeoDataApi.getPlacePhotos(googleApiClient, barId).await()
        if (result != null && result.status.isSuccess) {
            return result.photoMetadata[0].getPhoto(googleApiClient).await().bitmap
        }
        return null
    }

}