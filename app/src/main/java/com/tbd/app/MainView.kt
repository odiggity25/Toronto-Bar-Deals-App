package com.tbd.app

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.jakewharton.rxbinding2.view.detaches
import com.tbd.app.models.BarMeta
import com.tbd.app.utils.hideKeyboard
import com.tbd.app.utils.pxToDp
import com.tbd.app.utils.view.throttleClicks
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit




/**
 * Created by orrie on 2017-06-19.
 */
class MainView(context: Context,
               supportFragmentManager: FragmentManager,
               googleApiClient: GoogleApiClient) : LinearLayout(context), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private val mapFragment by lazy { supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment }
    val addBarClicks by lazy { findViewById(R.id.main_add_bar).throttleClicks() }
    private val addImage by lazy { findViewById(R.id.main_add_bar) as ImageView }
    private var addDealView: AddDealView? = null
    private val container by lazy { findViewById(R.id.main_container) as FrameLayout }
    val dealListView by lazy { findViewById(R.id.deal_list_view) as DealListView }

    private val mapReadiesSubject = PublishSubject.create<Unit>()
    val mapReadies: Observable<Unit> = mapReadiesSubject.hide()
    private val mapChangesSubject = PublishSubject.create<Projection>()
    val mapChanges: Observable<Projection> = mapChangesSubject.hide()
    private val addBarDialogShowsSubject = PublishSubject.create<Unit>()
    val addBarDialogShows: Observable<Unit> = addBarDialogShowsSubject.hide()
    private val addBarDialogClosesSubject = PublishSubject.create<Unit>()
    val addBarDialogCloses: Observable<Unit> = addBarDialogClosesSubject.hide()
    private val markerClicksSubject = PublishSubject.create<String>()
    val markerClicks: Observable<String> = markerClicksSubject.hide()

    var lastOpened: Marker? = null
    val markers = mutableListOf<Marker>()

    init {
        View.inflate(context, R.layout.view_main, this)
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        orientation = VERTICAL
        mapFragment.getMapAsync(this)
        addBarClicks.throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe { if (addImage.rotation == 0f) addBarDialogShowsSubject.onNext(Unit)
                else addBarDialogClosesSubject.onNext(Unit) }
        MainPresenter(this, detaches(), dealListView, googleApiClient)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        mapReadiesSubject.onNext(Unit)
        googleMap.setOnCameraIdleListener {
            googleMap.let { mapChangesSubject.onNext(it.projection) }
        }

        // Custom marker listener seems to be the only way to prevent the map from centering on marker click
        googleMap.setOnMarkerClickListener({ marker ->
            if (lastOpened != marker) {
                lastOpened?.hideInfoWindow()
            }
            marker.showInfoWindow()
            lastOpened = marker
            markerClicksSubject.onNext(marker.tag as String)
            true
        })
    }

    fun moveMap(latLng: LatLng, zoom: Float) {
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    fun showAddBarView() {
        val addDealView = AddDealView(context)
        addDealView.closes.subscribe { closeAddBarView() }
        this.addDealView = addDealView
        container.addView(addDealView)
        addDealView.x = width.toFloat()
        addDealView.y = -height.toFloat()
        addDealView.animate()
                .x(0f)
                .y(0f)
                .setDuration(200)
                .setListener(null)

        addImage.bringToFront()
        addImage.animate()
                .rotation(135f)
                .setDuration(200)
                .x(addImage.x - pxToDp(50))
                .y(addImage.y + pxToDp(50))
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        addImage.rotation = 45f
                    }
                })
    }

    fun closeAddBarView() {
        hideKeyboard(context as AppCompatActivity)
        addImage.rotation = 45f
        addImage.animate()
                .rotation(-180f)
                .x(addImage.x + pxToDp(50))
                .y(addImage.y - pxToDp(50))
                .setDuration(200)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        addImage.rotation = 0f
                    }
                })

        addDealView?.let {
            it.animate()
                    .x(width.toFloat())
                    .y(-height.toFloat())
                    .setDuration(200)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            container.removeView(addDealView)
                            addDealView = null
                        }
                    })
        }
    }

    fun addMarker(bar: BarMeta) {
        val marker = googleMap?.addMarker(MarkerOptions()
                .position(LatLng(bar.lat, bar.lng))
                .title(bar.name))
        marker?.let {
            it.tag = bar.id
            markers.add(it)
        }
    }

    fun removeMarker(bar: BarMeta) {
        markers.filter { it.tag == bar.id }
                .map { it.remove() }
    }

    fun highlightMarker(barId: String) {
        lastOpened?.hideInfoWindow()
        markers.filter { it.tag == barId }
                .map {
                    it.showInfoWindow()
                    lastOpened = it
                }
    }

}