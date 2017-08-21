package com.tbd.app

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.jakewharton.rxbinding2.support.v7.widget.scrollStateChanges
import com.jakewharton.rxbinding2.view.detaches
import com.tbd.app.models.Bar
import com.wattpad.tap.util.rx.autoDispose
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * View that shows the Bars in a horizontal recyclerview
 * Created by orrie on 2017-07-04.
 */
class BarListView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs){
    private val recyclerView by lazy { findViewById(R.id.deal_list_recyclerview) as RecyclerView }
    private val barFocusChangesSubject = PublishSubject.create<String>()
    val barFocusChanges: Observable<String> = barFocusChangesSubject.hide()
    val adapter: BarAdapter
    val barClicks: Observable<Pair<Bar, ConstraintLayout>>
    val layoutManager: LinearLayoutManager

    init {
        View.inflate(context, R.layout.view_bar_list, this)
        adapter = BarAdapter(context, mutableListOf(), DealFilter())
        barClicks = adapter.barClicks
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
        recyclerView.scrollStateChanges()
                .filter { it == RecyclerView.SCROLL_STATE_IDLE }
                .map { layoutManager.findFirstCompletelyVisibleItemPosition() }
                .filter { it >= 0 }
                .map { adapter.getItem(it) }
                .subscribe {
                    barFocusChangesSubject.onNext(it.barMeta.id)
                }
                .autoDispose(detaches())

        adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {

            override fun onChanged() {
                getCurrentBar()?.let { barFocusChangesSubject.onNext(it.barMeta.id) }
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                getCurrentBar()?.let { barFocusChangesSubject.onNext(it.barMeta.id) }
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                getCurrentBar()?.let { barFocusChangesSubject.onNext(it.barMeta.id) }
            }
        })
    }

    fun addBar(bar: Bar) {
        adapter.addItem(bar)
    }

    fun removeBar(bar: Bar) {
        adapter.removeItem(bar)
    }

    fun scrollToBar(barId: String) {
        val position = adapter.getPosition(barId)
        if (position >= 0) {
            recyclerView.smoothScrollToPosition(position)
        }
    }

    fun filter(dealFilter: DealFilter) {
        adapter.filter(dealFilter)
    }

    fun getCurrentBar(): Bar? {
        val position = layoutManager.findFirstCompletelyVisibleItemPosition()
        return if (position >= 0) adapter.getItem(position) else null
    }
}