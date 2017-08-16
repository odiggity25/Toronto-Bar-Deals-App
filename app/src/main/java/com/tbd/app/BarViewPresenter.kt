package com.tbd.app

import android.content.Context
import com.tbd.app.utils.events.EventManager

/**
 * Created by orrie on 2017-08-07.
 */
class BarViewPresenter(context: Context, view: BarView, eventManager: EventManager = EventManager()) {

    init {
        view.dealClicks.subscribe { view.showDealOptions(it) }
        view.reportDealClicks.subscribe { view.showReportDealDialog(it) }
        view.reportDealSubmits.subscribe { eventManager.sendSlackEvent(it.toString()) }
    }
}