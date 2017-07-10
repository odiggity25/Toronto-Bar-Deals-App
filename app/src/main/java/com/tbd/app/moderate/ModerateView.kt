package com.tbd.app.moderate

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.jakewharton.rxbinding2.view.detaches
import com.tbd.app.BarAdapter
import com.tbd.app.R
import com.tbd.app.models.Bar
import com.tbd.app.utils.dpToPx
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by orrie on 2017-07-07.
 */
class ModerateView(context: Context): FrameLayout(context){
    private val recyclerView by lazy {findViewById(R.id.moderate_bars_recyclerview) as RecyclerView}
    private val adapter = BarAdapter(context, mutableListOf(), dpToPx(160))
    val barClicks = adapter.barClicks
    private val dealApprovesSubject = PublishSubject.create<Bar>()
    val dealApproves: Observable<Bar> = dealApprovesSubject.hide()
    private val dealDeniesSubject = PublishSubject.create<Bar>()
    val dealDenies: Observable<Bar> = dealDeniesSubject.hide()

    init {
        View.inflate(context, R.layout.view_moderate, this)
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        ModeratePresenter(this, detaches())
    }

    fun addBar(bar: Bar) {
        adapter.addItem(bar)
    }

    fun removeBar(bar: Bar) {
        adapter.removeItem(bar)
    }

    fun showModerateOptions(bar: Bar) {
        AlertDialog.Builder(context)
                .setTitle("Moderate deal")
                .setPositiveButton("Approve", {_, _ -> dealApprovesSubject.onNext(bar)})
                .setNegativeButton("Deny", {_, _ -> dealDeniesSubject.onNext(bar)})
                .show()
    }

}