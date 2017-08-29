package com.nextdrink.app.moderate

import com.nextdrink.app.apis.BarApi
import com.wattpad.tap.util.rx.autoDispose
import io.reactivex.Observable
import timber.log.Timber

/**
 * This Activity will not be seen by the users. It shows a list of all the deals added by unknown users and allows
 * an admin to approve or deny the deals.
 * Created by orrie on 2017-07-07.
 */
class ModeratePresenter(moderateView: ModerateView,
                        exits: Observable<Unit>,
                        barApi: BarApi = BarApi()) {

    init {
        barApi.fetchUnmoderatedDeals()
                .subscribe (
                        { moderateView.addBar(it) },
                        { Timber.e("Failed to fetch unmoderated deals: ${it.message}")})
                .autoDispose(exits)

        moderateView.barClicks.subscribe { moderateView.showModerateOptions(it.first) }
        moderateView.dealApproves.subscribe {
            barApi.approveBarDeal(it.barMeta, it.deals[0]).subscribe({ moderateView.removeBar(it) },{})
        }
        moderateView.dealDenies.subscribe {
            barApi.removeDeal(it.barMeta, it.deals[0], false).subscribe({ moderateView.removeBar(it) },{})
        }
    }

}