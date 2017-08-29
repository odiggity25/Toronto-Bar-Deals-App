package com.nextdrink.app

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.jakewharton.rxbinding2.view.clicks
import com.nextdrink.app.utils.dayOfWeekAsInt
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject



/**
 * Created by orrie on 2017-06-21.
 */
class DayOfWeekPicker(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs){
    private val dayClicksSubject = PublishSubject.create<DaySelected>()
    val dayClicks: Observable<DaySelected> = dayClicksSubject.hide()
    private val multiSelect: Boolean
    private val nowOption: Boolean
    private var previousSelected: TextView? = null

    val dayNow by lazy { findViewById(R.id.day_now) as TextView }
    val day0 by lazy { findViewById(R.id.day0) as TextView }
    val day1 by lazy { findViewById(R.id.day1) as TextView }
    val day2 by lazy { findViewById(R.id.day2) as TextView }
    val day3 by lazy { findViewById(R.id.day3) as TextView }
    val day4 by lazy { findViewById(R.id.day4) as TextView }
    val day5 by lazy { findViewById(R.id.day5) as TextView }
    val day6 by lazy { findViewById(R.id.day6) as TextView }

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.DayOfWeekPicker, 0, 0)
        try {
            multiSelect = a.getBoolean(R.styleable.DayOfWeekPicker_multiSelect, true)
            nowOption = a.getBoolean(R.styleable.DayOfWeekPicker_nowOption, false)
        } finally {
            a.recycle()
        }

        orientation = HORIZONTAL
        View.inflate(context, R.layout.view_day_of_week_picker, this)

        if (nowOption) {
            dayNow.visibility = View.VISIBLE
        }
        dayNow.clicks().subscribe {
            dayNow.isSelected = !dayNow.isSelected
            dayClicksSubject.onNext(DaySelected(dayOfWeekAsInt(), dayNow.isSelected, true))
            unselectPrevious(dayNow)
        }
        day0.clicks().subscribe {
            day0.isSelected = !day0.isSelected
            dayClicksSubject.onNext(DaySelected(0, day0.isSelected))
            unselectPrevious(day0)
        }
        day1.clicks().subscribe {
            day1.isSelected = !day1.isSelected
            dayClicksSubject.onNext(DaySelected(1, day1.isSelected))
            unselectPrevious(day1)
        }
        day2.clicks().subscribe {
            day2.isSelected = !day2.isSelected
            dayClicksSubject.onNext(DaySelected(2, day2.isSelected))
            unselectPrevious(day2)
        }
        day3.clicks().subscribe {
            day3.isSelected = !day3.isSelected
            dayClicksSubject.onNext(DaySelected(3, day3.isSelected))
            unselectPrevious(day3)
        }
        day4.clicks().subscribe {
            day4.isSelected = !day4.isSelected
            dayClicksSubject.onNext(DaySelected(4, day4.isSelected))
            unselectPrevious(day4)
        }
        day5.clicks().subscribe {
            day5.isSelected = !day5.isSelected
            dayClicksSubject.onNext(DaySelected(5, day5.isSelected))
            unselectPrevious(day5)
        }
        day6.clicks().subscribe {
            day6.isSelected = !day6.isSelected
            dayClicksSubject.onNext(DaySelected(6, day6.isSelected))
            unselectPrevious(day6)
        }
    }

    fun setInitialDay(day: Int) {
            when(day) {
                0 -> {
                    day0.isSelected = true
                    unselectPrevious(day0)
                }
                1 -> {
                    day1.isSelected = true
                    unselectPrevious(day1)
                }
                2 ->  {
                    day2.isSelected = true
                    unselectPrevious(day2)
                }
                3 -> {
                    day3.isSelected = true
                    unselectPrevious(day3)
                }
                4 -> {
                    day4.isSelected = true
                    unselectPrevious(day4)
                }
                5 -> {
                    day5.isSelected = true
                    unselectPrevious(day5)
                }
                6 -> {
                    day6.isSelected = true
                    unselectPrevious(day6)
                }
                7 -> {
                    dayNow.isSelected = true
                    unselectPrevious(dayNow)
                }
            }
        }

    private fun unselectPrevious(view: TextView) {
        previousSelected?.let {
            if (!multiSelect && it != view) {
                it.isSelected = false
            }
        }
        previousSelected = view
    }

    data class DaySelected(val day: Int, val selected: Boolean, val now: Boolean = false)
}