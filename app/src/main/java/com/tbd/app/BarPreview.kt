package com.tbd.app

import android.content.Context
import android.support.v7.widget.CardView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.tbd.app.models.Bar
import com.tbd.app.utils.dpToPx
import java.text.SimpleDateFormat
import java.util.*

/**
 * Shows a preview of the barMeta and its deals in a card form
 * Created by orrie on 2017-07-04.
 */
class BarPreview(context: Context, width: Int, height: Int) : CardView(context) {
    val barName by lazy { findViewById(R.id.bar_name_shared) as TextView }
    val dealsDescription by lazy { findViewById(R.id.bar_preview_deals) as TextView}
    val barImage by lazy { findViewById(R.id.bar_image_shared) as ImageView }
    init {
        View.inflate(context, R.layout.view_bar_preview, this)
        layoutParams = LayoutParams(width, height)
        radius = dpToPx(6).toFloat()
        useCompatPadding = true
        isClickable = true
    }

    fun bind(bar: Bar) {
        barName.text = bar.barMeta.name
        var dealText = ""
        bar.deals.forEach {
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
        bar.barMeta.image?.let {
            barImage.setImageBitmap(it)
        }
    }
}