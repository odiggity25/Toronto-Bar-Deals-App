package com.wattpad.tap.util

import android.view.View
import android.view.ViewTreeObserver

fun showStatusBar(view: View) {
    view.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
}

fun hideStatusBar(view: View) {
    view.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_FULLSCREEN
}

fun View.onNextLayout(runnable: () -> Unit) {
    val listener = object : View.OnLayoutChangeListener {
        override fun onLayoutChange(v: View,
                                    left: Int,
                                    top: Int,
                                    right: Int,
                                    bottom: Int,
                                    oldLeft: Int,
                                    oldTop: Int,
                                    oldRight: Int,
                                    oldBottom: Int) {
            removeOnLayoutChangeListener(this)
            v.post { runnable() }
        }
    }

    addOnLayoutChangeListener(listener)
}

fun View.beforeNextDraw(shouldCancel: Boolean, beforeDraw: () -> Unit) {
    val listener = object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            viewTreeObserver.removeOnPreDrawListener(this)
            beforeDraw()
            return shouldCancel
        }
    }

    viewTreeObserver.addOnPreDrawListener(listener)
}

fun View.setTopPadding(top: Int) {
    setPaddingRelative(paddingStart, top, paddingEnd, paddingBottom)
}
