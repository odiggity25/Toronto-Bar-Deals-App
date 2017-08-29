package com.nextdrink.app

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.detaches
import io.reactivex.Observable

/**
 * A view that allows the user to choose the filters they want to apply to the deals they see
 * Created by orrie on 2017-07-26.
 */
class DealFiltersView(context: Context, val dealFilter: DealFilter): ConstraintLayout(context) {

    private val tagView by lazy { findViewById(R.id.filter_deal_tags) as TagView }
    private val presenter: DealFiltersPresenter
    val closes: Observable<DealFilter> by lazy { findViewById(R.id.deal_filters_closes).clicks()
            .map {
                dealFilter.tags = tagView.selectedTags
                dealFilter
            }
    }
    val resetFiltersClicks by lazy { findViewById(R.id.deal_filters_resets).clicks() }

    init {
        View.inflate(context, R.layout.view_deals_filters, this)
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        presenter = DealFiltersPresenter(this, detaches())
        isClickable = true
        setBackgroundResource(R.color.white)
    }

    fun addTags(tags: List<String>) {
        tagView.addTagsWithSelection(tags.map { TagView.Tag(it, dealFilter.tags.contains(it)) })
    }

    fun resetFilters() {
        tagView.unselectAll()
    }
}