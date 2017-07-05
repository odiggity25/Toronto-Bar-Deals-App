package com.tbd.app

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.tbd.app.models.BarDeals

/**
 * Created by orrie on 2017-07-04.
 */
class DealListAdapter(private val context: Context,
                      private val barDealsList: MutableList<BarDeals>) : RecyclerView.Adapter<DealListAdapter.DealPreviewHolder>() {
    override fun onBindViewHolder(holder: DealPreviewHolder?, position: Int) {
        holder?.view?.bind(barDealsList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DealPreviewHolder {
        return DealPreviewHolder(DealPreview(context))
    }

    override fun getItemCount(): Int {
        return barDealsList.size
    }

    class DealPreviewHolder(val view: DealPreview) : RecyclerView.ViewHolder(view)
}