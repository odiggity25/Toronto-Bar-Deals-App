package com.tbd.app

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.tbd.app.models.BarDeals
import java.text.SimpleDateFormat
import java.util.*

/**
 * Shows a preview of the bar and its deals in a card form
 * Created by orrie on 2017-07-04.
 */
class DealPreview(context: Context) : LinearLayout(context) {
    val barName by lazy { findViewById(R.id.deal_preview_bar_name) as TextView }
    val dealsDescription by lazy { findViewById(R.id.deal_preview_deals) as TextView}
    init {
        View.inflate(context, R.layout.view_deal_preview, this)
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        orientation = HORIZONTAL
    }

    fun bind(barDeals: BarDeals) {
        barName.text = barDeals.bar.name
        var dealText = ""
        barDeals.deals.forEach {
            if (it.allDay) {
                dealText = dealText.plus("All day")
            } else {
                val startTime = it.startTime?.let { SimpleDateFormat("h:mm a").format(Date(it)) }
                val endTime = it.endTime?.let { SimpleDateFormat("h:mm a").format(Date(it)) }
                dealText = dealText.plus(startTime?.replace(":00", ""))
                        .plus(" - ")
                        .plus(endTime?.replace(":00", ""))
            }
            dealText = dealText.plus(": ")
                    .plus(it.description)
                    .plus("\n")
        }
        dealsDescription.text = dealText
    }
}