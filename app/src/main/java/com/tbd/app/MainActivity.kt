package com.tbd.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var mainView: MainView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainView = MainView(this, supportFragmentManager)
        setContentView(mainView)
    }

    override fun onBackPressed() {
        if (!mainView.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            MainPresenter.REQUEST_GET_FINE_LOCATION_PERMISSION -> mainView.mainPresenter.setMapLocation()
        }
    }
}
