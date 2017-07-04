package com.tbd.app

import android.location.Location
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.LatLng
import com.tbd.app.apis.BarDealsApi
import com.wattpad.tap.util.rx.autoDispose
import io.reactivex.Observable
import timber.log.Timber


/**
 * Created by orrie on 2017-06-19.
 */
class MainPresenter(val mainView: MainView,
                    val cancelSignal: Observable<Unit>,
                    val barDealsApi: BarDealsApi = BarDealsApi())  {

    init {
        mainView.mapReadies.subscribe { mapReady() }
        mainView.mapChanges.subscribe { mapChanged(it) }
        mainView.addBarDialogShows.subscribe { mainView.showAddBarView() }
        mainView.addBarDialogCloses.subscribe { mainView.closeAddBarView() }
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
        val radius = radiusResults[0]
        barDealsApi.fetchBarsForLocation(GeoLocation(latLngBounds.center.latitude, latLngBounds.center.longitude),
                radius.toDouble())
                .subscribe({
                    it.forEach { mainView.addMarker(it) }
                }, {
                    Timber.e("Failed to retrieve bars ${it.message}")
                }).autoDispose(cancelSignal)
    }
}