package com.tbd.app

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.tbd.app.models.BarDeals
import com.tbd.app.utils.dpToPx
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by orrie on 2017-07-04.
 */
class DealListAdapter(private val context: Context,
                      private val barDealsList: MutableList<BarDeals>) : RecyclerView.Adapter<DealListAdapter.DealPreviewHolder>() {

    private val barClicksSubject = PublishSubject.create<String>()
    val barClicks: Observable<String> = barClicksSubject.hide()

    override fun onBindViewHolder(holder: DealPreviewHolder?, position: Int) {
        holder?.view?.bind(barDealsList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealPreviewHolder {
        // -30 so the next item peeks in
        val dealPreviewHolder = DealPreviewHolder(DealPreview(context, parent.measuredWidth - dpToPx(30)))
        dealPreviewHolder.view.clicks().subscribe {
            val barId = barDealsList[dealPreviewHolder.adapterPosition].bar.id
            barClicksSubject.onNext(barId)
        }
        return dealPreviewHolder
    }

    override fun getItemCount(): Int {
        return barDealsList.size
    }

    fun getItem(position: Int): BarDeals =
            barDealsList[position]

    fun getPosition(barId: String): Int =
        barDealsList.indexOfFirst { it.bar.id == barId }

    fun addItem(barDeals: BarDeals) {
        barDealsList.add(barDeals)
        notifyItemInserted(barDealsList.lastIndex)
    }

    fun removeItem(barDealsToRemove: BarDeals) {
        var indexToRemove = barDealsList.indexOfFirst { it.bar.id == barDealsToRemove.bar.id }
        if (indexToRemove >= 0) {
            barDealsList.removeAt(indexToRemove)
            notifyItemRemoved(indexToRemove)
        }
    }

    class DealPreviewHolder(val view: DealPreview) : RecyclerView.ViewHolder(view)
}