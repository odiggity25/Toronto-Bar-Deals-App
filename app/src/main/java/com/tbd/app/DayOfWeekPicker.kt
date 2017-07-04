package com.tbd.app

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.subjects.PublishSubject

/**
 * Created by orrie on 2017-06-21.
 */
class DayOfWeekPicker(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs){
    private val dayClicksSubject = PublishSubject.create<DaySelected>()
    val dayClicks = dayClicksSubject.hide()

    init {
        orientation = HORIZONTAL
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        View.inflate(context, R.layout.view_day_of_week_picker, this)
        val day0 = findViewById(R.id.day0) as TextView
        val day1 = findViewById(R.id.day1) as TextView
        val day2 = findViewById(R.id.day2) as TextView
        val day3 = findViewById(R.id.day3) as TextView
        val day4 = findViewById(R.id.day4) as TextView
        val day5 = findViewById(R.id.day5) as TextView
        val day6 = findViewById(R.id.day6) as TextView

        day0.clicks().subscribe {
            day0.isSelected = !day0.isSelected
            dayClicksSubject.onNext(DaySelected(0, day0.isSelected))
        }
        day1.clicks().subscribe {
            day1.isSelected = !day1.isSelected
            dayClicksSubject.onNext(DaySelected(1, day1.isSelected))
        }
        day2.clicks().subscribe {
            day2.isSelected = !day2.isSelected
            dayClicksSubject.onNext(DaySelected(2, day2.isSelected))
        }
        day3.clicks().subscribe {
            day3.isSelected = !day3.isSelected
            dayClicksSubject.onNext(DaySelected(3, day3.isSelected))
        }
        day4.clicks().subscribe {
            day4.isSelected = !day4.isSelected
            dayClicksSubject.onNext(DaySelected(4, day4.isSelected))
        }
        day5.clicks().subscribe {
            day5.isSelected = !day5.isSelected
            dayClicksSubject.onNext(DaySelected(5, day5.isSelected))
        }
        day6.clicks().subscribe {
            day6.isSelected = !day6.isSelected
            dayClicksSubject.onNext(DaySelected(6, day6.isSelected))
        }
    }

    data class DaySelected(val day: Int, val selected: Boolean)
}