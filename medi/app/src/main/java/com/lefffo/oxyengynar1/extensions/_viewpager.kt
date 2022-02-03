package com.lefffo.oxyengynar1.extensions

import android.animation.ValueAnimator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import androidx.viewpager2.widget.ViewPager2

inline fun ViewPager2.onPageChanged(
    crossinline listener: (position: Int) -> Unit
) {
    val callback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            listener(position)
        }
    }
    doOnAttach {
        registerOnPageChangeCallback(callback)
    }
    doOnDetach {
        unregisterOnPageChangeCallback(callback)
    }
}

fun ViewPager2.enableEndlessScroll() {
    val callback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            if (state == ViewPager2.SCROLL_STATE_IDLE || state == ViewPager2.SCROLL_STATE_DRAGGING) {
                val itemCount = adapter?.itemCount ?: return
                if (currentItem == 0) {
                    setCurrentItem(itemCount - 2, false)
                } else if (currentItem == itemCount - 1) {
                    setCurrentItem(1, false)
                }
            }
        }
    }
    doOnAttach {
        registerOnPageChangeCallback(callback)
    }
    doOnDetach {
        unregisterOnPageChangeCallback(callback)
    }
}

fun ViewPager2.smoothScrollTo(position: Int) {
    val target = resources.displayMetrics.widthPixels.toFloat() * (position - currentItem)
    var previousValue = 0f
    val animator = ValueAnimator.ofFloat(0f, target)
        .apply {
            duration = 500L

            doOnStart {
                beginFakeDrag()
            }
            doOnEnd {
                endFakeDrag()
                setCurrentItem(position, false)
            }
            addUpdateListener {
                val currentValue = (it.animatedValue as Float) - previousValue
                fakeDragBy(-currentValue)
                previousValue = it.animatedValue as Float
            }

            start()
        }
    doOnDetach { animator.cancel() }
}