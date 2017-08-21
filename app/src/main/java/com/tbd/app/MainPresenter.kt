package com.tbd.app

import android.location.Location
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.LatLng
import com.tbd.app.apis.BarApi
import com.tbd.app.apis.GeoFireApi
import com.tbd.app.utils.dayOfWeekAsInt
import com.wattpad.tap.util.rx.autoDispose
import io.reactivex.Observable
import timber.log.Timber


/**
 * Created by orrie on 2017-06-19.
 */
class MainPresenter(private val mainView: MainView,
                    private val cancelSignal: Observable<Unit>,
                    private val barListView: BarListView,
                    private val barApi: BarApi = BarApi())  {

    var watching = false
    var dealFilter = DealFilter()

    init {
        mainView.mapReadies.subscribe { mapReady() }
        mainView.mapChanges.subscribe { mapChanged(it) }
        mainView.addBarDialogShows.subscribe { mainView.showAddBarView() }
        mainView.addBarDialogCloses.subscribe { mainView.closeAddBarView() }
        mainView.markerClicks.subscribe { barListView.scrollToBar(it) }
        mainView.moderateClicks.subscribe { mainView.showModerateActivity() }
        mainView.dayClicks
                .filter { it.selected }
                .subscribe { (day, _, now) ->
                    dealFilter.daysOfWeek = mutableListOf(day)
                    dealFilter.now = now
                    mainView.updateFilter(dealFilter)
                }
        mainView.filterClicks.subscribe { mainView.showDealFiltersView(dealFilter) }
        mainView.filterCloses.subscribe {
            dealFilter = it
            mainView.updateFilter(dealFilter)
            mainView.hideDealFiltersView()
        }

        barListView.barFocusChanges.subscribe { mainView.showMarkerInfoWindow(it) }
        barListView.barClicks.subscribe {
            mainView.showMarkerInfoWindow(it.first.barMeta.id)
            mainView.showBarView(it)
        }

        val day = dayOfWeekAsInt()
        dealFilter.daysOfWeek = mutableListOf(day)
        dealFilter.now = true
        mainView.updateFilter(dealFilter)
        mainView.setInitialDayOfWeekToNow()
    }

    fun mapReady() {
        val toronto = LatLng(43.6532, -79.3832)
        mainView.moveMap(toronto, 12f)
    }

    fun mapChanged(projection: Projection) {
        val latLngBounds = projection.visibleRegion.latLngBounds
        val radiusResults = FloatArray(3)
        // Note we use the mid latitude since the width of the screen is smaller than the height so if
        // we used bottom left and top right we'd get a radius that is actually larger than what is on screen
        val midLatitude = (latLngBounds.southwest.latitude + latLngBounds.northeast.latitude)/2
        Location.distanceBetween(midLatitude, latLngBounds.southwest.longitude,
                midLatitude, latLngBounds.northeast.longitude, radiusResults)
        val radius = radiusResults[0].toDouble() / 2
        val geoLocation = GeoLocation(latLngBounds.center.latitude, latLngBounds.center.longitude)

        if (!watching) {
            barApi.watchBarsForLocation(geoLocation, radius)
                    .subscribe({
                        when (it.action) {
                            GeoFireApi.GeoAction.ENTERED -> {
                                mainView.addBar(it.bar)
                                barListView.addBar(it.bar)
                            }
                            GeoFireApi.GeoAction.EXITED -> {
                                mainView.removeMarker(it.bar)
                                barListView.removeBar(it.bar)
                            }
                        }
                    }, {
                        Timber.e("Failed to retrieve bars ${it.message}")
                    }, {
                        Timber.i("On complete barMeta watching")
                    }).autoDispose(cancelSignal)
            watching = true
        } else {
            barApi.updateWatchLocation(geoLocation, radius)
        }
    }
}