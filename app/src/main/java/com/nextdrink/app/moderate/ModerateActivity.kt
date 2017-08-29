package com.nextdrink.app.moderate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class ModerateActivity : AppCompatActivity() {

    companion object {

        fun newIntent(context: Context): Intent =
            Intent(context, ModerateActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ModerateView(this))
    }
}
