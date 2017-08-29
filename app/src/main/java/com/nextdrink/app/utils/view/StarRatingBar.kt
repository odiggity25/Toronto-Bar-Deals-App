package com.nextdrink.app.utils.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.nextdrink.app.R
import com.nextdrink.app.utils.dpToPx

/**
 * Created by orrie on 2017-08-23.
 */
/**
 * This draws stars and half stars in a horizontal linear layout to match the number of stars passed in in the constructor
 */
class StarRatingBar(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    fun bind(stars: Double) {
        val rating = stars
        val wholeStars = Math.floor(rating).toInt()
        val halfStar = rating % 1 > 0
        for (i in 1..5) {
            val starView = ImageView(getContext())
            starView.layoutParams = ViewGroup.LayoutParams(dpToPx(15), dpToPx(15))
            if (i <= wholeStars) {
                starView.setImageDrawable(resources.getDrawable(R.drawable.ic_filled_star))
            } else if (i == wholeStars + 1 && halfStar) {
                starView.setImageDrawable(resources.getDrawable(R.drawable.ic_half_filled_star))
            } else {
                starView.setImageDrawable(resources.getDrawable(R.drawable.ic_empty_star))
            }
            addView(starView)
        }
    }
}