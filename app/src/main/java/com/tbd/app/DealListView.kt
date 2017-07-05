package com.tbd.app

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.tbd.app.models.BarDeals

/**
 * View that shows the BarDeals in a horizontal recyclerview
 * Created by orrie on 2017-07-04.
 */
class DealListView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs){
    private val recyclerView by lazy { findViewById(R.id.deal_list_recyclerview) as RecyclerView }

    init {
        View.inflate(context, R.layout.view_deal_list, this)
    }

    fun bind(barDealsList: MutableList<BarDeals>) {
        recyclerView.adapter = DealListAdapter(context, barDealsList)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }
}