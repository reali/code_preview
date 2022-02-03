package com.lefffo.oxyengynar1.extensions

import android.view.WindowManager
import androidx.fragment.app.Fragment
import timber.log.Timber

fun Fragment.enableKeepScreenOn() {
    Timber.tag("Fragment").d("enable keep screen on")
    requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}

fun Fragment.disableKeepScreenOn() {
    Timber.tag("Fragment").d("disable keep screen on")
    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}