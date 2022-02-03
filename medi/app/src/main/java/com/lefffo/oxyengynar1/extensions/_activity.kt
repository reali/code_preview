package com.lefffo.oxyengynar1.extensions

import android.app.Activity
import android.view.View

fun Activity.enableFullScreen() {
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
}