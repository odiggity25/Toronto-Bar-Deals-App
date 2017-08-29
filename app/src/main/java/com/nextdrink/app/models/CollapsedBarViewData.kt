package com.nextdrink.app.models

import android.graphics.Bitmap

/**
 * Created by orrie on 2017-07-13.
 */
data class CollapsedBarViewData(val x: Float = 0f,
                             val y: Float = 0f,
                             val width: Int = 0,
                             val height: Int = 0,
                             val textSize: Float = 0f,
                             val barName: String = "",
                             val barImage: Bitmap? = null)