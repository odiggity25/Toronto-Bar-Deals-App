package com.nextdrink.app.utils.animation

import android.transition.Transition

/**
 * Use this if you only want to override some of the callbacks
 * Created by orrie on 2017-07-12.
 */
open class SimpleTransitionListener : Transition.TransitionListener {
    override fun onTransitionEnd(transition: Transition?) {}
    override fun onTransitionResume(transition: Transition?) {}
    override fun onTransitionPause(transition: Transition?) {}
    override fun onTransitionCancel(transition: Transition?) {}
    override fun onTransitionStart(transition: Transition?) {}
}