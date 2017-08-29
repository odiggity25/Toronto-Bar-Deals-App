package com.nextdrink.app.utils.view

import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import java.util.concurrent.TimeUnit

/**
 * Created by orrie on 2017-06-21.
 */

fun View.throttleClicks(): io.reactivex.Observable<kotlin.Unit> =
    this.clicks().throttleFirst(400, TimeUnit.MILLISECONDS)