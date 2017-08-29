package com.nextdrink.app

/**
 * Created by orrie on 2017-07-22.
 */
class DealFilter(var daysOfWeek: List<Int> = mutableListOf(),
                 var tags: List<String> = mutableListOf(),
                 var now: Boolean = false)