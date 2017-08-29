package com.nextdrink.app

import android.content.Context
import com.nextdrink.app.models.Bar
import com.nextdrink.app.utils.events.EventManager
import com.nextdrink.app.utils.googleMapUrl
import com.nextdrink.app.utils.openExternalUrl

/**
 * Created by orrie on 2017-08-07.
 */
class BarViewPresenter(context: Context, bar: Bar, view: BarView, eventManager: EventManager = EventManager()) {

    init {
        view.dealClicks.subscribe { view.showDealOptions(it) }
        view.reportDealClicks.subscribe { view.showReportDealDialog(it) }
        view.reportDealSubmits.subscribe { eventManager.sendSlackEvent(it.toString()) }
        view.websiteClicks.subscribe { bar.barMeta.website?.let { openExternalUrl(context, it) } }
        view.showOnMapClicks.subscribe { openExternalUrl(context, bar.googleMapUrl()) }
    }
}