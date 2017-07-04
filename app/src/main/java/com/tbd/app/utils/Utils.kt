package com.wattpad.tap.util

import android.content.res.Resources

fun pxToDp(px: Int): Int =
        (px / Resources.getSystem().displayMetrics.density).toInt()

fun dpToPx(dp: Int): Int =
        ((dp * Resources.getSystem().displayMetrics.density).toInt())