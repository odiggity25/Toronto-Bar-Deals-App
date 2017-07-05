package com.tbd.app

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.jakewharton.rxbinding2.support.v7.widget.scrollStateChanges
import com.jakewharton.rxbinding2.view.detaches
import com.tbd.app.models.BarDeals
import com.wattpad.tap.util.rx.autoDispose
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * View that shows the BarDeals in a horizontal recyclerview
 * Created by orrie on 2017-07-04.
 */
class DealListView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs){
    private val recyclerView by lazy { findViewById(R.id.deal_list_recyclerview) as RecyclerView }
    private val barFocusChangesSubject = PublishSubject.create<String>()
    val barFocusChanges: Observable<String> = barFocusChangesSubject.hide()
    val adapter: DealListAdapter
    val barClicks: Observable<String>

    init {
        View.inflate(context, R.layout.view_deal_list, this)
        adapter = DealListAdapter(context, mutableListOf())
        barClicks = adapter.barClicks
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
        recyclerView.scrollStateChanges()
                .filter { it == RecyclerView.SCROLL_STATE_IDLE }
                .map { layoutManager.findFirstCompletelyVisibleItemPosition() }
                .filter { it >= 0 }
                .map { adapter.getItem(it) }
                .subscribe {
                    barFocusChangesSubject.onNext(it.bar.id)
                }
                .autoDispose(detaches())
    }

    fun addBar(barDeals: BarDeals) {
        adapter.addItem(barDeals)
    }

    fun removeBar(barDeals: BarDeals) {
        adapter.removeItem(barDeals)
    }
}