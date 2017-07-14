package com.tbd.app.utils.animation

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.support.constraint.ConstraintLayout
import android.transition.Transition
import android.transition.TransitionValues
import android.util.FloatProperty
import android.view.View
import android.view.ViewGroup


/**
 * Created by orrie on 2017-07-12.
 */
class TranslationTransition : Transition() {

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    private fun captureValues(transitionValues: TransitionValues) {
        // save current progress in the values map
        if (transitionValues.view is ConstraintLayout) {
            transitionValues.values.put(TRANSLATION_X_PROGRESS, transitionValues.view.x)
            transitionValues.values.put(TRANSLATION_Y_PROGRESS, transitionValues.view.y)
        }
    }

    override fun createAnimator(sceneRoot: ViewGroup, startValues: TransitionValues?,
                                endValues: TransitionValues?): Animator? {
        if (startValues != null && endValues != null) {
            val view = endValues.view
            val startX = startValues.values[TRANSLATION_X_PROGRESS] as Float
            val endX = endValues.values[TRANSLATION_X_PROGRESS] as Float
            val startY = startValues.values[TRANSLATION_Y_PROGRESS] as Float
            val endY = endValues.values[TRANSLATION_Y_PROGRESS] as Float
            if (startX != endX || startY != endY) {
                // first of all we need to apply the start value, because right now
                // the view has end value
                view.x = startX
                view.y = startY
                // create animator with our progressBar, property and end value
                val phx = PropertyValuesHolder.ofFloat(TRANSLATION_X_PROPERTY, endX)
                val phy = PropertyValuesHolder.ofFloat(TRANSLATION_Y_PROPERTY, endY)
                return ObjectAnimator.ofPropertyValuesHolder(view, phx, phy)
            }
        }
        return null
    }

    companion object {
        private val TRANSLATION_X_PROGRESS = "TranslationTransition:progressX"
        private val TRANSLATION_Y_PROGRESS = "TranslationTransition:progressY"

        private val TRANSLATION_Y_PROPERTY = object : FloatProperty<View>(TRANSLATION_Y_PROGRESS) {

            override fun setValue(view: View, value: Float) {
                view.y = value
            }

            override fun get(view: View): Float? {
                return view.y
            }
        }

        private val TRANSLATION_X_PROPERTY = object : FloatProperty<View>(TRANSLATION_X_PROGRESS) {

            override fun setValue(view: View, value: Float) {
                view.x = value
            }

            override fun get(view: View): Float? {
                return view.x
            }
        }

    }
}