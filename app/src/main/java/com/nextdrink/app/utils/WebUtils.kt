package com.nextdrink.app.utils

import android.content.Context
import android.content.Intent
import android.net.Uri


/**
 * Created by orrie on 2017-08-23.
 */
fun openExternalUrl(context: Context, url: String) {
    val i = Intent(Intent.ACTION_VIEW)
    i.data = Uri.parse(url)
    context.startActivity(i)
}