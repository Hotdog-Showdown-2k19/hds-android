package com.github.akassharjun.hotdogshowdown.presenter

import android.content.SharedPreferences

class ResultActivityPresenter(val view: View) {

    val PREFS_FILENAME = "hotdog.prefs"
    var prefs: SharedPreferences? = null

    fun getPreviousHotdogs(){

    }


    interface View {

    }
}