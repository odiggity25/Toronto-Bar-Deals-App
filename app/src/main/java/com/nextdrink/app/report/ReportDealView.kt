package com.nextdrink.app.report

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.view.View
import android.widget.TextView
import com.nextdrink.app.R
import com.nextdrink.app.models.Deal

/**
 * Created by orrie on 2017-08-07.
 */
class ReportDealView(context: Context, deal: Deal): ConstraintLayout(context) {

    init {
        View.inflate(context, R.layout.view_report_deal, this)
        (findViewById(R.id.report_deal_title) as TextView).text = deal.description
        setBackgroundColor(context.resources.getColor(R.color.white))
    }
}