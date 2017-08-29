package com.nextdrink.app

import android.Manifest
import android.location.Location
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.LatLng
import com.nextdrink.app.apis.BarApi
import com.nextdrink.app.apis.GeoFireApi
import com.nextdrink.app.apis.LocationApi
import com.nextdrink.app.utils.dayOfWeekAsInt
import com.wattpad.tap.util.rx.autoDispose
import io.reactivex.Observable
import timber.log.Timber


/**
 * Created by orrie on 2017-06-19.
 */
class MainPresenter(private val mainView: MainView,
                    private val cancelSignal: Observable<Unit>,
                    private val barListView: BarListView,
                    private val barApi: BarApi = BarApi(),
                    private val locationApi: LocationApi = LocationApi(mainView.context))  {

    var watching = false
    var dealFilter = DealFilter()
    val context = mainView.context as AppCompatActivity
    val defaultZoom = 14f

    companion object {
        val REQUEST_GET_FINE_LOCATION_PERMISSION = 101
    }

    init {
        val day = dayOfWeekAsInt()
        dealFilter.daysOfWeek = mutableListOf(day)
        dealFilter.now = true
        mainView.bind(dealFilter)
        mainView.setInitialDayOfWeekToNow()

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

        if (!locationApi.hasLocationPermission()) {
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_GET_FINE_LOCATION_PERMISSION)
        }
    }

    private fun mapReady() {
        setMapLocation()
    }

    fun setMapLocation() {
        val torontoLocation = LatLng(43.6532, -79.3832)
        if (!locationApi.hasLocationPermission()) {
            mainView.moveMap(torontoLocation, defaultZoom)
            return
        }
        locationApi.getLastLocation().subscribe({
            val position = LatLng(it.latitude, it.longitude)
            mainView.moveMap(position, defaultZoom)
            mainView.setPositionMarker(position)
        }, {
            mainView.moveMap(torontoLocation, defaultZoom)
        })
    }

    private fun mapChanged(projection: Projection) {
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
                    .retry()
                    .subscribe({
                        when (it.action) {
                            GeoFireApi.GeoAction.ENTERED -> {
                                it.bar?.let { bar ->
                                    mainView.addBar(bar)
                                    barListView.addBar(bar)
                                }
                            }
                            GeoFireApi.GeoAction.EXITED -> {
                                it.bar?.let { bar ->
                                    mainView.removeBar(bar)
                                    barListView.removeBar(bar)
                                }
                            }
                            GeoFireApi.GeoAction.FINISHED -> {
                                barListView.updateEmptyState()
                            }
                        }
                    }, {
                        Timber.e("Failed to retrieve bars $it")
                    }, {
                        Timber.i("On complete barMeta watching")
                    }).autoDispose(cancelSignal)
            watching = true
        } else {
            barApi.updateWatchLocation(geoLocation, radius)
        }
    }
}