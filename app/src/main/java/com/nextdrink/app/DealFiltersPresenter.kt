package com.nextdrink.app

import com.nextdrink.app.apis.BarApi
import com.wattpad.tap.util.rx.autoDispose
import io.reactivex.Observable

/**
 * Created by orrie on 2017-07-26.
 */
class DealFiltersPresenter(val dealFiltersView: DealFiltersView,
                           detaches: Observable<Unit>,
                           barApi: BarApi = BarApi()) {

    init {
        barApi.fetchAllDealTags().subscribe({ dealFiltersView.addTags(it) }, {})
                        .autoDispose(detaches)

        dealFiltersView.resetFiltersClicks.subscribe { dealFiltersView.resetFilters() }
    }
}