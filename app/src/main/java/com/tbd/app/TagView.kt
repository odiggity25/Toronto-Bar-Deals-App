package com.tbd.app

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.jakewharton.rxbinding2.view.clicks
import com.nex3z.flowlayout.FlowLayout

/**
 * Created by orrie on 2017-07-21.
 */
class TagView(context: Context, attrs: AttributeSet): FlowLayout(context, attrs) {

    val tagViews = mutableListOf<TextView>()
    val selectedTags = mutableListOf<String>()

    fun addTagsWithSelection(tags: List<Tag>) {
        tags.forEach { addTag(it) }
    }

    fun addTags(tags: List<String>) {
        tags.map { Tag(it, false) }
                .forEach { addTag(it) }
    }

    fun addTag(tag: Tag) {
        val tv = View.inflate(context, R.layout.deal_tag, null) as TextView
        tv.text = tag.name
        tagViews.add(tv)
        addView(tv)
        tv.clicks().subscribe {
            val tagName = tv.text.toString()
            if (selectedTags.contains(tagName)) {
                unselectTag(tagName, tv)
            } else {
                selectTag(tagName, tv)
            }
        }
        if (tag.selected) {
            selectTag(tag.name, tv)
        }
    }

    fun unselectAll() {
        selectedTags.clear()
        tagViews.forEach {
            it.isSelected = false
            it.setTextColor(context.resources.getColor(R.color.colorAccent))
        }
    }

    private fun unselectTag(tagName: String, tv: TextView) {
        selectedTags.remove(tagName)
        tv.isSelected = false
        tv.setTextColor(context.resources.getColor(R.color.colorAccent))
    }

    private fun selectTag(tagName: String, tv: TextView) {
        selectedTags.add(tagName)
        tv.isSelected = true
        tv.setTextColor(context.resources.getColor(R.color.white))
    }

    data class Tag(val name: String, val selected: Boolean)
}