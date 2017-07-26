package com.tbd.app.utils.animation

import android.os.Build
import android.transition.Fade
import android.transition.Slide
import android.transition.Transition

/**
 * Created by orrie on 2017-07-26.
 */
fun slideTransitionCompat(gravity: Int): Transition =
    if (Build.VERSION.SDK_INT >= 21) {
        Slide(gravity)
    } else {
        Fade()
    }