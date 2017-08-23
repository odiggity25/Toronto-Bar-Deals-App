package com.tbd.app.utils

import com.tbd.app.models.Bar
import java.net.URLEncoder

/**
 * Created by orrie on 2017-08-23.
 */
fun Bar.googleMapUrl(): String {
    val address = URLEncoder.encode(this.barMeta.address, "UTF-8")
    return "https://www.google.com/maps/search/?api=1&query=$address&query_place_id=${this.barMeta.id}"
}