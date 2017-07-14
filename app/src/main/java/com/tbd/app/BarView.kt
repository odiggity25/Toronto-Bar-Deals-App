package com.tbd.app

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.tbd.app.models.Bar
import com.tbd.app.models.CollapsedBarViewData

/**
 * Created by orrie on 2017-07-10.
 */
class BarView : ConstraintLayout {

    var bar: Bar? = null

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.view_bar, this)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        View.inflate(context, R.layout.view_bar, this)
    }

    constructor(context: Context, id: Int, bar: Bar, collapsedData: CollapsedBarViewData) : super(context) {
        this.id = id
        this.bar = bar
        View.inflate(context, R.layout.view_bar, this)
        setBackgroundResource(R.color.white)

        layoutParams = FrameLayout.LayoutParams(collapsedData.width, collapsedData.height)
        x = collapsedData.x
        y = collapsedData.y
        visibility = View.INVISIBLE

        val collapsedConstraint = ConstraintSet()
        collapsedConstraint.clone(context, R.layout.view_bar_collapsed)
        setConstraintSet(collapsedConstraint)

        (findViewById(R.id.bar_image_shared) as ImageView).setImageBitmap(collapsedData.barImage)
        (findViewById(R.id.bar_name_shared) as TextView).text = collapsedData.barName
    }

}