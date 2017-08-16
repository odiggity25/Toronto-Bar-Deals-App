package com.tbd.app.models

/**
 * Created by orrie on 2017-08-15.
 */
data class DealReport(val deal: Deal, val reason: String) {

    override fun toString(): String {
        return "Deal ${deal.id}: ${deal.description} was reported for '$reason' for bar ${deal.barId}"
    }
}