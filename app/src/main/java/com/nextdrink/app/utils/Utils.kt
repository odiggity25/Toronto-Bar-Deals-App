package com.nextdrink.app.utils

import android.content.res.Resources

fun pxToDp(px: Int): Int =
        (px / Resources.getSystem().displayMetrics.density).toInt()

fun dpToPx(dp: Int): Int =
        ((dp * Resources.getSystem().displayMetrics.density).toInt())
