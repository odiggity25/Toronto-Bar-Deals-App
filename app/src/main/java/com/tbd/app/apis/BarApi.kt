package com.tbd.app.apis

import android.graphics.Bitmap
import com.firebase.geofire.GeoLocation
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places
import com.tbd.app.models.Bar
import com.tbd.app.models.BarMeta
import com.tbd.app.models.Deal
import com.tbd.app.utils.firebase.RxFirebaseDb
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by orrie on 2017-06-22.
 */
class BarApi(private val rxFirebaseDb: RxFirebaseDb = RxFirebaseDb(),
             private val barParser: BarParser = BarParser(),
             private val geoFireApi: GeoFireApi = GeoFireApi(),
             private val googleApiClient: GoogleApiClient? = null) {

    fun addUnmoderatedBarDeal(barMeta: BarMeta, deal: Deal): Completable =
            rxFirebaseDb.valueExists("bars/${barMeta.id}")
                    .flatMapCompletable { exists ->
                        if (!exists) {
                            addBar(barMeta)
                        } else {
                            Completable.complete()
                        }
                    }
                    .andThen (
                        addDeal(barMeta, deal, false)
                    )

    /**
     * Note: The geo cordinates won't be saved until the deal is verified, so that it won't show up
     * on the map
     */
    fun addBar(barMeta: BarMeta): Completable =
            rxFirebaseDb.setValue("bars/${barMeta.id}",
                    mapOf(
                            "name" to barMeta.name,
                            "lat" to barMeta.lat,
                            "lon" to barMeta.lng
                    ))

    fun approveBarDeal(barMeta: BarMeta, deal: Deal): Completable {
        return removeDeal(barMeta, deal, false)
                .andThen ( addDeal(barMeta, deal, true))
                .andThen ( geoFireApi.setBarLocation(barMeta.id, GeoLocation(barMeta.lat, barMeta.lng)) )
    }

    fun addDeal(bar: BarMeta, deal: Deal, moderated: Boolean): Completable {
        val root = if (moderated) "deals" else "unmoderated_deals"
        val key = rxFirebaseDb.getKey("$root/${bar.id}/")
        return rxFirebaseDb.setValue("$root/${bar.id}/$key", mapOf(
                                        "days_of_week" to deal.daysOfWeek.toList(),
                                        "tags" to deal.tags.toList(),
                                        "description" to deal.description,
                                        "all_day" to deal.allDay,
                                        "start_time" to deal.startTime,
                                        "end_time" to deal.endTime
                                ))
    }

    fun removeDeal(bar: BarMeta, deal: Deal, moderated: Boolean): Completable {
        val root = if (moderated) "unmoderated_deals" else "deals"
        return rxFirebaseDb.removeValue("$root/${bar.id}/${deal.id}")
    }

    fun fetchBarMeta(id: String): Single<BarMeta> =
            rxFirebaseDb.fetch("bars/$id", barParser::parseBarMeta)

    fun fetchDeals(barId: String): Single<MutableList<Deal>> =
            rxFirebaseDb.fetch("deals/$barId/", {barParser.parseDeals(barId, it)})

    fun fetchBar(id: String): Single<Bar> =
            fetchBarMeta(id)
                    .flatMap { bar ->
                        fetchDeals(bar.id)
                                .flatMap { Single.just(Bar(bar, it)) }

                    }

    fun watchBarsForLocation(geoLocation: GeoLocation, radius: Double): Observable<BarChange> =
                geoFireApi.watchBarIds(geoLocation, radius)
                        .flatMap { geoChange ->
                            fetchBar(geoChange.barId)
                                    .flatMapObservable {
                                        Observable.just(BarChange(geoChange.action, it))
                                    }

                        }

    fun updateWatchLocation(geoLocation: GeoLocation, radius: Double) {
        geoFireApi.updateGeoQuery(geoLocation, radius)
    }

    data class BarChange(val action: GeoFireApi.GeoAction, val bar: Bar)

    fun imageForBar(barId: String): Bitmap? {
        var bitmap: Bitmap? = null
        if (googleApiClient == null) {
            return null
        }
        val result = Places.GeoDataApi.getPlacePhotos(googleApiClient, barId).await()
        if (result?.status.isSuccess) {
            bitmap = result.photoMetadata[0].getPhoto(googleApiClient).await().bitmap
        }
        result?.photoMetadata?.release()
        return bitmap
    }

    fun fetchUnmoderatedDeals(): Observable<Bar> {
        return rxFirebaseDb.fetch("unmoderated_deals", barParser::parseUnmoderatedDeals)
                .flatMapObservable { Observable.fromIterable(it) }
                .flatMap { deal ->
                    fetchBarMeta(deal.barId)
                            .flatMapObservable { Observable.just(Bar(it, listOf(deal))) }
                }
    }

}