package com.tbd.app

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.jakewharton.rxbinding2.view.clicks
import com.nex3z.flowlayout.FlowLayout
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by orrie on 2017-07-21.
 */
class TagView(context: Context, attrs: AttributeSet): FlowLayout(context, attrs) {

    private val tagSelectsSubject = PublishSubject.create<String>()
    val tagSelects: Observable<String> = tagSelectsSubject.hide()
    private val tagUnselectsSubject = PublishSubject.create<String>()
    val tagUnselects: Observable<String> = tagUnselectsSubject.hide()

    val selectedTags = mutableListOf<String>()

    fun addTags(tags: List<String>) {
        tags.forEach { addTag(it) }
    }

    fun addTag(tag: String) {
        val tv = View.inflate(context, R.layout.deal_tag, null) as TextView
        tv.text = tag
        addView(tv)
        tv.clicks().subscribe {
            val tag = tv.text.toString()
            if (selectedTags.contains(tag)) {
                selectedTags.remove(tag)
                tagUnselectsSubject.onNext(tag)
                tv.isSelected = false
                tv.setTextColor(context.resources.getColor(R.color.colorAccent))
            } else {
                selectedTags.add(tag)
                tagSelectsSubject.onNext(tag)
                tv.isSelected = true
                tv.setTextColor(context.resources.getColor(R.color.white))
            }
        }
    }
}