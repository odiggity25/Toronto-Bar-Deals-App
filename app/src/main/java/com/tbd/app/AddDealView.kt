package com.tbd.app

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.detaches
import com.tbd.app.utils.hideKeyboard
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by orrie on 2017-06-20.
 */
class AddDealView(context: Context) : FrameLayout(context) {

    val allDayToggles: Observable<Boolean> by lazy { (findViewById(R.id.all_day) as CheckBox)
            .let { checkbox ->
                checkbox.clicks().map { checkbox.isChecked }
            }}

    val dayClicks: Observable<DayOfWeekPicker.DaySelected> by lazy { (findViewById(R.id.deal_day_of_week_picker) as DayOfWeekPicker).dayClicks }

    private val startTimeView by lazy { findViewById(R.id.start_time) as TextView }
    private val endTimeView by lazy { findViewById(R.id.end_time) as TextView }
    val startTimeClicks by lazy { startTimeView.clicks() }
    val endTimeClicks by lazy { endTimeView.clicks() }
    val submitButton: View by lazy { findViewById(R.id.submit_deal) }
    val submitClicks by lazy { submitButton.clicks() }
    val descriptionView by lazy { findViewById(R.id.deal_description) as EditText }
    private val closesSubject = PublishSubject.create<Unit>()
    val closes: Observable<Unit> = closesSubject.hide()
    private val placeSelectsSubject = PublishSubject.create<Place>()
    val placeSelects: Observable<Place> = placeSelectsSubject.hide()
    private val tagView by lazy { findViewById(R.id.add_deal_tags) as TagView }

    init {
        View.inflate(context, R.layout.view_add_deal, this)
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        isClickable = true
        val addDealPresenter = AddDealPresenter(this, detaches())

        startTimeClicks.subscribe {
            TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                addDealPresenter.startTimeSelected(hourOfDay, minute)
            }, 18, 0, false).show()
        }

        endTimeClicks.subscribe {
            TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                addDealPresenter.endTimeSelected(hourOfDay, minute)
            }, 21, 0, false).show()
        }

        val fragmentManager = (context as AppCompatActivity).fragmentManager
        val placesFragment = fragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as PlaceAutocompleteFragment
        placesFragment.setHint(context.getString(R.string.watering_hole))
        placesFragment.setBoundsBias(LatLngBounds(LatLng(43.541844, -79.620778), LatLng(43.906694, -79.255710)))
        val filter = AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT).build()
        placesFragment.setFilter(filter)
        placesFragment.setOnPlaceSelectedListener(object: PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place?) {
                placeSelectsSubject.onNext(p0)
            }

            override fun onError(p0: Status?) {
                Timber.e("Failed to retrieve place: ${p0.toString()}")
            }
        })

        descriptionView.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(context as Activity)
                return@OnEditorActionListener true
            }
            false
        })
        tagView.tagClicks.subscribe { hideKeyboard(context as Activity) }

        detaches().subscribe {
            fragmentManager.beginTransaction()
                    .remove(placesFragment).commit()
        }
    }

    fun toggleTimeSelectors(show: Boolean) {
        findViewById(R.id.time_container).visibility = if (show) View.VISIBLE else View.GONE
    }

    fun updateStartTime(date: Date) {
        startTimeView.text = SimpleDateFormat("hh:mm a").format(date)
        startTimeView.setTextColor(context.resources.getColor(R.color.dark_grey))
    }

    fun updateEndTime(date: Date) {
        endTimeView.text = SimpleDateFormat("hh:mm a").format(date)
        endTimeView.setTextColor(context.resources.getColor(R.color.dark_grey))
    }

    fun showSubmissionError(error: String) {
        Snackbar.make((context as AppCompatActivity).findViewById(android.R.id.content), error, Snackbar.LENGTH_SHORT).show()
    }

    fun showSubmissionSuccess() {
        Snackbar.make((context as AppCompatActivity).findViewById(android.R.id.content),
                "Deal successfully submitted for review. Thanks!", Snackbar.LENGTH_SHORT).show()
    }

    fun closeView() {
        closesSubject.onNext(Unit)
    }

    fun addTags(tags: List<String>) {
        tagView.addTags(tags)
    }

    fun getSelectedTags(): List<String> =
            tagView.selectedTags

}