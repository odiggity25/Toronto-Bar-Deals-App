package com.tbd.app

import android.location.Location
import com.firebase.geofire.GeoLocation
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.LatLng
import com.tbd.app.apis.BarDealsApi
import com.tbd.app.apis.GeoFireApi
import com.tbd.app.models.BarDeals
import com.wattpad.tap.util.rx.autoDispose
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


/**
 * Created by orrie on 2017-06-19.
 */
class MainPresenter(private val mainView: MainView,
                    private val cancelSignal: Observable<Unit>,
                    private val dealListView: DealListView,
                    private val googleApiClient: GoogleApiClient,
                    private val barDealsApi: BarDealsApi = BarDealsApi(googleApiClient = googleApiClient))  {

    var watching = false

    init {
        mainView.mapReadies.subscribe { mapReady() }
        mainView.mapChanges.subscribe { mapChanged(it) }
        mainView.addBarDialogShows.subscribe { mainView.showAddBarView() }
        mainView.addBarDialogCloses.subscribe { mainView.closeAddBarView() }
        mainView.markerClicks.subscribe { dealListView.scrollToBar(it) }

        dealListView.barFocusChanges.subscribe { mainView.highlightMarker(it) }
        dealListView.barClicks.subscribe { mainView.highlightMarker(it) }
    }

    fun mapReady() {
        val toronto = LatLng(43.6532, -79.3832)
        mainView.moveMap(toronto, 12f)
    }

    fun mapChanged(projection: Projection) {
        val latLngBounds = projection.visibleRegion.latLngBounds
        val radiusResults = FloatArray(3)
        Location.distanceBetween(latLngBounds.southwest.latitude, latLngBounds.southwest.longitude,
                latLngBounds.northeast.latitude, latLngBounds.northeast.longitude, radiusResults)
        val radius = radiusResults[0].toDouble() / 2
        val geoLocation = GeoLocation(latLngBounds.center.latitude, latLngBounds.center.longitude)

        if (!watching) {
            barDealsApi.watchBarsForLocation(geoLocation, radius)
                    .subscribe({
                        when (it.action) {
                            GeoFireApi.GeoAction.ENTERED -> {
                                mainView.addMarker(it.barDeals.bar)
                                addBarToList(it.barDeals)
                            }
                            GeoFireApi.GeoAction.EXITED -> {
                                mainView.removeMarker(it.barDeals.bar)
                                dealListView.removeBar(it.barDeals)
                            }
                        }
                    }, {
                        Timber.e("Failed to retrieve bars ${it.message}")
                    }, {
                        Timber.i("On complete bar watching")
                    }).autoDispose(cancelSignal)
            watching = true
        } else {
            barDealsApi.updateWatchLocation(geoLocation, radius)
        }
    }

    fun addBarToList(barDeals: BarDeals) {
        Observable.fromCallable {  barDealsApi.imageForBar(barDeals.bar.id) }
                .map {
                    barDeals.bar.image = it
                    Single.just(barDeals)
                }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate { dealListView.addBar(barDeals) }
                .subscribe ()
    }
}