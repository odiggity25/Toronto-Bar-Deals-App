package com.tbd.app.apis

import com.firebase.geofire.GeoLocation
import com.tbd.app.models.Bar
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
                  private val geoFireApi: GeoFireApi = GeoFireApi()) {

    fun addBarDeal(bar: Bar, deal: Deal): Completable =
            rxFirebaseDb.valueExists("bars/${bar.id}")
                    .flatMapCompletable { exists ->
                        if (!exists) {
                            addDeal(bar, deal)
                        } else {
                            Completable.create { it.onComplete() }
                        }
                    }
                    .andThen (
                        addBar(bar)
                    )

    fun addBar(bar: Bar): Completable =
            rxFirebaseDb.setValue("bars/${bar.id}",
                    mapOf(
                            "name" to bar.name,
                            "lat" to bar.lat,
                            "lon" to bar.lng,
                            "image_url" to bar.imageUrl
                    ))
                    .andThen {
                        geoFireApi.setBarLocation(bar.id, GeoLocation(bar.lat, bar.lng))
                    }

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

    fun fetchBarsForLocation(geoLocation: GeoLocation, radius: Double): Single<Set<Bar>> =
                geoFireApi.fetchBarIds(geoLocation, radius)
                        .flatMap {
                            Observable.fromIterable(it)
                                    .flatMapSingle { fetchBar(it) }
                                    .toList()
                                    .map { it.toSet() }
                        }

}