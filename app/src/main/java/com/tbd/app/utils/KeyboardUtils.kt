package com.tbd.app.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

/**
 * Utilities for working with the soft keyboard
 *
 * @author orrie
 */

fun hideKeyboard(activity: Activity) {
    activity.currentFocus?.let { view ->
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

/**
 * Note: This assumes that the given View has focus
 */
fun hideKeyboard(v: TextView) {
    val context = v.context
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(v.windowToken, 0)
}
