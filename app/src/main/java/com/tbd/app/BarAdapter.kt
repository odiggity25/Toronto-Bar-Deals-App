package com.tbd.app

import android.content.Context
import android.graphics.BitmapFactory
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.tbd.app.apis.BarApi
import com.tbd.app.models.Bar
import com.tbd.app.utils.dpToPx
import com.tbd.app.utils.matchesFilter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

/**
 * Created by orrie on 2017-07-04.
 */
class BarAdapter(private val context: Context,
                 private val bars: MutableList<Bar>,
                 private var dealFilter: DealFilter,
                 private var itemHeight: Int = -1,
                 private val barApi: BarApi = BarApi()) : RecyclerView.Adapter<BarAdapter.BarHolder>() {

    private val barClicksSubject = PublishSubject.create<Pair<Bar, ConstraintLayout>>()
    val barClicks: Observable<Pair<Bar, ConstraintLayout>> = barClicksSubject.hide()
    private var barsFiltered = mutableListOf<Bar>()
    private val defaultBarImage = BitmapFactory.decodeResource(null, R.drawable.ic_local_bar)

    override fun onBindViewHolder(holder: BarHolder, position: Int) {
        val bar = barsFiltered[position]
        holder.view.bind(bar, dealFilter)
        if (bar.barMeta.image == null) {
            loadImage(bar)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarHolder {
        if (itemHeight < 0) {
            itemHeight = parent.height
        }
        // -36 dp so the next item peeks in
        val barHolder = BarHolder(BarPreview(context, parent.measuredWidth - dpToPx(36), itemHeight))
        barHolder.view.clicks().subscribe {
            val bar = barsFiltered[barHolder.adapterPosition]
            barClicksSubject.onNext(Pair(bar, barHolder.view.findViewById(R.id.collapsed_inside) as ConstraintLayout))
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
        loadImage(bar)
        if (!bar.deals.filter { it.matchesFilter(dealFilter) }.isEmpty()) {
            barsFiltered.add(bar)
            notifyItemInserted(barsFiltered.lastIndex)
        }
    }

    fun removeItem(barToRemove: Bar) {
        var indexToRemove = barsFiltered.indexOfFirst { it.barMeta.id == barToRemove.barMeta.id }
        if (indexToRemove >= 0) {
            barsFiltered.removeAt(indexToRemove)
            notifyItemRemoved(indexToRemove)
        }
        indexToRemove = bars.indexOfFirst { it.barMeta.id == barToRemove.barMeta.id }
        if (indexToRemove >= 0) {
            bars.removeAt(indexToRemove)
        }
    }

    fun updateItem(updatedBar: Bar) {
        val index = bars.indexOfFirst { it.barMeta.id == updatedBar.barMeta.id }
        if (index >= 0) {
            bars[index] = updatedBar
        }
        val filteredIndex = barsFiltered.indexOfFirst { it.barMeta.id == updatedBar.barMeta.id }
        if (filteredIndex >= 0) {
            barsFiltered[filteredIndex] = updatedBar
            notifyItemChanged(filteredIndex)
        }
    }

    fun filter(dealFilter: DealFilter) {
        this.dealFilter = dealFilter
        barsFiltered = bars.filter { !it.deals.filter { it.matchesFilter(dealFilter) }.isEmpty() } as MutableList<Bar>
        notifyDataSetChanged()
    }

    private fun loadImage(bar: Bar) {
        // TODO: Use LRU cache
        Observable.fromCallable { barApi.imageForBar(bar.barMeta.id) }
                .map {
                    bar.barMeta.image = it
                    bar
                }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    updateItem(it)
                }, {
                    bar.barMeta.image = defaultBarImage
                    updateItem(bar)
                    Timber.e("Failed to load image for bar: ${bar.barMeta.id} because ${it.message}")
                })
    }

    class BarHolder(val view: BarPreview) : RecyclerView.ViewHolder(view)
}