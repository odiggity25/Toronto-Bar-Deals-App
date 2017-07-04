package com.tbd.app

import android.app.FragmentManager
import com.google.android.gms.location.places.Place
import com.tbd.app.apis.BarDealsApi
import com.tbd.app.models.Bar
import com.tbd.app.models.Deal
import io.reactivex.Observable
import timber.log.Timber
import java.util.*

/**
 * Created by orrie on 2017-06-21.
 */
class AddDealPresenter(val addDealView: AddDealView,
                       val fragmentManager: FragmentManager,
                       detaches: Observable<Unit>,
                       barDealsApi: BarDealsApi = BarDealsApi()) {
    var place: Place? = null
    var startTime: Date? = null
    var endTime: Date? = null
    var allDay = true
    var daysAvailable = mutableSetOf<Int>()

    init {

        addDealView.allDayToggles.subscribe { checked ->
            allDay = checked
            addDealView.toggleTimeSelectors(!checked)
        }

        addDealView.dayClicks.subscribe { daySelected ->
            if (daySelected.selected) {
                daysAvailable.add(daySelected.day)
            } else {
                daysAvailable.remove(daySelected.day)
            }
        }

        addDealView.submitClicks.subscribe {
            val description = addDealView.descriptionView.text.toString()
            val finalPlace = place
            if (finalPlace == null) {
                addDealView.showSubmissionError("Choose the location before submitting")
                return@subscribe
            }
            if (!allDay && startTime == null) {
                addDealView.showSubmissionError("Choose the start time or choose all day before submitting")
                return@subscribe
            }
            if (description.isNullOrBlank()) {
                addDealView.showSubmissionError("Describe the deal before submitting")
                return@subscribe
            }

            val bar = Bar(finalPlace.id, finalPlace.name.toString(), finalPlace.latLng.latitude, finalPlace.latLng.longitude, null)
            val deal = Deal("", daysAvailable, mutableSetOf(), description, allDay, startTime?.time, endTime?.time)
            barDealsApi.addBarDeal(bar, deal).subscribe({
                Timber.i("added bar")
            }, { e -> Timber.e("failed to add bar: $e")})
            addDealView.showSubmissionSuccess()
            addDealView.closeView()
        }

        addDealView.placeSelects.subscribe { place = it }
    }

    fun startTimeSelected(hourOfDay: Int, minute: Int) {
        val date = parseDate(hourOfDay, minute)
        startTime = date
        addDealView.updateStartTime(date)

    }

    fun endTimeSelected(hourOfDay: Int, minute: Int) {
        val date = parseDate(hourOfDay, minute)
        endTime = date
        addDealView.updateEndTime(date)
    }

    fun parseDate(hourOfDay: Int, minute: Int) : Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
        cal.set(Calendar.MINUTE, minute)
        return cal.time
    }

}