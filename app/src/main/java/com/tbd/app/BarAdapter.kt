package com.tbd.app

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.tbd.app.models.Bar
import com.tbd.app.utils.dpToPx
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by orrie on 2017-07-04.
 */
class BarAdapter(private val context: Context,
                 private val bars: MutableList<Bar>,
                 private var itemHeight: Int = -1) : RecyclerView.Adapter<BarAdapter.BarHolder>() {

    private val barClicksSubject = PublishSubject.create<Bar>()
    val barClicks: Observable<Bar> = barClicksSubject.hide()
    private var barsFiltered = mutableListOf<Bar>()
    var selectedDayOfWeek = -1

    override fun onBindViewHolder(holder: BarHolder?, position: Int) {
        holder?.view?.bind(barsFiltered[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarHolder {
        if (itemHeight < 0) {
            itemHeight = parent.height
        }
        // -36 dp so the next item peeks in
        val barHolder = BarHolder(BarView(context, parent.measuredWidth - dpToPx(36), itemHeight))
        barHolder.view.clicks().subscribe {
            val bar = barsFiltered[barHolder.adapterPosition]
            barClicksSubject.onNext(bar)
        }
        return barHolder
    }

    override fun getItemCount(): Int {
        return barsFiltered.size
    }

    fun getItem(position: Int): Bar =
            barsFiltered[position]

    fun getPosition(barId: String): Int =
        barsFiltered.indexOfFirst { it.barMeta.id == barId }

    fun addItem(bar: Bar) {
        bars.add(bar)
        if (selectedDayOfWeek == -1 || !bar.deals.filter { it.daysOfWeek.contains(selectedDayOfWeek) }.isEmpty()) {
            barsFiltered.add(bar)
            notifyItemInserted(bars.lastIndex)
        }
    }

    fun removeItem(barToRemove: Bar) {
        var indexToRemove = barsFiltered.indexOfFirst { it.barMeta.id == barToRemove.barMeta.id }
        if (indexToRemove >= 0) {
            barsFiltered.removeAt(indexToRemove)
            notifyItemRemoved(indexToRemove)
        }
        bars.remove(barToRemove)
    }

    fun filterByDay(day: Int) {
        selectedDayOfWeek = day
        barsFiltered = bars.filter { !it.deals.filter { it.daysOfWeek.contains(day) }.isEmpty() } as MutableList<Bar>
        notifyDataSetChanged()
    }

    class BarHolder(val view: BarView) : RecyclerView.ViewHolder(view)
}