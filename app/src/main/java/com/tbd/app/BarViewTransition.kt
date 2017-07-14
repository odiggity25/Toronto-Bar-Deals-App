package com.tbd.app

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.transition.*
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.tbd.app.models.CollapsedBarViewData
import com.tbd.app.utils.animation.SimpleTransitionListener
import com.tbd.app.utils.animation.TextResizeTransition
import com.tbd.app.utils.animation.TranslationTransition

/**
 * Animates transitioning from the bar preview below the map to a full screen [BarView]
 * Created by orrie on 2017-07-14.
 */
class BarViewTransition(private val context: Context,
                        private val parentView: MainView,
                        private val barView: ConstraintLayout,
                        private val barPreview: ConstraintLayout,
                        private val collapsedData: CollapsedBarViewData) {

    fun transition(expandBarView: Boolean) {
        val fadeBarPreviewTransition = Fade()
                .addTarget(barPreview.findViewById(R.id.bar_preview_deals))
                .setDuration(100)

        val fadeBarViewTransition = Fade().addTarget(barView).setDuration(1)

        val changeBoundsTransition = ChangeBounds()
                .addListener(object : SimpleTransitionListener() {
                    override fun onTransitionEnd(transition: Transition?) {
                        if (!expandBarView) {
                            parentView.removeView(barView)
                        }
                    }

                })
        val translateTransition = TranslationTransition().addTarget(barView)
        val textResizeTransition = TextResizeTransition().addTarget(barView.findViewById(R.id.bar_name_shared))
        val mainTransition = TransitionSet()
                .addTransition(changeBoundsTransition)
                .addTransition(textResizeTransition)
                .addTransition(translateTransition)

        val transitionsOrdered = if (expandBarView) {
            TransitionSet()
                    .addTransition(fadeBarViewTransition)
                    .addTransition(mainTransition)
                    .setOrdering(TransitionSet.ORDERING_SEQUENTIAL)
        } else {
            TransitionSet()
                    .addTransition(mainTransition)
                    .addTransition(fadeBarViewTransition)
                    .addTransition(fadeBarPreviewTransition)
                    .setOrdering(TransitionSet.ORDERING_SEQUENTIAL)
        }

        TransitionManager.beginDelayedTransition(parentView, transitionsOrdered)

        barPreview.findViewById(R.id.bar_preview_deals)?.visibility = if (expandBarView) View.INVISIBLE else View.VISIBLE

        val expandedConstraint = ConstraintSet()
        val collapsedConstraint = ConstraintSet()
        expandedConstraint.clone(BarView(context))
        collapsedConstraint.clone(context, R.layout.view_bar_collapsed)
        val constraint = if (expandBarView) expandedConstraint else collapsedConstraint
        constraint.applyTo(barView)

        barView.visibility = if (expandBarView) View.VISIBLE else View.INVISIBLE

        val params = barView.layoutParams as FrameLayout.LayoutParams
        params.width = if (expandBarView) ViewGroup.LayoutParams.MATCH_PARENT else collapsedData.width
        params.height = if (expandBarView) ViewGroup.LayoutParams.MATCH_PARENT else collapsedData.height
        barView.layoutParams = params
        barView.x = if (expandBarView) 0f else collapsedData.x
        barView.y = if (expandBarView) 0f else collapsedData.y
        (barView.findViewById(R.id.bar_name_shared) as TextView).textSize = if (expandBarView) 36f else collapsedData.textSize
    }
}