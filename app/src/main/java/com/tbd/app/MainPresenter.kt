package com.tbd.app

import android.location.Location
import com.firebase.geofire.GeoLocation
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.LatLng
import com.tbd.app.apis.BarApi
import com.tbd.app.apis.GeoFireApi
import com.tbd.app.models.Bar
import com.wattpad.tap.util.rx.autoDispose
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*


/**
 * Created by orrie on 2017-06-19.
 */
class MainPresenter(private val mainView: MainView,
                    private val cancelSignal: Observable<Unit>,
                    private val barListView: BarListView,
                    private val googleApiClient: GoogleApiClient,
                    private val barApi: BarApi = BarApi(googleApiClient = googleApiClient))  {

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
                .subscribe { (day) ->
                    dealFilter.daysOfWeek = mutableListOf(day)
                    mainView.updateFilter(dealFilter)
                }

        barListView.barFocusChanges.subscribe { mainView.highlightMarker(it) }
        barListView.barClicks.subscribe {
            mainView.highlightMarker(it.first.barMeta.id)
            mainView.showBarView(it)
        }

        val day = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }
        dealFilter.daysOfWeek = mutableListOf(day)
        mainView.updateFilter(dealFilter)
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
            barApi.watchBarsForLocation(geoLocation, radius)
                    .subscribe({
                        when (it.action) {
                            GeoFireApi.GeoAction.ENTERED -> {
                                mainView.addBar(it.bar)
                                addBarToList(it.bar)
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

    fun addBarToList(bar: Bar) {
        Observable.fromCallable {  barApi.imageForBar(bar.barMeta.id) }
                .map {
                    bar.barMeta.image = it
                    Single.just(bar)
                }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate { barListView.addBar(bar) }
                .subscribe ()
    }
}