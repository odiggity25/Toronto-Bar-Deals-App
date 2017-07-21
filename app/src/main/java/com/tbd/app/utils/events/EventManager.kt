package com.tbd.app.utils.events

import com.tbd.app.BuildConfig
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException

/**
 * Created by orrie on 2017-07-10.
 */
class EventManager(private var client: OkHttpClient = OkHttpClient()) {

    private val SLACK_WEBHOOK_URL = "https://hooks.slack.com/services/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"
    val JSON: MediaType = MediaType.parse("application/json; charset=utf-8")!!

    fun sendSlackEvent(message: String) {
        if (BuildConfig.DEBUG) {
            Timber.i("Not sending slack event because debug build")
            return
        }
        val messageObject = JSONObject()
        try {
            messageObject.put("text", message)
        } catch (e: JSONException) {
            Timber.e("Failed to create slack json: " + e.message)
            return
        }


        val body = RequestBody.create(JSON, messageObject.toString())
        val request = Request.Builder()
                .url(SLACK_WEBHOOK_URL)
                .post(body)
                .build()
        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call?, e: IOException) {
                Timber.e("Failed to post to slack ${e.message}")
            }

            override fun onResponse(call: Call?, response: Response?) {}
        })
    }
}