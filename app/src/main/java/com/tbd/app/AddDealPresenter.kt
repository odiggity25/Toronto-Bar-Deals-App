package com.tbd.app

import android.app.FragmentManager
import com.google.android.gms.location.places.Place
import com.tbd.app.apis.BarApi
import com.tbd.app.models.BarMeta
import com.tbd.app.models.Deal
import com.tbd.app.utils.events.EventManager
import com.wattpad.tap.util.rx.autoDispose
import io.reactivex.Observable
import timber.log.Timber
import java.util.*

/**
 * Created by orrie on 2017-06-21.
 */
class AddDealPresenter(val addDealView: AddDealView,
                       val fragmentManager: FragmentManager,
                       detaches: Observable<Unit>,
                       barApi: BarApi = BarApi()) {
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
            val tags = addDealView.getSelectedTags()
            if (finalPlace == null) {
                addDealView.showSubmissionError("Choose the location before submitting")
                return@subscribe
            }
            if (!allDay && startTime == null && endTime == null) {
                addDealView.showSubmissionError("Choose the start time or end time or choose all day before submitting")
                return@subscribe
            }
            if (description.isNullOrBlank()) {
                addDealView.showSubmissionError("Describe the deal before submitting")
                return@subscribe
            }
            if (tags.isEmpty()) {
                addDealView.showSubmissionError("Select at least one tag before submitting")
                return@subscribe
            }

            val bar = BarMeta(finalPlace.id, finalPlace.name.toString(), finalPlace.latLng.latitude, finalPlace.latLng.longitude, null)
            if (daysAvailable.isEmpty()) {
                daysAvailable.addAll(listOf(0,1,2,3,4,5,6))
            }
            val deal = Deal("", daysAvailable, tags.toMutableSet(), description, allDay, startTime?.time, endTime?.time, bar.id)
            barApi.addUnmoderatedBarDeal(bar, deal).subscribe({
                Timber.i("added barMeta")
                EventManager().sendSlackEvent("New unmoderated deal ${deal.description}")
            }, { e -> Timber.e("failed to add barMeta: $e")})
            addDealView.showSubmissionSuccess()
            addDealView.closeView()
        }

        addDealView.placeSelects.subscribe { place = it }

        barApi.fetchAllDealTags().subscribe({ addDealView.addTags(it) }, {})
                .autoDispose(detaches)
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